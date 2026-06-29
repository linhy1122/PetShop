package com.petshop.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Order;
import com.petshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务
 * - 每分钟扫描超时未支付订单（创建超过30分钟 & 状态=0），自动取消
 */
@Component
public class OrderScheduler {

    @Autowired
    private OrderService orderService;

    /** 每60秒执行一次：自动取消超时未支付订单 */
    @Scheduled(fixedRate = 60000)
    public void autoCancelExpiredOrders() {
        // 30分钟前的时间点
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(30);

        // 查找创建时间超过30分钟且状态=0（待支付）的订单
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(Order::getCreateTime, expiredTime)
               .eq(Order::getStatus, 0);

        List<Order> expiredOrders = orderService.list(wrapper);

        for (Order order : expiredOrders) {
            try {
                orderService.cancelOrder(
                        order.getId(),
                        "订单超时未支付，系统自动取消",
                        "SYSTEM",
                        true
                );
            } catch (Exception e) {
                // 单个订单取消失败不影响其他订单
                System.err.println("自动取消订单失败，订单ID：" + order.getId() + "，原因：" + e.getMessage());
            }
        }

        if (!expiredOrders.isEmpty()) {
            System.out.println("自动取消超时订单完成，数量：" + expiredOrders.size());
        }
    }
}
