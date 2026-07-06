package com.petshop.controller;

import com.petshop.dto.StoreDto;
import com.petshop.entity.Store;
import com.petshop.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreController")
class StoreControllerTest {

    @Mock private StoreService storeService;
    @InjectMocks private StoreController controller;

    @Test void list() {
        when(storeService.page(any(), any())).thenReturn(null);
        assertEquals(200, controller.list(1, 10, null, null).getCode());
    }

    @Test void detail() {
        Store s = new Store(); s.setId(1L); s.setName("测试店铺");
        when(storeService.getById(1L)).thenReturn(s);
        assertEquals("测试店铺", controller.detail(1L).getData().getName());
    }

    @Test void nearby() {
        when(storeService.searchNearby(any(), any(), any())).thenReturn(Collections.emptyList());
        assertEquals(200, controller.nearby(java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, 5000).getCode());
    }

    @Test void create() {
        StoreDto d = new StoreDto(); d.setName("新店"); d.setPhone("138");
        d.setCity("上海"); d.setAddress("地址"); d.setStatus(1);
        Store s = new Store(); s.setId(1L);
        when(storeService.createStore(any(StoreDto.class))).thenReturn(s);
        assertEquals(200, controller.adminCreate(d).getCode());
    }

    @Test void updateStatus() {
        doNothing().when(storeService).updateStoreStatus(anyLong(), anyInt());
        assertEquals(200, controller.adminUpdateStatus(1L, 0).getCode());
    }
}
