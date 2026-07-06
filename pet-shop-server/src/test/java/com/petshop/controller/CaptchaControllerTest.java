package com.petshop.controller;

import com.petshop.util.CaptchaUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CaptchaController")
class CaptchaControllerTest {

    private final CaptchaController controller = new CaptchaController();

    @Test
    @DisplayName("GET /api/captcha → 4 个字段")
    void generate() {
        var r = controller.getCaptcha();
        assertEquals(200, r.getCode());
        Map<?, ?> data = (Map<?, ?>) r.getData();
        assertNotNull(data.get("captchaKey"));
        assertNotNull(data.get("bgImage"));
        assertNotNull(data.get("sliderImage"));
        assertNotNull(data.get("sliderY"));
    }

    @Test
    @DisplayName("POST /api/captcha/verify 正确 → passed=true")
    void verifyOk() {
        Map<String, Object> cap = CaptchaUtil.generate();
        String key = (String) cap.get("captchaKey");
        int x = extractX(key);
        Map<String, Object> body = Map.of("captchaKey", key, "captchaX", x);
        var r = controller.verify(body);
        assertEquals(200, r.getCode());
        assertTrue((Boolean) ((Map<?, ?>) r.getData()).get("passed"));
    }

    @Test
    @DisplayName("POST /api/captcha/verify 错误 → passed=false")
    void verifyFail() {
        String key = (String) CaptchaUtil.generate().get("captchaKey");
        Map<String, Object> body = Map.of("captchaKey", key, "captchaX", 9999);
        var r = controller.verify(body);
        assertFalse((Boolean) ((Map<?, ?>) r.getData()).get("passed"));
    }

    private int extractX(String key) {
        String payload = new String(Base64.getDecoder().decode(key.split("\\.")[0]));
        return Integer.parseInt(payload.replaceAll(".*\"x\":(\\d+).*", "$1"));
    }
}
