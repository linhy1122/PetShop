package com.petshop.controller;

import com.petshop.dto.CartItemDto;
import com.petshop.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartController")
class CartControllerTest {

    @Mock private CartService cartService;
    @InjectMocks private CartController controller;

    @Test
    @DisplayName("GET /api/cart/list")
    void list() {
        CartItemDto item = new CartItemDto(); item.setProductName("狗粮");
        item.setPrice(new BigDecimal("50")); item.setQuantity(2);
        when(cartService.getUserCart(1L)).thenReturn(Arrays.asList(item));
        var r = controller.list(1L);
        assertEquals(200, r.getCode());
        assertEquals(1, r.getData().size());
    }

    @Test void add() {
        doNothing().when(cartService).addToCart(anyLong(), anyLong(), anyInt());
        assertEquals(200, controller.add(1L, 100L, 2).getCode());
    }

    @Test void updateQty() {
        doNothing().when(cartService).updateQuantity(anyLong(), anyInt());
        assertEquals(200, controller.updateQuantity(1L, 5).getCode());
    }

    @Test void remove() {
        when(cartService.removeById(1L)).thenReturn(true);
        assertEquals(200, controller.remove(1L).getCode());
    }

    @Test void checkAll() {
        doNothing().when(cartService).checkAll(anyLong(), anyInt());
        assertEquals(200, controller.checkAll(1L, 1).getCode());
    }

    @Test void clearOffline() {
        doNothing().when(cartService).clearOffline(anyLong());
        assertEquals(200, controller.clearOffline(1L).getCode());
    }
}
