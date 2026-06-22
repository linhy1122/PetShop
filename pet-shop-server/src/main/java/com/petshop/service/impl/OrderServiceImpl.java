package com.petshop.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.*;
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

    @Override
    @Transactional
    public Order createOrder(Long userId, Long addressId, String remark) {
        // 获取购物车中选中的商品
        List<Cart> cartItems = cartService.getUserCart(userId);
        List<Cart> checkedItems = cartItems.stream()
                .filter(c -> c.getChecked() == 1)
                .toList();

        if (checkedItems.isEmpty()) {
            throw new RuntimeException("购物车中没有选中的商品");
        }

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : checkedItems) {
            Product product = productService.getById(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品已下架：" + cart.getProductId());
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
        order.setStatus(1);
        order.setPayMethod(payMethod);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason, boolean isAutoCancel) {
        Order order = getById(orderId);
        validateStatus(order, 0, 1);
        order.setStatus(-1);
        String cancelMsg = isAutoCancel ? "超时未支付自动取消" : reason;
        order.setCancelReason(cancelMsg);
        updateById(order);
        // TODO: 恢复库存
    }

    @Override
    @Transactional
    public void deliverOrder(Long orderId, String logisticsCompany, String logisticsNo) {
        Order order = getById(orderId);
        validateStatus(order, 1);
        order.setStatus(2);
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        order.setDeliveryTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void confirmReceive(Long orderId) {
        Order order = getById(orderId);
        validateStatus(order, 2);
        order.setStatus(3);
        order.setReceiveTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void applyRefund(Long orderId, String reason) {
        Order order = getById(orderId);
        validateStatus(order, 2, 3);
        order.setStatus(-2);
        order.setCancelReason(reason);
        updateById(order);
    }

    @Override
    @Transactional
    public void auditRefund(Long orderId, boolean approved) {
        Order order = getById(orderId);
        if (order.getStatus() != -2) {
            throw new RuntimeException("当前状态不可审核退单");
        }
        if (approved) {
            order.setStatus(-3);
        } else {
            // 恢复到申请退单前的状态
            order.setStatus(2); // TODO: 记录原始状态
        }
        updateById(order);
    }

    @Override
    @Transactional
    public void adminRefund(Long orderId, String reason) {
        Order order = getById(orderId);
        if (order.getStatus() != 3) { // 只有已收货的才能管理员退单
            throw new RuntimeException("当前状态不可退单");
        }
        order.setStatus(-4);
        order.setCancelReason(reason);
        updateById(order);
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
}
