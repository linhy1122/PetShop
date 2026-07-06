package com.petshop.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * 全局异常处理器 — JUnit 5 + Mockito（只 mock BindingResult）
 */
@DisplayName("GlobalExceptionHandler 异常映射")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() { handler = new GlobalExceptionHandler(); }

    @Nested
    @DisplayName("RuntimeException → code=500")
    class Runtime {

        @Test
        @DisplayName("返回异常消息")
        void withMessage() {
            Result<?> r = handler.handleRuntimeException(
                    new RuntimeException("用户名已存在"));
            assertEquals(500, r.getCode());
            assertEquals("用户名已存在", r.getMessage());
        }

        @Test
        @DisplayName("空消息也不崩溃")
        void nullMessage() {
            Result<?> r = handler.handleRuntimeException(new RuntimeException());
            assertEquals(500, r.getCode());
            assertNull(r.getMessage());
        }
    }

    @Nested
    @DisplayName("BindException → code=400")
    class Bind {

        @Test
        @DisplayName("单字段错误")
        void singleField() {
            BindException ex = createBindException(
                    new FieldError("dto", "email", "邮箱格式不正确"));

            Result<?> r = handler.handleBindException(ex);
            assertEquals(400, r.getCode());
            assertEquals("email: 邮箱格式不正确", r.getMessage());
        }

        @Test
        @DisplayName("多字段错误用 ; 串联")
        void multipleFields() {
            BindException ex = createBindException(
                    new FieldError("dto", "username", "用户名不能为空"),
                    new FieldError("dto", "password", "密码不能为空"));

            Result<?> r = handler.handleBindException(ex);
            assertEquals(400, r.getCode());
            assertTrue(r.getMessage().contains("; "));
            assertTrue(r.getMessage().contains("username"));
            assertTrue(r.getMessage().contains("password"));
        }

        @Test
        @DisplayName("无字段错误时默认消息")
        void noFields() {
            BindException ex = createBindException();
            Result<?> r = handler.handleBindException(ex);
            assertEquals("参数校验失败", r.getMessage());
        }
    }

    @Nested
    @DisplayName("Exception → code=500 固定消息")
    class Generic {

        @Test
        @DisplayName("不暴露内部错误详情")
        void hidesDetails() {
            Result<?> r = handler.handleException(new Exception("内部崩溃"));
            assertEquals(500, r.getCode());
            assertEquals("服务器内部错误", r.getMessage());
        }
    }

    /** 构造 BindException（Mock BindingResult） */
    private BindException createBindException(FieldError... errors) {
        BindingResult br = mock(BindingResult.class);
        when(br.getFieldErrors()).thenReturn(
                errors.length == 0 ? Collections.emptyList() : Arrays.asList(errors));
        return new BindException(br);
    }
}
