package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.common.MemberLevel;
import com.petshop.entity.*;
import com.petshop.mapper.OrderLogMapper;
import com.petshop.service.CartService;
import com.petshop.service.OrderItemService;
import com.petshop.service.ProductService;
import com.petshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
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
 * OrderServiceImpl — 最复杂：7种状态 × 8种转换
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl 订单状态机")
@SuppressWarnings("unchecked")
class OrderServiceImplTest {

    @Mock private CartService cartService;
    @Mock private OrderItemService orderItemService;
    @Mock private ProductService productService;
    @Mock private OrderLogMapper orderLogMapper;
    @Mock private UserService userService;
    @InjectMocks private OrderServiceImpl orderService;

    // ==================== 状态机总览 ====================
    // 0(待支付) → 1(已支付) → 2(已发货) → 3(已收货) → 4(已完成)
    //   ↓             ↓
    // -1(已取消)    -2(退单中) → -3(退单通过) / 恢复
    //               -4(管理员退单)

    // ==================== createOrder ====================
    @Nested
    @DisplayName("createOrder 创建订单")
    class CreateOrder {

        @Test
        @DisplayName("购物车无选中商品 → 异常")
        void emptyCart() {
            when(cartService.getCartEntities(1L)).thenReturn(Collections.emptyList());
            assertEquals("购物车中没有选中的商品",
                    assertThrows(RuntimeException.class,
                            () -> orderService.createOrder(1L, 1L, null)).getMessage());
        }

        @Test
        @DisplayName("普通会员无折扣")
        void normalMember() {
            var spy = setupCreateSpy(MemberLevel.NORMAL);
            Order o = spy.createOrder(1L, 1L, null);
            assertEquals(0, new BigDecimal("100.00").compareTo(o.getPayAmount()));
        }

        @Test
        @DisplayName("银卡 95 折")
        void silver() {
            var spy = setupCreateSpy(MemberLevel.SILVER);
            assertEquals(0, new BigDecimal("95.00").compareTo(
                    spy.createOrder(1L, 1L, null).getPayAmount()));
        }

        @Test
        @DisplayName("金卡 9 折")
        void gold() {
            var spy = setupCreateSpy(MemberLevel.GOLD);
            assertEquals(0, new BigDecimal("90.00").compareTo(
                    spy.createOrder(1L, 1L, null).getPayAmount()));
        }

        @Test
        @DisplayName("钻石 85 折")
        void diamond() {
            var spy = setupCreateSpy(MemberLevel.DIAMOND);
            assertEquals(0, new BigDecimal("85.00").compareTo(
                    spy.createOrder(1L, 1L, null).getPayAmount()));
        }

        @Test
        @DisplayName("商品已下架 → 异常")
        void offlineProduct() {
            var spy = spy(orderService);
            doReturn(true).when(spy).save(any()); doReturn(1).when(orderLogMapper).insert(any());
            when(cartService.getCartEntities(1L)).thenReturn(checkedCart());
            when(userService.getById(1L)).thenReturn(userWithLevel(0));
            when(productService.getById(100L)).thenReturn(null); // 商品不存在
            assertThrows(RuntimeException.class,
                    () -> spy.createOrder(1L, 1L, null));
        }

        @Test
        @DisplayName("宠物已被购买 → 异常")
        void petSold() {
            var spy = spy(orderService);
            doReturn(true).when(spy).save(any()); doReturn(1).when(orderLogMapper).insert(any());
            when(cartService.getCartEntities(1L)).thenReturn(checkedCart());
            when(userService.getById(1L)).thenReturn(userWithLevel(0));
            Product pet = new Product(); pet.setProductType(1); pet.setStock(0); pet.setStatus(1);
            when(productService.getById(100L)).thenReturn(pet);
            assertThrows(RuntimeException.class,
                    () -> spy.createOrder(1L, 1L, null));
        }

        private OrderServiceImpl setupCreateSpy(int memberLevel) {
            var spy = spy(orderService);
            doReturn(true).when(spy).save(any()); doReturn(1).when(orderLogMapper).insert(any());
            when(cartService.getCartEntities(1L)).thenReturn(checkedCart());
            when(userService.getById(1L)).thenReturn(userWithLevel(memberLevel));
            Product p = new Product(); p.setId(100L); p.setName("狗粮");
            p.setProductType(2); p.setPrice(new BigDecimal("100")); p.setStock(10); p.setStatus(1);
            when(productService.getById(100L)).thenReturn(p, p);
            when(orderItemService.save(any())).thenReturn(true);
            when(productService.deductStock(eq(100L), eq(1))).thenReturn(true);
            when(cartService.removeById(1L)).thenReturn(true);
            return spy;
        }
    }

    // ==================== payOrder ====================
    @Nested
    @DisplayName("payOrder 支付")
    class PayOrder {

        @Test
        @DisplayName("0 → 1：记录支付方式和时间")
        void success() {
            var spy = spy(orderService);
            Order o = order(0); doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            when(userService.getById(anyLong())).thenReturn(null);
            spy.payOrder(1L, "alipay");
            assertEquals(1, o.getStatus()); assertEquals("alipay", o.getPayMethod());
            assertNotNull(o.getPayTime());
        }

        @Test
        @DisplayName("非待支付状态 → 拒绝")
        void wrongStatus() {
            var spy = spy(orderService);
            doReturn(order(1)).when(spy).getById(1L); // 已支付
            assertThrows(RuntimeException.class,
                    () -> spy.payOrder(1L, "alipay"));
        }

        @Test
        @DisplayName("累计消费达标 → 自动升级")
        void upgradeMember() {
            var spy = spy(orderService);
            Order o = order(0); o.setUserId(10L); o.setPayAmount(new BigDecimal("5000"));
            doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            User u = new User(); u.setId(10L); u.setMemberLevel(0);
            when(userService.getById(10L)).thenReturn(u);
            doReturn(Arrays.asList(o)).when(spy).list(any(LambdaQueryWrapper.class));
            doReturn(true).when(userService).updateById(any());
            spy.payOrder(1L, "alipay");
            assertEquals(2, u.getMemberLevel()); // 5000 → 金卡
        }

        @Test
        @DisplayName("升级失败不影响支付")
        void upgradeFailureIgnored() {
            var spy = spy(orderService);
            Order o = order(0);
            doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            when(userService.getById(anyLong())).thenReturn(new User());
            doThrow(new RuntimeException("DB error")).when(spy).list(any(LambdaQueryWrapper.class));
            assertDoesNotThrow(() -> spy.payOrder(1L, "alipay"));
            assertEquals(1, o.getStatus());
        }
    }

    // ==================== cancelOrder ====================
    @Nested
    @DisplayName("cancelOrder 取消订单")
    class CancelOrder {

        @Test
        @DisplayName("0 → -1：恢复宠物库存")
        void cancelRestoresPet() {
            var spy = spy(orderService);
            Order o = order(0); doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            OrderItem item = new OrderItem(); item.setProductId(100L); item.setQuantity(1);
            when(orderItemService.getByOrderId(1L)).thenReturn(Arrays.asList(item));
            Product pet = new Product(); pet.setProductType(1); pet.setStatus(0); pet.setStock(0);
            when(productService.getById(100L)).thenReturn(pet);
            when(productService.updateById(any())).thenReturn(true);
            spy.cancelOrder(1L, "不要了", "USER", false);
            assertEquals(-1, o.getStatus()); assertEquals(1, pet.getStatus());
            assertEquals(1, pet.getStock());
        }

        @Test
        @DisplayName("系统自动取消")
        void autoCancel() {
            var spy = spy(orderService);
            Order o = order(0); doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            when(orderItemService.getByOrderId(1L)).thenReturn(Collections.emptyList());
            spy.cancelOrder(1L, "", "SYSTEM", true);
            assertEquals("超时未支付自动取消", o.getCancelReason());
            assertEquals("SYSTEM", o.getCancelType());
        }

        @Test
        @DisplayName("已完成的订单不可取消")
        void completedCantCancel() {
            var spy = spy(orderService);
            doReturn(order(4)).when(spy).getById(1L);
            assertThrows(RuntimeException.class,
                    () -> spy.cancelOrder(1L, "r", "USER", false));
        }
    }

    // ==================== deliverOrder / confirmReceive / complete ====================
    @Nested
    @DisplayName("发货 → 收货 → 完成")
    class DeliverReceiveComplete {

        @Test
        @DisplayName("1 → 2 发货")
        void deliver() {
            var spy = setupTransition(1);
            spy.deliverOrder(1L, "顺丰", "SF123");
        }

        @Test
        @DisplayName("2 → 3 收货")
        void receive() {
            var spy = setupTransition(2);
            spy.confirmReceive(1L);
        }

        @Test
        @DisplayName("3 → 4 完成")
        void complete() {
            var spy = setupTransition(3);
            spy.completeOrder(1L);
        }

        @Test
        @DisplayName("非当前状态发货 → 拒绝")
        void deliverWrongStatus() {
            var spy = spy(orderService);
            doReturn(order(0)).when(spy).getById(1L);
            assertThrows(RuntimeException.class, () -> spy.deliverOrder(1L, "SF", "1"));
        }

        OrderServiceImpl setupTransition(int status) {
            var spy = spy(orderService);
            Order o = order(status);
            doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any());
            doReturn(1).when(orderLogMapper).insert(any());
            return spy;
        }
    }

    // ==================== 退单流程 ====================
    @Nested
    @DisplayName("退单：申请 → 审核")
    class Refund {

        @Test
        @DisplayName("2 → -2：保存 previousStatus")
        void applyFromDelivered() {
            var spy = spy(orderService);
            Order o = order(2); doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            spy.applyRefund(1L, "质量问题");
            assertEquals(-2, o.getStatus()); assertEquals(2, o.getPreviousStatus());
        }

        @Test
        @DisplayName("3 → -2：从已收货申请退单")
        void applyFromReceived() {
            var spy = spy(orderService);
            Order o = order(3); doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            spy.applyRefund(1L, "不喜欢");
            assertEquals(-2, o.getStatus()); assertEquals(3, o.getPreviousStatus());
        }

        @Test
        @DisplayName("审核通过 -2 → -3：退款+恢复库存")
        void auditApprove() {
            var spy = spy(orderService);
            Order o = order(-2); o.setPreviousStatus(2);
            o.setPayAmount(new BigDecimal("200"));
            doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            OrderItem item = new OrderItem(); item.setProductId(100L); item.setQuantity(2);
            when(orderItemService.getByOrderId(1L)).thenReturn(Arrays.asList(item));
            Product p = new Product(); p.setProductType(2); p.setStock(5);
            when(productService.getById(100L)).thenReturn(p);
            when(productService.updateById(any())).thenReturn(true);
            spy.auditRefund(1L, true, "同意");
            assertEquals(-3, o.getStatus());
            assertEquals(0, new BigDecimal("200").compareTo(o.getRefundMoney()));
            assertEquals(7, p.getStock()); // 5 + 2
        }

        @Test
        @DisplayName("审核拒绝 → 恢复 previousStatus，清除标记")
        void auditReject() {
            var spy = spy(orderService);
            Order o = order(-2); o.setPreviousStatus(3);
            doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            spy.auditRefund(1L, false, "不符合条件");
            assertEquals(3, o.getStatus()); assertNull(o.getPreviousStatus());
        }

        @Test
        @DisplayName("previousStatus=null 兜底为 2")
        void nullPreviousStatusDefault() {
            var spy = spy(orderService);
            Order o = order(-2); o.setPreviousStatus(null);
            doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            spy.auditRefund(1L, false, "拒绝");
            assertEquals(2, o.getStatus());
        }

        @Test
        @DisplayName("非 -2 状态不可审核")
        void wrongStatusForAudit() {
            var spy = spy(orderService);
            doReturn(order(1)).when(spy).getById(1L);
            assertEquals("当前状态不可审核退单",
                    assertThrows(RuntimeException.class,
                            () -> spy.auditRefund(1L, true, "")).getMessage());
        }
    }

    // ==================== adminRefund ====================
    @Nested
    @DisplayName("adminRefund 管理员退单")
    class AdminRefund {

        @Test
        @DisplayName("3 → -4：仅已收货可退")
        void success() {
            var spy = spy(orderService);
            Order o = order(3); o.setPayAmount(new BigDecimal("100"));
            doReturn(o).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any()); doReturn(1).when(orderLogMapper).insert(any());
            when(orderItemService.getByOrderId(1L)).thenReturn(Collections.emptyList());
            spy.adminRefund(1L, "投诉");
            assertEquals(-4, o.getStatus()); assertEquals("ADMIN", o.getCancelType());
        }

        @Test
        @DisplayName("非 3 状态 → 拒绝")
        void wrongStatus() {
            var spy = spy(orderService);
            doReturn(order(2)).when(spy).getById(1L);
            assertEquals("当前状态不可退单", assertThrows(RuntimeException.class,
                    () -> spy.adminRefund(1L, "r")).getMessage());
        }
    }

    // ==================== helpers ====================
    private Order order(int status) {
        Order o = new Order(); o.setId(1L); o.setOrderNo("N" + status);
        o.setUserId(1L); o.setStatus(status);
        o.setTotalAmount(new BigDecimal("100")); o.setPayAmount(new BigDecimal("100"));
        return o;
    }

    private List<Cart> checkedCart() {
        Cart c = new Cart(); c.setId(1L); c.setProductId(100L);
        c.setUserId(1L); c.setQuantity(1); c.setChecked(1);
        return Arrays.asList(c);
    }

    private User userWithLevel(int level) {
        User u = new User(); u.setId(1L); u.setMemberLevel(level); return u;
    }
}
