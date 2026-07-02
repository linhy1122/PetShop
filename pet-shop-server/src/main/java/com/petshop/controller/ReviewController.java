package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.entity.OrderItem;
import com.petshop.entity.Review;
import com.petshop.entity.User;
import com.petshop.service.OrderItemService;
import com.petshop.service.OrderService;
import com.petshop.service.ReviewService;
import com.petshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 评价接口
 */
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserService userService;

    /** 商品评价列表 */
    @GetMapping("/product/{productId}")
    public Result<Page<Review>> listByProduct(@PathVariable Long productId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId)
               .orderByDesc(Review::getCreateTime);
        Page<Review> result = reviewService.page(new Page<>(page, size), wrapper);

        // 批量填充用户名和头像
        List<Review> records = result.getRecords();
        if (!records.isEmpty()) {
            Set<Long> userIds = records.stream()
                    .map(Review::getUserId)
                    .collect(Collectors.toSet());
            List<User> users = userService.listByIds(userIds);
            Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
            records.forEach(r -> {
                User u = userMap.get(r.getUserId());
                if (u != null) {
                    r.setUsername(u.getNickname() != null && !u.getNickname().isEmpty()
                            ? u.getNickname() : u.getUsername());
                    r.setAvatar(u.getAvatar() != null ? u.getAvatar() : "");
                } else {
                    r.setUsername("匿名用户");
                    r.setAvatar("");
                }
            });
        }

        return Result.ok(result);
    }

    /** 提交评价（支持从订单详情或商品详情页提交） */
    @PostMapping("/submit")
    @Transactional
    public Result<?> submit(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Integer rating = body.get("rating") != null ? Integer.valueOf(body.get("rating").toString()) : 5;
        String content = body.get("content") != null ? body.get("content").toString() : "";
        String images = body.get("images") != null ? body.get("images").toString() : "[]";

        // 判断评价来源：有 productId 且无有效 orderId → 商品详情页直接评价
        Object productIdObj = body.get("productId");
        Object orderIdObj = body.get("orderId");
        boolean fromProductPage = productIdObj != null && !"0".equals(productIdObj.toString())
                && (orderIdObj == null || "0".equals(orderIdObj.toString()));

        if (fromProductPage) {
            // 商品详情页直接评价
            Long productId = Long.valueOf(productIdObj.toString());
            Review review = new Review();
            review.setUserId(userId);
            review.setOrderId(0L);
            review.setProductId(productId);
            review.setRating(rating);
            review.setContent(content);
            review.setImages(images);
            reviewService.save(review);
        } else {
            // 订单详情页评价：对订单中每个商品生成评价，并完成订单
            Long orderId = Long.valueOf(body.get("orderId").toString());
            List<OrderItem> items = orderItemService.getByOrderId(orderId);
            if (items.isEmpty()) {
                return Result.error("订单无商品，无法评价");
            }
            for (OrderItem item : items) {
                Review review = new Review();
                review.setUserId(userId);
                review.setOrderId(orderId);
                review.setProductId(item.getProductId());
                review.setRating(rating);
                review.setContent(content);
                review.setImages(images);
                reviewService.save(review);
            }
            // 仅当订单状态为3（待评价）时才完成订单
            com.petshop.entity.Order order = orderService.getById(orderId);
            if (order != null && order.getStatus() == 3) {
                orderService.completeOrder(orderId);
            }
        }

        return Result.ok();
    }

    /** 获取用户在指定订单下的评价（含商品名称） */
    @GetMapping("/order/{orderId}")
    public Result<List<Review>> listByOrder(@PathVariable Long orderId,
                                             @RequestParam Long userId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId)
               .eq(Review::getUserId, userId)
               .orderByDesc(Review::getCreateTime);
        List<Review> reviews = reviewService.list(wrapper);

        // 填充商品名称
        if (!reviews.isEmpty()) {
            List<OrderItem> items = orderItemService.getByOrderId(orderId);
            Map<Long, String> nameMap = items.stream()
                    .collect(Collectors.toMap(OrderItem::getProductId,
                            OrderItem::getProductName, (a, b) -> a));
            reviews.forEach(r -> r.setProductName(nameMap.getOrDefault(r.getProductId(), "未知商品")));
        }

        return Result.ok(reviews);
    }

    /** 修改评价（批量更新该订单下该用户的所有评价记录） */
    @PutMapping("/order/{orderId}")
    @Transactional
    public Result<?> updateByOrder(@PathVariable Long orderId,
                                    @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Integer rating = body.get("rating") != null ? Integer.valueOf(body.get("rating").toString()) : 5;
        String content = body.get("content") != null ? body.get("content").toString() : "";
        String images = body.get("images") != null ? body.get("images").toString() : "[]";

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getOrderId, orderId)
               .eq(Review::getUserId, userId);
        List<Review> reviews = reviewService.list(wrapper);
        if (reviews.isEmpty()) {
            return Result.error("未找到评价记录");
        }
        for (Review r : reviews) {
            r.setRating(rating);
            r.setContent(content);
            r.setImages(images);
            reviewService.updateById(r);
        }
        return Result.ok();
    }

    /** 修改单条评价 */
    @PutMapping("/{reviewId}")
    @Transactional
    public Result<?> updateOne(@PathVariable Long reviewId,
                                @RequestBody Map<String, Object> body) {
        Review review = reviewService.getById(reviewId);
        if (review == null) {
            return Result.error("评价不存在");
        }
        Long userId = Long.valueOf(body.get("userId").toString());
        if (!review.getUserId().equals(userId)) {
            return Result.error("无权修改他人评价");
        }
        if (body.get("rating") != null) {
            review.setRating(Integer.valueOf(body.get("rating").toString()));
        }
        if (body.get("content") != null) {
            review.setContent(body.get("content").toString());
        }
        if (body.get("images") != null) {
            review.setImages(body.get("images").toString());
        }
        reviewService.updateById(review);
        return Result.ok();
    }

    /** 删除评价（用户删自己，管理员删任意） */
    @DeleteMapping("/{reviewId}")
    public Result<?> delete(@PathVariable Long reviewId,
                            @RequestParam Long userId,
                            @RequestParam(defaultValue = "user") String role) {
        Review review = reviewService.getById(reviewId);
        if (review == null) {
            return Result.error("评价不存在");
        }
        // 非管理员只能删除自己的评价
        if (!"admin".equals(role) && !review.getUserId().equals(userId)) {
            return Result.error("无权删除他人评价");
        }
        reviewService.removeById(reviewId);
        return Result.ok();
    }
}
