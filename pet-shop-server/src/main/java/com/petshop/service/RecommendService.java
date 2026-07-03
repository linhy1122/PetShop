package com.petshop.service;

import com.petshop.entity.Product;

import java.util.List;

/**
 * 个性化推荐服务
 */
public interface RecommendService {

    /**
     * 基于协同过滤为用户推荐商品
     * @param userId 用户ID
     * @param limit  返回数量
     * @return 推荐商品列表
     */
    List<Product> recommend(Long userId, int limit);
}
