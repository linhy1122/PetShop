package com.petshop.service.impl;

import com.petshop.dto.CartItemDto;
import com.petshop.entity.Cart;
import com.petshop.entity.Product;
import com.petshop.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CartServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartServiceImpl 购物车")
class CartServiceImplTest {

    @Mock private ProductMapper productMapper;
    @InjectMocks private CartServiceImpl cartService;

    @Nested
    @DisplayName("addToCart 添加购物车")
    class AddToCart {

        @Test
        @DisplayName("商品不存在或下架 → 异常")
        void offline() {
            when(productMapper.selectById(100L)).thenReturn(null);
            assertEquals("商品已下架或不存在", assertThrows(RuntimeException.class,
                    () -> cartService.addToCart(1L, 100L, 1)).getMessage());
        }

        @Test
        @DisplayName("宠物限购 1 只（已有 1 只再加 1 只）")
        void petLimit() {
            when(productMapper.selectById(100L)).thenReturn(pet(100L));
            var spy = spy(cartService);
            Cart exist = new Cart(); exist.setQuantity(1);
            doReturn(exist).when(spy).getOne(any());
            assertEquals("每只宠物限购1只", assertThrows(RuntimeException.class,
                    () -> spy.addToCart(1L, 100L, 1)).getMessage());
        }

        @Test
        @DisplayName("用品超库存 → 异常")
        void overStock() {
            when(productMapper.selectById(200L)).thenReturn(acc(200L, 5));
            var spy = spy(cartService);
            doReturn(null).when(spy).getOne(any());
            assertThrows(RuntimeException.class,
                    () -> spy.addToCart(1L, 200L, 6));
        }

        @Test
        @DisplayName("新建购物车记录 checked=1")
        void newItem() {
            when(productMapper.selectById(200L)).thenReturn(acc(200L, 10));
            var spy = spy(cartService);
            doReturn(null).when(spy).getOne(any());
            doReturn(true).when(spy).save(any());
            spy.addToCart(1L, 200L, 3);
            verify(spy).save(argThat(c -> c.getChecked() == 1 && c.getQuantity() == 3));
        }

        @Test
        @DisplayName("已存在 → 累加数量")
        void addExisting() {
            when(productMapper.selectById(200L)).thenReturn(acc(200L, 10));
            var spy = spy(cartService);
            Cart exist = new Cart(); exist.setQuantity(2);
            doReturn(exist).when(spy).getOne(any());
            doReturn(true).when(spy).updateById(any());
            spy.addToCart(1L, 200L, 3);
            verify(spy).updateById(argThat(c -> c.getQuantity() == 5));
        }
    }

    @Nested
    @DisplayName("getUserCart 获取购物车")
    class GetUserCart {

        @Test
        @DisplayName("空购物车 → 空列表")
        void empty() {
            var spy = spy(cartService);
            doReturn(Collections.emptyList()).when(spy).list(any());
            assertTrue(spy.getUserCart(1L).isEmpty());
        }

        @Test
        @DisplayName("商品已删除 → 占位信息")
        void deletedProduct() {
            var spy = spy(cartService);
            Cart c = new Cart(); c.setId(1L); c.setProductId(100L);
            c.setQuantity(1); c.setChecked(1);
            doReturn(Arrays.asList(c)).when(spy).list(any());
            when(productMapper.selectBatchIds(anyList())).thenReturn(Collections.emptyList());
            List<CartItemDto> r = spy.getUserCart(1L);
            assertEquals("商品已删除", r.get(0).getProductName());
            assertEquals(BigDecimal.ZERO, r.get(0).getPrice());
        }
    }

    @Nested
    @DisplayName("全选 / 清除下架")
    class Batch {

        @Test
        @DisplayName("checkAll 跳过已下架商品")
        void checkAllSkipsOffline() {
            var spy = spy(cartService);
            CartItemDto offline = new CartItemDto(); offline.setId(1L); offline.setProductStatus(0);
            CartItemDto online = new CartItemDto(); online.setId(2L); online.setProductStatus(1);
            doReturn(Arrays.asList(offline, online)).when(spy).getUserCart(1L);
            doReturn(true).when(spy).updateBatchById(anyList());
            spy.checkAll(1L, 1);
            verify(spy).updateBatchById(argThat(list -> list.size() == 1));
        }

        @Test
        @DisplayName("clearOffline 删除下架商品")
        void clearOffline() {
            var spy = spy(cartService);
            CartItemDto offline = new CartItemDto(); offline.setId(1L); offline.setProductStatus(0);
            doReturn(Arrays.asList(offline)).when(spy).getUserCart(1L);
            doReturn(true).when(spy).removeByIds(anyList());
            spy.clearOffline(1L);
            verify(spy).removeByIds(argThat(list -> list.contains(1L)));
        }
    }

    @Nested
    @DisplayName("updateQuantity 修改数量")
    class UpdateQuantity {

        @Test
        @DisplayName("记录不存在 → 异常")
        void notFound() {
            var spy = spy(cartService);
            doReturn(null).when(spy).getById(999L);
            assertEquals("购物车记录不存在", assertThrows(RuntimeException.class,
                    () -> spy.updateQuantity(999L, 1)).getMessage());
        }

        @Test
        @DisplayName("宠物超 1 → 异常")
        void petOverLimit() {
            var spy = spy(cartService);
            Cart c = new Cart(); c.setProductId(100L);
            doReturn(c).when(spy).getById(1L);
            when(productMapper.selectById(100L)).thenReturn(pet(100L));
            assertEquals("每只宠物限购1只", assertThrows(RuntimeException.class,
                    () -> spy.updateQuantity(1L, 2)).getMessage());
        }

        @Test
        @DisplayName("正常更新")
        void success() {
            var spy = spy(cartService);
            Cart c = new Cart(); c.setProductId(200L);
            doReturn(c).when(spy).getById(1L);
            when(productMapper.selectById(200L)).thenReturn(acc(200L, 10));
            doReturn(true).when(spy).updateById(any());
            spy.updateQuantity(1L, 5);
            assertEquals(5, c.getQuantity());
        }
    }

    // ============ helpers ============
    private Product pet(Long id) {
        Product p = new Product(); p.setId(id); p.setProductType(1);
        p.setStock(1); p.setStatus(1); p.setPrice(new BigDecimal("100"));
        return p;
    }

    private Product acc(Long id, int stock) {
        Product p = new Product(); p.setId(id); p.setProductType(2);
        p.setStock(stock); p.setStatus(1); p.setPrice(new BigDecimal("50"));
        return p;
    }
}
