package com.petshop.service.impl;

import com.petshop.dto.ProductDto;
import com.petshop.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl 商品")
class ProductServiceImplTest {

    @InjectMocks private ProductServiceImpl productService;

    @Nested
    @DisplayName("deductStock 库存扣减")
    class DeductStock {

        @Test
        @DisplayName("宠物 → status=0, stock=0 下架")
        void pet() {
            var spy = spy(productService);
            Product p = new Product(); p.setId(1L); p.setProductType(1);
            p.setStock(1); p.setStatus(1); p.setSales(0);
            doReturn(p).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any());
            assertTrue(spy.deductStock(1L, 1));
            assertEquals(0, p.getStatus()); assertEquals(0, p.getStock());
        }

        @Test
        @DisplayName("宠物已被购买 → 异常")
        void petSold() {
            var spy = spy(productService);
            Product p = new Product(); p.setProductType(1); p.setStock(0); p.setStatus(0);
            doReturn(p).when(spy).getById(1L);
            assertEquals("该宠物已被其他用户购买", assertThrows(RuntimeException.class,
                    () -> spy.deductStock(1L, 1)).getMessage());
        }

        @Test
        @DisplayName("用品 → 扣库存 + 增销量")
        void accessory() {
            var spy = spy(productService);
            Product p = new Product(); p.setId(2L); p.setProductType(2);
            p.setStock(10); p.setStatus(1); p.setSales(5);
            doReturn(p).when(spy).getById(2L);
            doReturn(true).when(spy).updateById(any());
            spy.deductStock(2L, 3);
            assertEquals(7, p.getStock()); assertEquals(8, p.getSales());
        }

        @Test
        @DisplayName("用品库存不足 → 异常")
        void insufficient() {
            var spy = spy(productService);
            Product p = new Product(); p.setProductType(2); p.setStock(2);
            doReturn(p).when(spy).getById(1L);
            assertEquals("库存不足", assertThrows(RuntimeException.class,
                    () -> spy.deductStock(1L, 5)).getMessage());
        }

        @Test
        @DisplayName("商品不存在 → 异常")
        void notFound() {
            var spy = spy(productService);
            doReturn(null).when(spy).getById(999L);
            assertEquals("商品不存在", assertThrows(RuntimeException.class,
                    () -> spy.deductStock(999L, 1)).getMessage());
        }
    }

    @Nested
    @DisplayName("saveProduct 默认值")
    class SaveDefaults {

        @Test
        @DisplayName("status/stock/sales/images 空时设默认")
        void defaults() {
            var spy = spy(productService);
            doReturn(true).when(spy).save(any());
            ProductDto dto = dto("商品");
            spy.saveProduct(dto);
            verify(spy).save(argThat(p ->
                    p.getStatus() == 1 && p.getStock() == 0 &&
                    p.getSales() == 0 && "[]".equals(p.getImages())));
        }

        @Test
        @DisplayName("已有值时不覆盖")
        void preserve() {
            var spy = spy(productService);
            doReturn(true).when(spy).save(any());
            ProductDto dto = dto("商品"); dto.setStatus(0); dto.setStock(5);
            dto.setImages("[\"a\"]");
            spy.saveProduct(dto);
            verify(spy).save(argThat(p -> p.getStatus() == 0 && p.getStock() == 5));
        }
    }

    private ProductDto dto(String name) {
        ProductDto d = new ProductDto(); d.setName(name);
        d.setCategoryId(1L); d.setStoreId(1L); d.setPrice(BigDecimal.TEN);
        return d;
    }
}
