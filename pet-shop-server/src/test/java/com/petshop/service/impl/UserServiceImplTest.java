package com.petshop.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.config.WechatProperties;
import com.petshop.dto.UserAdminDto;
import com.petshop.entity.Order;
import com.petshop.entity.User;
import com.petshop.mapper.OrderMapper;
import com.petshop.util.CaptchaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试 — @Spy stub MyBatis-Plus 父类方法
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock private OrderMapper orderMapper;
    @Mock private WechatProperties wechatProperties;
    @InjectMocks private UserServiceImpl userService;

    // ==================== register ====================
    @Nested
    @DisplayName("register 注册")
    class Register {

        @Test
        @DisplayName("captchaX=null → 请完成滑块验证")
        void nullCaptchaX() {
            var e = assertThrows(RuntimeException.class,
                    () -> userService.register("u", "p", null, "k", null));
            assertEquals("请完成滑块验证", e.getMessage());
        }

        @Test
        @DisplayName("验证码错误")
        void badCaptcha() {
            try (MockedStatic<CaptchaUtil> c = mockStatic(CaptchaUtil.class)) {
                c.when(() -> CaptchaUtil.verify(anyString(), anyInt())).thenReturn(false);
                var e = assertThrows(RuntimeException.class,
                        () -> userService.register("u", "p", null, "k", 100));
                assertTrue(e.getMessage().contains("验证码"));
            }
        }

        @Test
        @DisplayName("用户名已存在")
        void duplicate() {
            try (MockedStatic<CaptchaUtil> c = mockStatic(CaptchaUtil.class)) {
                c.when(() -> CaptchaUtil.verify(anyString(), anyInt())).thenReturn(true);
                var spy = spy(userService);
                doReturn(new User()).when(spy).getOne(any());

                var e = assertThrows(RuntimeException.class,
                        () -> spy.register("exist", "p", null, "k", 100));
                assertEquals("用户名已存在", e.getMessage());
            }
        }

        @Test
        @DisplayName("正常注册：BCrypt加密、默认昵称、memberLevel=0、role=user")
        void success() {
            try (MockedStatic<CaptchaUtil> c = mockStatic(CaptchaUtil.class)) {
                c.when(() -> CaptchaUtil.verify(anyString(), anyInt())).thenReturn(true);
                var spy = spy(userService);
                doReturn(null).when(spy).getOne(any());
                doReturn(true).when(spy).save(any(User.class));

                User u = spy.register("new", "123", "138", "k", 100);
                assertEquals("new", u.getUsername());
                assertNotEquals("123", u.getPassword());
                assertTrue(u.getNickname().startsWith("用户"));
                assertEquals(0, u.getMemberLevel());
                assertEquals("user", u.getRole());
                assertEquals(0, u.getStatus());
            }
        }
    }

    // ==================== login ====================
    @Nested
    @DisplayName("login 登录")
    class Login {

        @Test
        @DisplayName("用户不存在 → 用户名或密码错误")
        void notFound() {
            try (MockedStatic<CaptchaUtil> c = mockStatic(CaptchaUtil.class)) {
                c.when(() -> CaptchaUtil.verify(anyString(), anyInt())).thenReturn(true);
                var spy = spy(userService);
                doReturn(null).when(spy).getOne(any());
                var e = assertThrows(RuntimeException.class,
                        () -> spy.login("no", "p", "k", 100));
                assertEquals("用户名或密码错误", e.getMessage());
            }
        }

        @Test
        @DisplayName("状态=1 禁用 → 账户已被禁用")
        void disabled() {
            try (MockedStatic<CaptchaUtil> c = mockStatic(CaptchaUtil.class)) {
                c.when(() -> CaptchaUtil.verify(anyString(), anyInt())).thenReturn(true);
                var spy = spy(userService);
                User u = new User(); u.setStatus(1);
                u.setPassword(BCrypt.hashpw("p"));
                doReturn(u).when(spy).getOne(any());
                assertEquals("账户已被禁用",
                        assertThrows(RuntimeException.class,
                                () -> spy.login("u", "p", "k", 100)).getMessage());
            }
        }

        @Test
        @DisplayName("密码错误")
        void wrongPwd() {
            try (MockedStatic<CaptchaUtil> c = mockStatic(CaptchaUtil.class)) {
                c.when(() -> CaptchaUtil.verify(anyString(), anyInt())).thenReturn(true);
                var spy = spy(userService);
                User u = new User(); u.setStatus(0);
                u.setPassword(BCrypt.hashpw("correct"));
                doReturn(u).when(spy).getOne(any());
                assertEquals("用户名或密码错误",
                        assertThrows(RuntimeException.class,
                                () -> spy.login("u", "wrong", "k", 100)).getMessage());
            }
        }

        @Test
        @DisplayName("成功 → token 以 token- 开头")
        void success() {
            try (MockedStatic<CaptchaUtil> c = mockStatic(CaptchaUtil.class)) {
                c.when(() -> CaptchaUtil.verify(anyString(), anyInt())).thenReturn(true);
                var spy = spy(userService);
                User u = new User(); u.setStatus(0);
                u.setPassword(BCrypt.hashpw("p"));
                doReturn(u).when(spy).getOne(any());
                assertTrue(spy.login("u", "p", "k", 100).startsWith("token-"));
            }
        }
    }

    // ==================== updatePassword ====================
    @Nested
    @DisplayName("updatePassword 修改密码")
    class UpdatePassword {

        @Test
        @DisplayName("用户不存在")
        void notFound() {
            var spy = spy(userService);
            doReturn(null).when(spy).getById(999L);
            assertEquals("用户不存在", assertThrows(RuntimeException.class,
                    () -> spy.updatePassword(999L, "old", "new")).getMessage());
        }

        @Test
        @DisplayName("原密码错误")
        void wrongOld() {
            var spy = spy(userService);
            User u = new User(); u.setPassword(BCrypt.hashpw("correct"));
            doReturn(u).when(spy).getById(1L);
            assertEquals("原密码错误", assertThrows(RuntimeException.class,
                    () -> spy.updatePassword(1L, "wrong", "new")).getMessage());
        }

        @Test
        @DisplayName("成功更新")
        void success() {
            var spy = spy(userService);
            User u = new User(); u.setPassword(BCrypt.hashpw("old"));
            doReturn(u).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any());
            assertDoesNotThrow(() -> spy.updatePassword(1L, "old", "new"));
            verify(spy).updateById(any(User.class));
        }
    }

    // ==================== updateProfile ====================
    @Nested
    @DisplayName("updateProfile 修改资料")
    class UpdateProfile {

        @Test
        @DisplayName("只更新非空字段")
        void partialUpdate() {
            var spy = spy(userService);
            User db = new User(); db.setId(1L);
            db.setNickname("旧昵称"); db.setPhone("旧电话");
            doReturn(db).when(spy).getById(1L);
            doReturn(true).when(spy).updateById(any());

            User input = new User(); input.setId(1L);
            input.setNickname("新昵称");
            // phone 和 email 为 null → 不覆盖
            spy.updateProfile(input);

            ArgumentCaptor<User> c = ArgumentCaptor.forClass(User.class);
            verify(spy).updateById(c.capture());
            assertEquals("新昵称", c.getValue().getNickname());
            assertEquals("旧电话", c.getValue().getPhone()); // 未被覆盖
        }
    }

    // ==================== GitHub OAuth ====================
    @Nested
    @DisplayName("loginByGithub")
    class LoginByGithub {

        @Test
        @DisplayName("新用户 → 自动注册")
        void newUser() {
            var spy = spy(userService);
            doReturn(null).doReturn(null).when(spy).getOne(any());
            // save 成功后设置 ID，避免 Map.of() 遇到 null
            doAnswer(inv -> { ((User) inv.getArgument(0)).setId(100L); return true; })
                    .when(spy).save(any());
            Map<String, Object> r = spy.loginByGithub(123L, "gh", "av", "em");
            assertTrue(((String) r.get("token")).startsWith("token-"));
            assertEquals("user", r.get("role"));
            assertEquals(100L, r.get("userId"));
        }

        @Test
        @DisplayName("已存在 → 直接登录，不重复 save")
        void existing() {
            var spy = spy(userService);
            User u = new User(); u.setId(10L); u.setStatus(0);
            u.setNickname("gh"); u.setRole("user"); u.setMemberLevel(0);
            doReturn(u).when(spy).getOne(any());
            Map<String, Object> r = spy.loginByGithub(123L, "gh", null, null);
            assertEquals(10L, r.get("userId"));
            verify(spy, never()).save(any());
        }

        @Test
        @DisplayName("已禁用 → 拒绝登录")
        void disabled() {
            var spy = spy(userService);
            User u = new User(); u.setStatus(1);
            doReturn(u).when(spy).getOne(any());
            assertEquals("账户已被禁用", assertThrows(RuntimeException.class,
                    () -> spy.loginByGithub(123L, "gh", null, null)).getMessage());
        }
    }

    // ==================== Admin CRUD ====================
    @Nested
    @DisplayName("管理员CRUD")
    class AdminCrud {

        @Test
        @DisplayName("禁用管理员账号 → 拒绝")
        void disableAdmin() {
            var spy = spy(userService);
            User admin = new User(); admin.setRole("admin"); admin.setDeleted(0);
            doReturn(admin).when(spy).getById(1L);
            UserAdminDto dto = new UserAdminDto(); dto.setMemberLevel(0); dto.setStatus(1);
            assertEquals("管理员账号不能被禁用", assertThrows(RuntimeException.class,
                    () -> spy.updateAdminUser(1L, dto)).getMessage());
        }

        @Test
        @DisplayName("删除管理员 → 拒绝")
        void deleteAdmin() {
            var spy = spy(userService);
            User admin = new User(); admin.setRole("admin"); admin.setDeleted(0);
            doReturn(admin).when(spy).getById(1L);
            assertEquals("管理员账号不能删除", assertThrows(RuntimeException.class,
                    () -> spy.deleteAdminUser(1L)).getMessage());
        }

        @Test
        @DisplayName("删除有订单的用户 → 拒绝")
        void deleteUserWithOrders() {
            var spy = spy(userService);
            User u = new User(); u.setRole("user"); u.setDeleted(0);
            doReturn(u).when(spy).getById(2L);
            when(orderMapper.selectCount(any())).thenReturn(3L);
            assertEquals("该用户存在订单，不能删除", assertThrows(RuntimeException.class,
                    () -> spy.deleteAdminUser(2L)).getMessage());
        }

        @Test
        @DisplayName("成功删除无订单用户")
        void deleteCleanUser() {
            var spy = spy(userService);
            User u = new User(); u.setRole("user"); u.setDeleted(0);
            doReturn(u).when(spy).getById(3L);
            when(orderMapper.selectCount(any())).thenReturn(0L);
            doReturn(true).when(spy).removeById(3L);
            assertDoesNotThrow(() -> spy.deleteAdminUser(3L));
        }

        @Test
        @DisplayName("创建管理员 → role=admin, BCrypt 加密")
        void createAdmin() {
            var spy = spy(userService);
            doReturn(null).when(spy).getOne(any());
            doReturn(true).when(spy).save(any());
            User input = new User(); input.setUsername("a"); input.setPassword("123");
            spy.adminCreateUser(input);
            ArgumentCaptor<User> c = ArgumentCaptor.forClass(User.class);
            verify(spy).save(c.capture());
            assertEquals("admin", c.getValue().getRole());
            assertNotEquals("123", c.getValue().getPassword());
        }
    }
}
