package com.petshop.controller;

import com.petshop.dto.ProductDto;
import com.petshop.entity.Product;
import com.petshop.service.ProductService;
import com.petshop.service.RecommendService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController")
class ProductControllerTest {

    @Mock private ProductService productService;
    @Mock private RecommendService recommendService;
    @InjectMocks private ProductController controller;

    @Test
    @DisplayName("GET /api/product/list")
    void list() {
        when(productService.page(any(), any())).thenReturn(null);
        assertEquals(200, controller.list(1, 10, null, null, null, null, null, null).getCode());
    }

    @Test
    @DisplayName("GET /api/product/1")
    void detail() {
        Product p = new Product(); p.setId(1L); p.setName("金毛"); p.setPrice(new BigDecimal("1500"));
        when(productService.getById(1L)).thenReturn(p);
        assertEquals("金毛", ((Product) controller.detail(1L).getData()).getName());
    }

    @Test
    @DisplayName("GET /api/product/hot")
    void hot() {
        when(productService.list(any())).thenReturn(Collections.emptyList());
        assertEquals(200, controller.hot(8).getCode());
    }

    @Test
    @DisplayName("GET /api/product/recommend")
    void recommend() {
        when(recommendService.recommend(anyLong(), anyInt())).thenReturn(Collections.emptyList());
        assertEquals(200, controller.recommend(1L, 8).getCode());
    }

    @Nested
    @DisplayName("管理端")
    class Admin {
        @Test void create() {
            when(productService.saveProduct(any(ProductDto.class))).thenReturn(true);
            ProductDto d = new ProductDto(); d.setName("新商品");
            d.setCategoryId(1L); d.setStoreId(1L); d.setPrice(BigDecimal.TEN);
            assertEquals(200, controller.create(d).getCode());
        }

        @Test void status() {
            when(productService.updateStatus(anyLong(), anyInt())).thenReturn(true);
            assertEquals(200, controller.status(1L, 0).getCode());
        }
    }
}
