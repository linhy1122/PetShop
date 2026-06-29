package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.dto.CartItemDto;
import com.petshop.entity.Cart;

import java.util.List;

/**
 * 购物车服务
 */
public interface CartService extends IService<Cart> {

    /** 加入购物车 */
    void addToCart(Long userId, Long productId, Integer quantity);

    /** 获取用户购物车（含商品详情） */
    List<CartItemDto> getUserCart(Long userId);

    /** 修改数量 */
    void updateQuantity(Long cartId, Integer quantity);

    /** 选中/取消选中 */
    void updateChecked(Long cartId, Integer checked);

    /** 获取原始 Cart 实体列表（内部使用） */
    List<Cart> getCartEntities(Long userId);

    /** 清除用户购物车中已下架的商品 */
    void clearOffline(Long userId);
}
