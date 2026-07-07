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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 评价接口
 */
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private static final String KEY_USER_ID = "userId";
    private static final String KEY_RATING = "rating";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_IMAGES = "images";
    private static final String KEY_PRODUCT_ID = "productId";
    private static final String KEY_ORDER_ID = "orderId";
    private static final String DEFAULT_IMAGES = "[]";
    private static final String DEFAULT_CONTENT = "";
    private static final int DEFAULT_RATING = 5;

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

        List<Review> records = result.getRecords();
        if (!records.isEmpty()) {
            Set<Long> userIds = records.stream()
                    .map(Review::getUserId)
                    .collect(Collectors.toSet());
            List<User> users = userService.listByIds(userIds);
            Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
            records.forEach(r -> fillUserInfo(r, userMap.get(r.getUserId())));
        }

        return Result.ok(result);
    }

    private void fillUserInfo(Review review, User user) {
        if (user != null) {
            review.setUsername(user.getNickname() != null && !user.getNickname().isEmpty()
                    ? user.getNickname() : user.getUsername());
            review.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
        } else {
            review.setUsername("匿名用户");
            review.setAvatar("");
        }
    }

    /** 提交评价（支持从订单详情或商品详情页提交） */
    @PostMapping("/submit")
    @Transactional
    public Result<Object> submit(@RequestBody Map<String, Object> body) {
        SubmitParams params = parseSubmitParams(body);

        if (params.fromProductPage) {
            createProductPageReview(params);
        } else {
            createOrderReviews(params);
        }

        return Result.ok();
    }

    /** 解析提交参数 */
    private SubmitParams parseSubmitParams(Map<String, Object> body) {
        SubmitParams params = new SubmitParams();
        params.userId = Long.valueOf(body.get(KEY_USER_ID).toString());
        params.rating = body.get(KEY_RATING) != null
                ? Integer.valueOf(body.get(KEY_RATING).toString()) : DEFAULT_RATING;
        params.content = body.get(KEY_CONTENT) != null
                ? body.get(KEY_CONTENT).toString() : DEFAULT_CONTENT;
        params.images = body.get(KEY_IMAGES) != null
                ? body.get(KEY_IMAGES).toString() : DEFAULT_IMAGES;

        Object productIdObj = body.get(KEY_PRODUCT_ID);
        Object orderIdObj = body.get(KEY_ORDER_ID);
        params.fromProductPage = productIdObj != null && !"0".equals(productIdObj.toString())
                && (orderIdObj == null || "0".equals(orderIdObj.toString()));

        if (params.fromProductPage) {
            params.productId = Long.valueOf(Objects.requireNonNull(productIdObj,
                    "productId is required").toString());
        } else {
            params.orderId = Long.valueOf(body.get(KEY_ORDER_ID).toString());
        }
        return params;
    }

    /** 从商品详情页直接评价 */
    private void createProductPageReview(SubmitParams params) {
        Review review = buildReview(params.userId, 0L, params.productId,
                params.rating, params.content, params.images);
        reviewService.save(review);
    }

    /** 从订单详情页评价：对订单中每个商品生成评价 */
    private void createOrderReviews(SubmitParams params) {
        List<OrderItem> items = orderItemService.getByOrderId(params.orderId);
        if (items.isEmpty()) {
            throw new com.petshop.common.BusinessException("订单无商品，无法评价");
        }
        for (OrderItem item : items) {
            Review review = buildReview(params.userId, params.orderId, item.getProductId(),
                    params.rating, params.content, params.images);
            reviewService.save(review);
        }
        com.petshop.entity.Order order = orderService.getById(params.orderId);
        if (order != null && order.getStatus() == 3) {
            orderService.completeOrder(params.orderId);
        }
    }

    private Review buildReview(Long userId, Long orderId, Long productId,
                               Integer rating, String content, String images) {
        Review review = new Review();
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);
        return review;
    }

    /** 提交参数内部类 */
    private static class SubmitParams {
        Long userId;
        Integer rating;
        String content;
        String images;
        boolean fromProductPage;
        Long productId;
        Long orderId;
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
    public Result<Object> updateByOrder(@PathVariable Long orderId,
                                        @RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get(KEY_USER_ID).toString());
        Integer rating = body.get(KEY_RATING) != null
                ? Integer.valueOf(body.get(KEY_RATING).toString()) : DEFAULT_RATING;
        String content = body.get(KEY_CONTENT) != null
                ? body.get(KEY_CONTENT).toString() : DEFAULT_CONTENT;
        String images = body.get(KEY_IMAGES) != null
                ? body.get(KEY_IMAGES).toString() : DEFAULT_IMAGES;

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
    public Result<Object> updateOne(@PathVariable Long reviewId,
                                    @RequestBody Map<String, Object> body) {
        Review review = reviewService.getById(reviewId);
        if (review == null) {
            return Result.error("评价不存在");
        }
        Long userId = Long.valueOf(body.get(KEY_USER_ID).toString());
        if (!review.getUserId().equals(userId)) {
            return Result.error("无权修改他人评价");
        }
        if (body.get(KEY_RATING) != null) {
            review.setRating(Integer.valueOf(body.get(KEY_RATING).toString()));
        }
        if (body.get(KEY_CONTENT) != null) {
            review.setContent(body.get(KEY_CONTENT).toString());
        }
        if (body.get(KEY_IMAGES) != null) {
            review.setImages(body.get(KEY_IMAGES).toString());
        }
        reviewService.updateById(review);
        return Result.ok();
    }

    /** 删除评价（用户删自己，管理员删任意） */
    @DeleteMapping("/{reviewId}")
    public Result<Object> delete(@PathVariable Long reviewId,
                                 @RequestParam Long userId,
                                 @RequestParam(defaultValue = "user") String role) {
        Review review = reviewService.getById(reviewId);
        if (review == null) {
            return Result.error("评价不存在");
        }
        if (!"admin".equals(role) && !review.getUserId().equals(userId)) {
            return Result.error("无权删除他人评价");
        }
        reviewService.removeById(reviewId);
        return Result.ok();
    }
}
