package com.petshop.service.impl;

import com.petshop.dto.StoreDto;
import com.petshop.entity.Store;
import com.petshop.mapper.ProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreServiceImpl 店铺")
class StoreServiceImplTest {

    @Mock private ProductMapper productMapper;
    @InjectMocks private StoreServiceImpl storeService;

    @Nested
    @DisplayName("createStore 默认值")
    class Create {

        @Test
        @DisplayName("rating 默认 5.0，坐标默认 0")
        void defaults() {
            var spy = spy(storeService);
            doReturn(true).when(spy).save(any());
            Store s = spy.createStore(validDto());
            assertEquals(0, new BigDecimal("5.0").compareTo(s.getRating()));
            assertEquals(0, BigDecimal.ZERO.compareTo(s.getLongitude()));
            assertEquals(0, BigDecimal.ZERO.compareTo(s.getLatitude()));
        }

        @Test
        @DisplayName("status 无效 → 异常")
        void badStatus() {
            StoreDto d = validDto(); d.setStatus(3);
            assertEquals("店铺状态只能是0或1", assertThrows(RuntimeException.class,
                    () -> storeService.createStore(d)).getMessage());
        }
    }

    @Nested
    @DisplayName("deleteStore 删除")
    class Delete {

        @Test
        @DisplayName("有商品 → 拒绝")
        void hasProducts() {
            var spy = spy(storeService);
            Store s = new Store(); s.setId(1L); s.setDeleted(0);
            doReturn(s).when(spy).getById(1L);
            when(productMapper.selectCount(any())).thenReturn(5L);
            assertEquals("该店铺下存在商品，不能删除", assertThrows(RuntimeException.class,
                    () -> spy.deleteStore(1L)).getMessage());
        }

        @Test
        @DisplayName("无商品 → 成功")
        void noProducts() {
            var spy = spy(storeService);
            Store s = new Store(); s.setId(1L); s.setDeleted(0);
            doReturn(s).when(spy).getById(1L);
            when(productMapper.selectCount(any())).thenReturn(0L);
            doReturn(true).when(spy).removeById(1L);
            assertDoesNotThrow(() -> spy.deleteStore(1L));
        }
    }

    @Nested
    @DisplayName("updateStoreStatus")
    class UpdateStatus {

        @Test
        @DisplayName("无效状态 → 异常")
        void invalid() {
            assertEquals("店铺状态只能是0或1", assertThrows(RuntimeException.class,
                    () -> storeService.updateStoreStatus(1L, 99)).getMessage());
        }
    }

    @Nested
    @DisplayName("searchNearby")
    class Nearby {

        @Test
        @DisplayName("返回营业中店铺")
        void returns() {
            var spy = spy(storeService);
            doReturn(java.util.Collections.emptyList()).when(spy).list(any());
            assertTrue(spy.searchNearby(BigDecimal.ZERO, BigDecimal.ZERO, 5000).isEmpty());
        }
    }

    private StoreDto validDto() {
        StoreDto d = new StoreDto(); d.setName("店"); d.setPhone("138");
        d.setCity("上海"); d.setAddress("地址"); d.setStatus(1);
        return d;
    }
}
