package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Order;
import com.petshop.entity.OrderItem;
import com.petshop.entity.Product;
import com.petshop.mapper.OrderItemMapper;
import com.petshop.mapper.OrderMapper;
import com.petshop.service.ProductService;
import com.petshop.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐 — 基于用户的 Jaccard 相似度
 */
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductService productService;

    @Override
    public List<Product> recommend(Long userId, int limit) {
        // 1. 获取所有有效订单 → (userId → Set<productId>)
        Map<Long, Set<Long>> userProducts = buildUserProductMap();

        // 2. 目标用户的购买集合
        Set<Long> targetSet = userProducts.getOrDefault(userId, Collections.emptySet());
        if (targetSet.isEmpty()) {
            // 冷启动：返回热门商品
            LambdaQueryWrapper<Product> hotWrapper = new LambdaQueryWrapper<>();
            hotWrapper.eq(Product::getStatus, 1)
                      .orderByDesc(Product::getSales)
                      .last("LIMIT " + limit);
            return productService.list(hotWrapper);
        }

        // 3. 计算与其他用户的 Jaccard 相似度，累计商品得分
        Map<Long, Double> scores = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : userProducts.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) continue;

            Set<Long> otherSet = entry.getValue();
            double similarity = jaccard(targetSet, otherSet);
            if (similarity <= 0) continue;

            // 相似用户买了但目标用户没买的商品，加分
            for (Long productId : otherSet) {
                if (!targetSet.contains(productId)) {
                    scores.merge(productId, similarity, Double::sum);
                }
            }
        }

        // 4. 如果没找到推荐（所有用户都不相似），降级为热门
        if (scores.isEmpty()) {
            LambdaQueryWrapper<Product> hotWrapper = new LambdaQueryWrapper<>();
            hotWrapper.eq(Product::getStatus, 1)
                      .orderByDesc(Product::getSales)
                      .last("LIMIT " + limit);
            return productService.list(hotWrapper);
        }

        // 5. 按得分排序取 top N
        List<Long> recommendedIds = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 6. 查询商品完整信息，保持排序
        List<Product> products = productService.listByIds(recommendedIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        return recommendedIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 构建用户→购买商品集合的映射
     */
    private Map<Long, Set<Long>> buildUserProductMap() {
        // 获取所有非取消的订单
        List<Order> validOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .notIn(Order::getStatus, -1, -2, -3, -4));

        if (validOrders.isEmpty()) return Collections.emptyMap();

        List<Long> orderIds = validOrders.stream()
                .map(Order::getId).collect(Collectors.toList());

        // 获取这些订单的所有订单项
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, orderIds));

        // 构建映射
        Map<Long, Long> orderUserMap = validOrders.stream()
                .collect(Collectors.toMap(Order::getId, Order::getUserId));

        Map<Long, Set<Long>> userProducts = new HashMap<>();
        for (OrderItem item : items) {
            Long userId = orderUserMap.get(item.getOrderId());
            if (userId != null) {
                userProducts.computeIfAbsent(userId, k -> new HashSet<>())
                        .add(item.getProductId());
            }
        }
        return userProducts;
    }

    /**
     * Jaccard 相似度 = |A ∩ B| / |A ∪ B|
     */
    private double jaccard(Set<Long> a, Set<Long> b) {
        if (a.isEmpty() || b.isEmpty()) return 0;
        Set<Long> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<Long> union = new HashSet<>(a);
        union.addAll(b);
        return (double) intersection.size() / union.size();
    }
}
