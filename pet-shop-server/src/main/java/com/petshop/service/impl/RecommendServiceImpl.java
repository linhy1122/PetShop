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

        // 2. 目标用户的购买集合（用于排除已购商品）
        Set<Long> purchased = userProducts.getOrDefault(userId, Collections.emptySet());

        // 3. 协同过滤打分（排除已购商品）
        Map<Long, Double> scores = new HashMap<>();
        if (!purchased.isEmpty()) {
            for (Map.Entry<Long, Set<Long>> entry : userProducts.entrySet()) {
                Long otherUserId = entry.getKey();
                if (otherUserId.equals(userId)) continue;

                Set<Long> otherSet = entry.getValue();
                double similarity = jaccard(purchased, otherSet);
                if (similarity <= 0) continue;

                for (Long productId : otherSet) {
                    if (!purchased.contains(productId)) {
                        scores.merge(productId, similarity, Double::sum);
                    }
                }
            }
        } 

        // 4. CF 推荐商品按得分排序
        List<Long> cfIds = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 5. 查询 CF 推荐商品
        List<Product> result = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();
        for (Long id : cfIds) {
            if (result.size() >= limit) break;
            Product p = productService.getById(id);
            if (p != null && p.getStatus() != null && p.getStatus() == 1) {
                result.add(p);
                addedIds.add(id);
            }
        }

        // 6. 不足 limit 个 → 随机补充其他在售商品
        if (result.size() < limit) {
            List<Product> fillers = getRandomFillers(limit - result.size(), purchased, addedIds);
            result.addAll(fillers);
        }

        return result;
    }

    /**
     * 随机补充在售商品，排除已购买和已推荐的商品
     */
    private List<Product> getRandomFillers(int need, Set<Long> purchased, Set<Long> alreadyAdded) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        if (!purchased.isEmpty() || !alreadyAdded.isEmpty()) {
            Set<Long> exclude = new HashSet<>(purchased);
            exclude.addAll(alreadyAdded);
            wrapper.notIn(Product::getId, exclude);
        }
        // 随机排序（MySQL RAND）
        wrapper.last("ORDER BY RAND() LIMIT " + (need * 3));
        List<Product> candidates = productService.list(wrapper);
        Collections.shuffle(candidates);
        return candidates.stream().limit(need).collect(Collectors.toList());
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
