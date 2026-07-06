package com.petshop.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统一返回结果 — JUnit 5 纯单元测试
 */
@DisplayName("Result 统一响应")
class ResultTest {

    @Nested
    @DisplayName("ok() 成功")
    class Ok {

        @Test
        @DisplayName("无参 → code=200, message=success, data=null")
        void noArgs() {
            Result<?> r = Result.ok();
            assertEquals(200, r.getCode());
            assertEquals("success", r.getMessage());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("ok(data) 包裹数据")
        void withData() {
            Result<String> r = Result.ok("hello");
            assertEquals("hello", r.getData());
        }

        @Test
        @DisplayName("ok(msg, data) 自定义消息")
        void withMessage() {
            Result<String> r = Result.ok("操作成功", "value");
            assertEquals("操作成功", r.getMessage());
            assertEquals("value", r.getData());
        }

        @Test
        @DisplayName("支持 null data")
        void nullData() {
            assertNull(Result.ok(null).getData());
        }
    }

    @Nested
    @DisplayName("fail() 失败")
    class Fail {

        @Test
        @DisplayName("fail(msg) → code=500")
        void defaultCode() {
            Result<?> r = Result.fail("服务器错误");
            assertEquals(500, r.getCode());
            assertEquals("服务器错误", r.getMessage());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("fail(code, msg) 自定义状态码")
        void customCode() {
            Result<?> r = Result.fail(503, "服务不可用");
            assertEquals(503, r.getCode());
        }
    }

    @Nested
    @DisplayName("error() 客户端错误")
    class Error {

        @Test
        @DisplayName("error(msg) → code=400")
        void clientError() {
            Result<?> r = Result.error("参数校验失败");
            assertEquals(400, r.getCode());
            assertEquals("参数校验失败", r.getMessage());
        }
    }

    @Test
    @DisplayName("setter/getter 正常")
    void settersWork() {
        Result<String> r = Result.ok();
        r.setCode(201);
        r.setMessage("created");
        r.setData("new");
        assertEquals(201, r.getCode());
        assertEquals("created", r.getMessage());
        assertEquals("new", r.getData());
    }
}
