package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.Cart;
import com.petshop.mapper.CartMapper;
import com.petshop.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 检查是否已存在
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .eq(Cart::getProductId, productId);
        Cart exist = getOne(wrapper);
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            updateById(exist);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            save(cart);
        }
    }

    @Override
    public List<Cart> getUserCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .orderByDesc(Cart::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        Cart cart = getById(cartId);
        if (cart != null) {
            cart.setQuantity(quantity);
            updateById(cart);
        }
    }

    @Override
    public void updateChecked(Long cartId, Integer checked) {
        Cart cart = getById(cartId);
        if (cart != null) {
            cart.setChecked(checked);
            updateById(cart);
        }
    }
}
