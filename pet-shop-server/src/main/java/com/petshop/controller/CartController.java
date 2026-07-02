package com.petshop.controller;

import com.petshop.common.Result;
import com.petshop.dto.CartItemDto;
import com.petshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车接口
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /** 获取用户购物车（含商品详情） */
    @GetMapping("/list")
    public Result<List<CartItemDto>> list(@RequestParam Long userId) {
        return Result.ok(cartService.getUserCart(userId));
    }

    /** 加入购物车 */
    @PostMapping("/add")
    public Result<?> add(@RequestParam Long userId,
                         @RequestParam Long productId,
                         @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.addToCart(userId, productId, quantity);
        return Result.ok();
    }

    /** 修改数量 */
    @PutMapping("/{cartId}/quantity")
    public Result<?> updateQuantity(@PathVariable Long cartId,
                                     @RequestParam Integer quantity) {
        cartService.updateQuantity(cartId, quantity);
        return Result.ok();
    }

    /** 选中/取消选中 */
    @PutMapping("/{cartId}/check")
    public Result<?> updateCheck(@PathVariable Long cartId,
                                  @RequestParam Integer checked) {
        cartService.updateChecked(cartId, checked);
        return Result.ok();
    }

    /** 删除购物车商品 */
    @DeleteMapping("/{cartId}")
    public Result<?> remove(@PathVariable Long cartId) {
        cartService.removeById(cartId);
        return Result.ok();
    }

    /** 批量删除购物车项 */
    @DeleteMapping("/batch")
    public Result<?> batchRemove(@RequestParam List<Long> ids) {
        cartService.removeByIds(ids);
        return Result.ok();
    }

    /** 清除用户购物车中已下架的商品 */
    @DeleteMapping("/clear-offline")
    public Result<?> clearOffline(@RequestParam Long userId) {
        cartService.clearOffline(userId);
        return Result.ok();
    }

    /** 全选/全不选（自动跳过已下架商品） */
    @PutMapping("/check-all")
    public Result<?> checkAll(@RequestParam Long userId,
                              @RequestParam Integer checked) {
        cartService.checkAll(userId, checked);
        return Result.ok();
    }
}
