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
        Map<Long, Set<Long>> userProducts = buildUserProductMap();
        Set<Long> purchased = userProducts.getOrDefault(userId, Collections.emptySet());

        Map<Long, Double> scores = computeCfScores(userProducts, userId, purchased);
        List<Product> result = fetchCfProducts(scores, limit);
        fillRemainingIfNeeded(result, limit, purchased);

        return result;
    }

    /**
     * 协同过滤打分：计算其他用户与目标用户的 Jaccard 相似度，为未购商品打分
     */
    private Map<Long, Double> computeCfScores(Map<Long, Set<Long>> userProducts,
                                              Long userId, Set<Long> purchased) {
        Map<Long, Double> scores = new HashMap<>();
        if (purchased.isEmpty()) {
            return scores;
        }
        for (Map.Entry<Long, Set<Long>> entry : userProducts.entrySet()) {
            Long otherUserId = entry.getKey();
            if (otherUserId.equals(userId)) {
                continue;
            }
            Set<Long> otherSet = entry.getValue();
            double similarity = jaccard(purchased, otherSet);
            if (similarity > 0) {
                addScoresForUnpurchased(scores, otherSet, purchased, similarity);
            }
        }
        return scores;
    }

    /**
     * 将其他用户购买但目标用户未购的商品加入打分表
     */
    private void addScoresForUnpurchased(Map<Long, Double> scores, Set<Long> otherSet,
                                         Set<Long> purchased, double similarity) {
        for (Long productId : otherSet) {
            if (!purchased.contains(productId)) {
                scores.merge(productId, similarity, Double::sum);
            }
        }
    }

    /**
     * 根据 CF 得分获取商品，按得分降序
     */
    private List<Product> fetchCfProducts(Map<Long, Double> scores, int limit) {
        List<Long> cfIds = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Product> result = new ArrayList<>();
        Set<Long> addedIds = new HashSet<>();
        for (Long id : cfIds) {
            if (result.size() >= limit) {
                break;
            }
            Product p = productService.getById(id);
            if (p != null && p.getStatus() != null && p.getStatus() == 1) {
                result.add(p);
                addedIds.add(id);
            }
        }
        return result;
    }

    /**
     * 数量不足 limit 时用随机商品补齐
     */
    private void fillRemainingIfNeeded(List<Product> result, int limit, Set<Long> purchased) {
        if (result.size() < limit) {
            Set<Long> alreadyAdded = result.stream().map(Product::getId).collect(Collectors.toSet());
            List<Product> fillers = getRandomFillers(limit - result.size(), purchased, alreadyAdded);
            result.addAll(fillers);
        }
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
        wrapper.last("ORDER BY RAND() LIMIT " + (need * 3));
        List<Product> candidates = productService.list(wrapper);
        Collections.shuffle(candidates);
        return candidates.stream().limit(need).collect(Collectors.toList());
    }

    /**
     * 构建用户→购买商品集合的映射
     */
    private Map<Long, Set<Long>> buildUserProductMap() {
        List<Order> validOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .notIn(Order::getStatus, -1, -2, -3, -4));

        if (validOrders.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> orderIds = validOrders.stream()
                .map(Order::getId).collect(Collectors.toList());

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .in(OrderItem::getOrderId, orderIds));

        Map<Long, Long> orderUserMap = validOrders.stream()
                .collect(Collectors.toMap(Order::getId, Order::getUserId));

        Map<Long, Set<Long>> userProducts = new HashMap<>();
        for (OrderItem item : items) {
            Long uid = orderUserMap.get(item.getOrderId());
            if (uid != null) {
                userProducts.computeIfAbsent(uid, k -> new HashSet<>())
                        .add(item.getProductId());
            }
        }
        return userProducts;
    }

    /**
     * Jaccard 相似度 = |A ∩ B| / |A ∪ B|
     */
    private double jaccard(Set<Long> a, Set<Long> b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        Set<Long> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<Long> union = new HashSet<>(a);
        union.addAll(b);
        return (double) intersection.size() / union.size();
    }
}
