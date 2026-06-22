package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.dto.OrderCreateDto;
import com.petshop.entity.Order;
import com.petshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /** 创建订单 */
    @PostMapping("/create")
    public Result<Order> create(@RequestParam Long userId,
                                 @Valid @RequestBody OrderCreateDto dto) {
        return Result.ok(orderService.createOrder(userId, dto.getAddressId(), dto.getRemark()));
    }

    /** 支付订单 */
    @PutMapping("/{orderId}/pay")
    public Result<?> pay(@PathVariable Long orderId,
                         @RequestParam(defaultValue = "alipay") String payMethod) {
        orderService.payOrder(orderId, payMethod);
        return Result.ok();
    }

    /** 取消订单 */
    @PutMapping("/{orderId}/cancel")
    public Result<?> cancel(@PathVariable Long orderId,
                            @RequestParam String reason) {
        orderService.cancelOrder(orderId, reason, false);
        return Result.ok();
    }

    /** 确认收货 */
    @PutMapping("/{orderId}/receive")
    public Result<?> receive(@PathVariable Long orderId) {
        orderService.confirmReceive(orderId);
        return Result.ok();
    }

    /** 申请退单 */
    @PutMapping("/{orderId}/refund")
    public Result<?> refund(@PathVariable Long orderId,
                            @RequestParam String reason) {
        orderService.applyRefund(orderId, reason);
        return Result.ok();
    }

    /** 用户订单列表 */
    @GetMapping("/user/{userId}")
    public Result<Page<Order>> userOrders(@PathVariable Long userId,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return Result.ok(orderService.page(new Page<>(page, size), wrapper));
    }

    /** 订单详情 */
    @GetMapping("/{orderId}")
    public Result<Order> detail(@PathVariable Long orderId) {
        return Result.ok(orderService.getById(orderId));
    }

    /** 发货（管理员） */
    @PutMapping("/{orderId}/deliver")
    public Result<?> deliver(@PathVariable Long orderId,
                             @RequestParam String logisticsCompany,
                             @RequestParam String logisticsNo) {
        orderService.deliverOrder(orderId, logisticsCompany, logisticsNo);
        return Result.ok();
    }

    /** 审核退单（管理员） */
    @PutMapping("/{orderId}/audit-refund")
    public Result<?> auditRefund(@PathVariable Long orderId,
                                  @RequestParam boolean approved) {
        orderService.auditRefund(orderId, approved);
        return Result.ok();
    }

    /** 管理员直接退单 */
    @PutMapping("/{orderId}/admin-refund")
    public Result<?> adminRefund(@PathVariable Long orderId,
                                  @RequestParam String reason) {
        orderService.adminRefund(orderId, reason);
        return Result.ok();
    }
}
