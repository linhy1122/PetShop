package com.petshop.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 滑块验证码 — JUnit 5 纯单元测试（无 Mock，直接用真实生成/验证）
 */
@DisplayName("CaptchaUtil 滑块验证码")
class CaptchaUtilTest {

    @Nested
    @DisplayName("generate() 生成")
    class Generate {

        @Test
        @DisplayName("返回 4 个必要字段")
        void allKeysPresent() {
            Map<String, Object> result = CaptchaUtil.generate();
            assertNotNull(result.get("captchaKey"));
            assertNotNull(result.get("bgImage"));
            assertNotNull(result.get("sliderImage"));
            assertNotNull(result.get("sliderY"));
        }

        @Test
        @DisplayName("captchaKey 格式 = Base64.HMAC(十六进制)")
        void keyFormat() {
            String key = (String) CaptchaUtil.generate().get("captchaKey");
            String[] parts = key.split("\\.");
            assertEquals(2, parts.length, "应由 . 分隔");
            assertDoesNotThrow(() -> Base64.getDecoder().decode(parts[0]),
                    "第一部分应为 Base64");
            assertTrue(parts[1].matches("[0-9a-f]+"),
                    "第二部分应为 HMAC 十六进制签名");
        }

        @Test
        @DisplayName("bgImage 是 PNG data URI")
        void bgImageFormat() {
            String bg = (String) CaptchaUtil.generate().get("bgImage");
            assertTrue(bg.startsWith("data:image/png;base64,"));
        }

        @RepeatedTest(3)
        @DisplayName("每次生成不同的 key")
        void randomX() {
            String k1 = (String) CaptchaUtil.generate().get("captchaKey");
            String k2 = (String) CaptchaUtil.generate().get("captchaKey");
            assertNotEquals(k1, k2);
        }
    }

    @Nested
    @DisplayName("verify() 校验")
    class Verify {

        @Test
        @DisplayName("正确 X → true")
        void correctPosition() {
            Map<String, Object> cap = CaptchaUtil.generate();
            String key = (String) cap.get("captchaKey");
            int x = extractX(key);
            assertTrue(CaptchaUtil.verify(key, x));
        }

        @Test
        @DisplayName("容差边界 ±5 → true")
        void toleranceBoundary() {
            Map<String, Object> cap = CaptchaUtil.generate();
            String key = (String) cap.get("captchaKey");
            int x = extractX(key);
            assertTrue(CaptchaUtil.verify(key, x + 5), "偏移 +5 应通过");
            assertTrue(CaptchaUtil.verify(key, x - 5), "偏移 -5 应通过");
        }

        @Test
        @DisplayName("超出容差 → false")
        void outsideTolerance() {
            Map<String, Object> cap = CaptchaUtil.generate();
            String key = (String) cap.get("captchaKey");
            int x = extractX(key);
            assertFalse(CaptchaUtil.verify(key, x + 6));
            assertFalse(CaptchaUtil.verify(key, x - 6));
        }

        @Test
        @DisplayName("篡改签名 → false")
        void tampered() {
            Map<String, Object> cap = CaptchaUtil.generate();
            String key = (String) cap.get("captchaKey");
            String[] parts = key.split("\\.");
            // 修改 payload
            String fakePayload = Base64.getEncoder().encodeToString(
                    "{\"x\":999,\"t\":0}".getBytes());
            String fakeKey = fakePayload + "." + parts[1];
            assertFalse(CaptchaUtil.verify(fakeKey, 999));
        }

        @Test
        @DisplayName("null / 空 / 格式错误 → false")
        void invalidKey() {
            assertFalse(CaptchaUtil.verify(null, 100));
            assertFalse(CaptchaUtil.verify("", 100));
            assertFalse(CaptchaUtil.verify("no-dot", 100));
            assertFalse(CaptchaUtil.verify("a.b.c", 100));
        }
    }

    /** 从 captchaKey 中解码出正确的 X 位置 */
    private static int extractX(String key) {
        String payload = new String(Base64.getDecoder().decode(key.split("\\.")[0]));
        return Integer.parseInt(payload.replaceAll(".*\"x\":(\\d+).*", "$1"));
    }
}
