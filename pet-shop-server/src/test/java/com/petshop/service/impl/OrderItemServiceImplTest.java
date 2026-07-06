package com.petshop.service.impl;

import com.petshop.entity.OrderItem;
import com.petshop.mapper.OrderItemMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemServiceImpl")
class OrderItemServiceImplTest {

    @Mock private OrderItemMapper orderItemMapper;
    @InjectMocks private OrderItemServiceImpl service;

    @Test
    @DisplayName("按 orderId 查询 → 订单项列表")
    void findByOrderId() {
        OrderItem a = new OrderItem(); a.setId(1L);
        OrderItem b = new OrderItem(); b.setId(2L);
        when(orderItemMapper.selectList(any())).thenReturn(Arrays.asList(a, b));
        List<OrderItem> r = service.getByOrderId(100L);
        assertEquals(2, r.size());
    }

    @Test
    @DisplayName("无订单项 → 空列表")
    void empty() {
        when(orderItemMapper.selectList(any())).thenReturn(Collections.emptyList());
        assertTrue(service.getByOrderId(999L).isEmpty());
    }
}
