package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.entity.Order;
import com.petshop.entity.OrderLog;

import java.util.List;

/**
 * 订单服务
 */
public interface OrderService extends IService<Order> {

    /** 创建订单（从购物车生成） */
    Order createOrder(Long userId, Long addressId, String remark);

    /** 支付订单 */
    void payOrder(Long orderId, String payMethod);

    /** 取消订单 */
    void cancelOrder(Long orderId, String reason, String cancelType, boolean isAutoCancel);

    /** 发货 */
    void deliverOrder(Long orderId, String logisticsCompany, String logisticsNo);

    /** 确认收货 */
    void confirmReceive(Long orderId);

    /** 申请退单 */
    void applyRefund(Long orderId, String reason);

    /** 审核退单 */
    void auditRefund(Long orderId, boolean approved, String auditRemark);

    /** 管理员直接退单 */
    void adminRefund(Long orderId, String reason);

    /** 完成订单（评价后状态 3→4） */
    void completeOrder(Long orderId);

    /** 获取订单操作日志 */
    List<OrderLog> getOrderLogs(Long orderId);
}
