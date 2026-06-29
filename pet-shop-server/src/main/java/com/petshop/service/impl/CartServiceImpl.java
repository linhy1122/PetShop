package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.dto.CartItemDto;
import com.petshop.entity.Cart;
import com.petshop.entity.Product;
import com.petshop.mapper.CartMapper;
import com.petshop.mapper.ProductMapper;
import com.petshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        // 校验商品是否存在
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == 0) {
            throw new RuntimeException("商品已下架或不存在");
        }

        // 检查是否已存在
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .eq(Cart::getProductId, productId);
        Cart exist = getOne(wrapper);

        int totalQty = quantity;
        if (exist != null) {
            totalQty = exist.getQuantity() + quantity;
        }

        // 库存校验：周边类型需要检查库存，宠物类型 stock=1
        if (product.getProductType() == 2 && totalQty > product.getStock()) {
            throw new RuntimeException("库存不足，当前库存：" + product.getStock());
        }
        // 宠物类型只能买1只
        if (product.getProductType() == 1 && totalQty > 1) {
            throw new RuntimeException("每只宠物限购1只");
        }

        if (exist != null) {
            exist.setQuantity(totalQty);
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
    public List<CartItemDto> getUserCart(Long userId) {
        // 查询用户购物车
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .orderByDesc(Cart::getCreateTime);
        List<Cart> cartList = list(wrapper);

        if (cartList.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询关联商品
        List<Long> productIds = cartList.stream()
                .map(Cart::getProductId)
                .distinct()
                .collect(Collectors.toList());

        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 组装 DTO
        List<CartItemDto> result = new ArrayList<>();
        for (Cart cart : cartList) {
            Product product = productMap.get(cart.getProductId());
            CartItemDto dto = new CartItemDto();
            dto.setId(cart.getId());
            dto.setProductId(cart.getProductId());
            dto.setQuantity(cart.getQuantity());
            dto.setChecked(cart.getChecked());

            if (product != null) {
                dto.setProductName(product.getName());
                dto.setProductImage(product.getMainImage());
                dto.setPrice(product.getPrice());
                dto.setProductType(product.getProductType());
                dto.setProductStatus(product.getStatus());
                dto.setStock(product.getStock());
            } else {
                dto.setProductName("商品已删除");
                dto.setPrice(java.math.BigDecimal.ZERO);
                dto.setProductStatus(0);
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    public List<Cart> getCartEntities(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .orderByDesc(Cart::getCreateTime);
        return list(wrapper);
    }

    @Override
    public void clearOffline(Long userId) {
        // 获取用户购物车中关联的已下架商品
        List<CartItemDto> cartItems = getUserCart(userId);
        List<Long> offlineIds = cartItems.stream()
                .filter(item -> item.getProductStatus() != null && item.getProductStatus() == 0)
                .map(CartItemDto::getId)
                .collect(Collectors.toList());

        if (!offlineIds.isEmpty()) {
            removeByIds(offlineIds);
        }
    }

    @Override
    public void updateQuantity(Long cartId, Integer quantity) {
        Cart cart = getById(cartId);
        if (cart == null) {
            throw new RuntimeException("购物车记录不存在");
        }

        // 校验库存
        Product product = productMapper.selectById(cart.getProductId());
        if (product != null && product.getProductType() == 2 && quantity > product.getStock()) {
            throw new RuntimeException("库存不足，当前库存：" + product.getStock());
        }
        if (product != null && product.getProductType() == 1 && quantity > 1) {
            throw new RuntimeException("每只宠物限购1只");
        }

        cart.setQuantity(quantity);
        updateById(cart);
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
