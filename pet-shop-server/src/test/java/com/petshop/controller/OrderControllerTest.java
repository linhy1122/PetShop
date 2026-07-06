package com.petshop.controller;

import com.petshop.dto.OrderCreateDto;
import com.petshop.entity.Order;
import com.petshop.service.OrderItemService;
import com.petshop.service.OrderService;
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
@DisplayName("OrderController")
class OrderControllerTest {

    @Mock private OrderService orderService;
    @Mock private OrderItemService orderItemService;
    @InjectMocks private OrderController controller;

    @Nested
    @DisplayName("POST /api/order/create")
    class Create {
        @Test
        @DisplayName("创建成功")
        void ok() {
            Order o = new Order(); o.setId(1L); o.setOrderNo("SN123"); o.setStatus(0);
            o.setTotalAmount(new BigDecimal("100"));
            when(orderService.createOrder(anyLong(), anyLong(), any())).thenReturn(o);
            OrderCreateDto d = new OrderCreateDto(); d.setAddressId(1L);
            var r = controller.create(1L, d);
            assertEquals(200, r.getCode());
            assertEquals("SN123", r.getData().getOrderNo());
        }
    }

    @Nested
    @DisplayName("状态转换")
    class Transitions {
        @Test void pay() {
            doNothing().when(orderService).payOrder(anyLong(), anyString());
            assertEquals(200, controller.pay(1L, "alipay").getCode());
        }
        @Test void cancel() {
            doNothing().when(orderService).cancelOrder(anyLong(), anyString(), anyString(), anyBoolean());
            assertEquals(200, controller.cancel(1L, "不想要", "USER").getCode());
        }
        @Test void receive() {
            doNothing().when(orderService).confirmReceive(anyLong());
            assertEquals(200, controller.receive(1L).getCode());
        }
        @Test void refund() {
            doNothing().when(orderService).applyRefund(anyLong(), anyString());
            assertEquals(200, controller.refund(1L, "质量问题").getCode());
        }
    }

    @Nested
    @DisplayName("管理端")
    class Admin {
        @Test void deliver() {
            doNothing().when(orderService).deliverOrder(anyLong(), anyString(), anyString());
            assertEquals(200, controller.deliver(1L, "顺丰", "SF123").getCode());
        }
        @Test void auditRefund() {
            doNothing().when(orderService).auditRefund(anyLong(), anyBoolean(), anyString());
            assertEquals(200, controller.auditRefund(1L, true, "同意").getCode());
        }
        @Test void adminRefund() {
            doNothing().when(orderService).adminRefund(anyLong(), anyString());
            assertEquals(200, controller.adminRefund(1L, "投诉").getCode());
        }
    }

    @Test
    @DisplayName("GET /api/order/1 → 详情")
    void detail() {
        Order o = new Order(); o.setId(1L); o.setOrderNo("SN1"); o.setStatus(1);
        when(orderService.getById(1L)).thenReturn(o);
        when(orderItemService.getByOrderId(1L)).thenReturn(Collections.emptyList());
        when(orderService.getOrderLogs(1L)).thenReturn(Collections.emptyList());
        var r = controller.detail(1L);
        assertEquals(200, r.getCode());
    }
}
