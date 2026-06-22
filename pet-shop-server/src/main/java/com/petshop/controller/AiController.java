package com.petshop.controller;

import com.petshop.common.Result;
import com.petshop.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI 智能客服接口
 */
@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    /** 发送消息 */
    @PostMapping("/chat")
    public Result<?> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String sessionId = body.getOrDefault("sessionId", "default");
        String reply = aiService.chat(message, sessionId);
        return Result.ok(Map.of("reply", reply));
    }
}
