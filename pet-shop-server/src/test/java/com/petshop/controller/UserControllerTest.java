package com.petshop.controller;

import com.petshop.dto.LoginDto;
import com.petshop.dto.RegisterDto;
import com.petshop.entity.User;
import com.petshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController")
class UserControllerTest {

    @Mock private UserService userService;
    @InjectMocks private UserController controller;

    @Nested
    @DisplayName("POST /api/user/register")
    class Register {
        @Test
        @DisplayName("正常注册 → userId")
        void ok() {
            User u = new User(); u.setId(5L);
            when(userService.register(anyString(), anyString(), any(), anyString(), any())).thenReturn(u);
            RegisterDto d = new RegisterDto();
            d.setUsername("u"); d.setPassword("p"); d.setCaptchaKey("k");
            var r = controller.register(d);
            assertEquals(200, r.getCode());
            assertEquals(5L, ((Map<?, ?>) r.getData()).get("userId"));
        }
    }

    @Nested
    @DisplayName("POST /api/user/login")
    class Login {
        @Test
        @DisplayName("成功 → token")
        void ok() {
            when(userService.login(anyString(), anyString(), anyString(), any())).thenReturn("token-abc");
            User u = new User(); u.setId(1L); u.setNickname("Test"); u.setRole("user"); u.setMemberLevel(0);
            when(userService.findByUsername("u")).thenReturn(u);
            LoginDto d = new LoginDto();
            d.setUsername("u"); d.setPassword("p"); d.setCaptchaKey("k");
            var r = controller.login(d);
            assertEquals(200, r.getCode());
            assertEquals("token-abc", ((Map<?, ?>) r.getData()).get("token"));
        }

        @Test
        @DisplayName("失败 → code=500")
        void fail() {
            when(userService.login(any(), any(), any(), any()))
                    .thenThrow(new RuntimeException("用户名或密码错误"));
            LoginDto d = new LoginDto();
            d.setUsername("u"); d.setPassword("wrong"); d.setCaptchaKey("k");
            // RuntimeException 会从 Controller 直接抛出，被 GlobalExceptionHandler 处理
            assertThrows(RuntimeException.class, () -> controller.login(d));
        }
    }

    @Nested
    @DisplayName("管理端")
    class Admin {
        @Test void updateStatus() {
            doNothing().when(userService).updateAdminUserStatus(anyLong(), anyInt());
            var r = controller.adminUpdateStatus(1L, 1);
            assertEquals(200, r.getCode());
        }

        @Test void delete() {
            doNothing().when(userService).deleteAdminUser(anyLong());
            var r = controller.adminDelete(2L);
            assertEquals(200, r.getCode());
        }
    }
}
