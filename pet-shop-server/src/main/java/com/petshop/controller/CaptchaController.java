package com.petshop.controller;

import com.petshop.common.Result;
import com.petshop.util.CaptchaUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 滑块验证码接口
 */
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    /** 获取滑块验证码 */
    @GetMapping
    public Result<Map<String, Object>> getCaptcha() {
        return Result.ok(CaptchaUtil.generate());
    }

    /** 即时校验滑块位置（前端松开滑块时调用） */
    @PostMapping("/verify")
    public Result<?> verify(@RequestBody Map<String, Object> body) {
        String captchaKey = (String) body.get("captchaKey");
        Integer captchaX = (Integer) body.get("captchaX");
        if (captchaKey == null || captchaX == null) {
            return Result.error("参数不完整");
        }
        boolean ok = CaptchaUtil.verify(captchaKey, captchaX);
        return Result.ok(Map.of("passed", ok));
    }
}
