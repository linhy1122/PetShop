package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Order;
import com.petshop.entity.OrderItem;
import com.petshop.entity.Product;
import com.petshop.mapper.OrderItemMapper;
import com.petshop.mapper.OrderMapper;
import com.petshop.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RecommendServiceImpl 协同过滤推荐")
@SuppressWarnings("unchecked")
class RecommendServiceImplTest {

    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private ProductService productService;
    @InjectMocks private RecommendServiceImpl recommendService;

    @Nested
    @DisplayName("recommend")
    class Recommend {

        @Test
        @DisplayName("冷启动：无历史订单 → 随机填充")
        void coldStart() {
            when(orderMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(productService.list(any(LambdaQueryWrapper.class)))
                    .thenReturn(products(5));

            List<Product> r = recommendService.recommend(1L, 3);
            assertEquals(3, r.size());
        }

        @Test
        @DisplayName("有购买历史 → 协同过滤推荐")
        void withHistory() {
            Order o1 = order(1L, 100L); Order o2 = order(2L, 200L);
            when(orderMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(o1, o2));

            OrderItem i1 = item(1L, 10L);    // user 100 bought 10
            OrderItem i2 = item(2L, 10L);    // user 200 also bought 10
            OrderItem i3 = item(2L, 20L);    // user 200 also bought 20
            when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(i1, i2, i3));

            Product p20 = product(20L);
            when(productService.getById(20L)).thenReturn(p20);
            when(productService.list(any(LambdaQueryWrapper.class)))
                    .thenReturn(products(3));

            List<Product> r = recommendService.recommend(100L, 5);
            assertFalse(r.isEmpty());
        }

        @Test
        @DisplayName("排除已购和已下架商品")
        void excludesPurchasedAndOffline() {
            Order o1 = order(1L, 100L); Order o2 = order(2L, 200L);
            when(orderMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(o1, o2));

            OrderItem i1 = item(1L, 10L);
            OrderItem i2 = item(2L, 30L); // CF 找到 30，但已下架
            when(orderItemMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(i1, i2));

            Product p30 = product(30L); p30.setStatus(0); // 下架
            when(productService.getById(30L)).thenReturn(p30);
            when(productService.list(any(LambdaQueryWrapper.class)))
                    .thenReturn(products(3));

            List<Product> r = recommendService.recommend(100L, 3);
            assertTrue(r.stream().noneMatch(p -> p.getId().equals(30L)));
        }

        @Test
        @DisplayName("不超过 limit")
        void respectsLimit() {
            when(orderMapper.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Collections.emptyList());
            when(productService.list(any(LambdaQueryWrapper.class)))
                    .thenReturn(products(10));

            assertTrue(recommendService.recommend(1L, 3).size() <= 3);
        }
    }

    private Order order(Long id, Long userId) {
        Order o = new Order(); o.setId(id); o.setUserId(userId); return o;
    }

    private OrderItem item(Long orderId, Long productId) {
        OrderItem i = new OrderItem(); i.setOrderId(orderId);
        i.setProductId(productId); return i;
    }

    private Product product(Long id) {
        Product p = new Product(); p.setId(id); p.setName("P" + id);
        p.setStatus(1); p.setPrice(new BigDecimal("100")); return p;
    }

    private List<Product> products(int n) {
        List<Product> list = new ArrayList<>();
        for (long i = 1; i <= n; i++) list.add(product(i));
        return list;
    }
}
