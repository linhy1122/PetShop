package com.petshop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.*;
import com.petshop.mapper.OrderLogMapper;
import com.petshop.mapper.OrderMapper;
import com.petshop.service.CartService;
import com.petshop.service.OrderItemService;
import com.petshop.service.OrderService;
import com.petshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现
 * 订单状态机：
 *   0(待支付) → 1(待发货) → 2(待收货) → 3(待评价) → 4(已完成)
 *   ↓                ↓
 *  -1(已取消)       -2(退单中) → -3(退单通过) / 退回原状态
 *                 -4(管理员退单)
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderLogMapper orderLogMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, String remark) {
        // 获取购物车中选中的商品
        List<Cart> cartItems = cartService.getCartEntities(userId);
        List<Cart> checkedItems = cartItems.stream()
                .filter(c -> c.getChecked() == 1)
                .toList();

        if (checkedItems.isEmpty()) {
            throw new RuntimeException("购物车中没有选中的商品");
        }

        // 计算总金额 + 校验商品可购买
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : checkedItems) {
            Product product = productService.getById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品已下架：" + cart.getProductId());
            }
            // 宠物类商品唯一性校验
            if (product.getProductType() == 1 && product.getStock() == 0) {
                throw new RuntimeException("该宠物已被其他用户购买：" + product.getName());
            }
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(0);
        order.setRemark(remark);

        save(order);

        // 记录日志
        saveOrderLog(order.getId(), "用户", null, 0, "提交订单");

        // 创建订单项
        for (Cart cart : checkedItems) {
            Product product = productService.getById(cart.getProductId());
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setQuantity(cart.getQuantity());
            item.setPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItemService.save(item);

            // 扣减库存
            productService.deductStock(product.getId(), cart.getQuantity());

            // 从购物车移除
            cartService.removeById(cart.getId());
        }

        return order;
    }

    @Override
    @Transactional
    public void payOrder(Long orderId, String payMethod) {
        Order order = getById(orderId);
        validateStatus(order, 0);
        int fromStatus = order.getStatus();
        order.setStatus(1);
        order.setPayMethod(payMethod);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
        saveOrderLog(orderId, "用户", fromStatus, 1, "支付成功，方式：" + payMethod);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason, String cancelType, boolean isAutoCancel) {
        Order order = getById(orderId);
        validateStatus(order, 0, 1);
        int fromStatus = order.getStatus();
        order.setStatus(-1);
        String cancelMsg = isAutoCancel ? "超时未支付自动取消" : reason;
        order.setCancelReason(cancelMsg);
        order.setCancelType(cancelType);
        updateById(order);

        String logRemark = isAutoCancel ? "系统自动取消（超时未支付）" : cancelMsg;
        saveOrderLog(orderId, cancelType, fromStatus, -1, logRemark);

        // 恢复库存
        List<OrderItem> items = orderItemService.getByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                if (product.getProductType() == 1) {
                    // 宠物商品：恢复上架
                    product.setStatus(1);
                    product.setStock(1);
                } else {
                    product.setStock(product.getStock() + item.getQuantity());
                }
                productService.updateById(product);
            }
        }
    }

    @Override
    @Transactional
    public void deliverOrder(Long orderId, String logisticsCompany, String logisticsNo) {
        Order order = getById(orderId);
        validateStatus(order, 1);
        int fromStatus = order.getStatus();
        order.setStatus(2);
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        order.setDeliveryTime(LocalDateTime.now());
        updateById(order);
        saveOrderLog(orderId, "管理员", fromStatus, 2,
                "发货：" + logisticsCompany + " " + logisticsNo);
    }

    @Override
    @Transactional
    public void confirmReceive(Long orderId) {
        Order order = getById(orderId);
        validateStatus(order, 2);
        int fromStatus = order.getStatus();
        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        updateById(order);
        saveOrderLog(orderId, "用户", fromStatus, 3, "确认收货");
    }

    @Override
    @Transactional
    public void applyRefund(Long orderId, String reason) {
        Order order = getById(orderId);
        validateStatus(order, 2, 3);
        int fromStatus = order.getStatus();
        order.setStatus(-2);
        order.setPreviousStatus(fromStatus);  // 保存原状态，审核拒绝后恢复
        order.setRefundReason(reason);
        updateById(order);
        saveOrderLog(orderId, "用户", fromStatus, -2, "申请退单：" + reason);
    }

    @Override
    @Transactional
    public void auditRefund(Long orderId, boolean approved, String auditRemark) {
        Order order = getById(orderId);
        if (order.getStatus() != -2) {
            throw new RuntimeException("当前状态不可审核退单");
        }
        int fromStatus = order.getStatus();

        if (approved) {
            order.setStatus(-3);
            order.setAuditRemark(auditRemark);
            order.setRefundMoney(order.getPayAmount());
            order.setRefundTime(LocalDateTime.now());
            updateById(order);
            saveOrderLog(orderId, "管理员", fromStatus, -3,
                    "退单审核通过，退款金额：¥" + order.getPayAmount());

            // 恢复库存
            List<OrderItem> items = orderItemService.getByOrderId(orderId);
            for (OrderItem item : items) {
                Product product = productService.getById(item.getProductId());
                if (product != null) {
                    if (product.getProductType() == 1) {
                        product.setStatus(1);
                        product.setStock(1);
                    } else {
                        product.setStock(product.getStock() + item.getQuantity());
                    }
                    productService.updateById(product);
                }
            }
        } else {
            // 恢复到申请退单前的状态
            Integer prevStatus = order.getPreviousStatus();
            if (prevStatus == null || (prevStatus != 2 && prevStatus != 3)) {
                prevStatus = 2; // 兜底
            }
            order.setStatus(prevStatus);
            order.setAuditRemark(auditRemark);
            order.setPreviousStatus(null); // 清除
            updateById(order);
            saveOrderLog(orderId, "管理员", fromStatus, prevStatus,
                    "退单审核拒绝" + (auditRemark != null ? "：" + auditRemark : ""));
        }
    }

    @Override
    @Transactional
    public void adminRefund(Long orderId, String reason) {
        Order order = getById(orderId);
        if (order.getStatus() != 3) { // 只有已收货的才能管理员退单
            throw new RuntimeException("当前状态不可退单");
        }
        int fromStatus = order.getStatus();
        order.setStatus(-4);
        order.setCancelReason(reason);
        order.setCancelType("ADMIN");
        order.setRefundReason(reason);
        order.setRefundMoney(order.getPayAmount());
        order.setRefundTime(LocalDateTime.now());
        updateById(order);

        saveOrderLog(orderId, "管理员", fromStatus, -4, "管理员直接退单：" + reason);

        // 恢复库存
        List<OrderItem> items = orderItemService.getByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                if (product.getProductType() == 1) {
                    product.setStatus(1);
                    product.setStock(1);
                } else {
                    product.setStock(product.getStock() + item.getQuantity());
                }
                productService.updateById(product);
            }
        }
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = getById(orderId);
        validateStatus(order, 3);
        int fromStatus = order.getStatus();
        order.setStatus(4);
        order.setCommentTime(LocalDateTime.now());
        order.setFinishTime(LocalDateTime.now());
        updateById(order);
        saveOrderLog(orderId, "用户", fromStatus, 4, "评价完成，订单已完成");
    }

    @Override
    public List<OrderLog> getOrderLogs(Long orderId) {
        LambdaQueryWrapper<OrderLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderLog::getOrderId, orderId)
               .orderByAsc(OrderLog::getCreateTime);
        return orderLogMapper.selectList(wrapper);
    }

    /** 校验订单状态 */
    private void validateStatus(Order order, Integer... validStatuses) {
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        for (Integer s : validStatuses) {
            if (order.getStatus().equals(s)) return;
        }
        throw new RuntimeException("当前订单状态不允许此操作，状态：" + order.getStatus());
    }

    /** 保存订单操作日志 */
    private void saveOrderLog(Long orderId, String operator, Integer fromStatus, Integer toStatus, String remark) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOperator(operator);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setRemark(remark);
        orderLogMapper.insert(log);
    }
}
