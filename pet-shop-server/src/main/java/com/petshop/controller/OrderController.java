package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.dto.OrderCreateDto;
import com.petshop.entity.Order;
import com.petshop.entity.OrderItem;
import com.petshop.entity.OrderLog;
import com.petshop.service.OrderItemService;
import com.petshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

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
                            @RequestParam String reason,
                            @RequestParam(defaultValue = "USER") String cancelType) {
        orderService.cancelOrder(orderId, reason, cancelType, false);
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

    /** 订单详情（含订单项 + 操作日志） */
    @GetMapping("/{orderId:\\d+}")
    public Result<Map<String, Object>> detail(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        List<OrderItem> items = orderItemService.getByOrderId(orderId);
        List<OrderLog> logs = orderService.getOrderLogs(orderId);

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        result.put("logs", logs);
        return Result.ok(result);
    }

    // ==================== 管理员接口 ====================

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
                                  @RequestParam boolean approved,
                                  @RequestParam(defaultValue = "") String auditRemark) {
        orderService.auditRefund(orderId, approved, auditRemark);
        return Result.ok();
    }

    /** 管理员直接退单 */
    @PutMapping("/{orderId}/admin-refund")
    public Result<?> adminRefund(@PathVariable Long orderId,
                                  @RequestParam String reason) {
        orderService.adminRefund(orderId, reason);
        return Result.ok();
    }

    /** 管理员订单列表（支持搜索筛选） */
    @GetMapping("/list")
    public Result<Page<Order>> adminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w
                    .like(Order::getOrderNo, keyword)
                    .or()
                    .eq(Order::getUserId, tryParseLong(keyword)));
        }
        if (startTime != null) {
            wrapper.ge(Order::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(Order::getCreateTime, endTime);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return Result.ok(orderService.page(new Page<>(page, size), wrapper));
    }

    /** 管理员订单详情（含订单项 + 日志） */
    @GetMapping("/admin/{orderId:\\d+}")
    public Result<Map<String, Object>> adminDetail(@PathVariable Long orderId) {
        return detail(orderId);
    }

    private Long tryParseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
