package com.petshop.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.petshop.service.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * AI 大模型服务实现（通义千问）
 */
@Service
public class AiServiceImpl implements AiService {

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.api-url}")
    private String apiUrl;

    @Override
    public String chat(String userMessage, String sessionId) {
        try {
            JSONObject body = JSONUtil.createObj()
                    .set("model", "qwen-turbo")
                    .set("input", JSONUtil.createObj()
                            .set("messages", new Object[]{
                                    Map.of("role", "system", "content",
                                            "你是一个宠物商店的智能客服助手，负责回答用户关于宠物购买、宠物用品、宠物养护等相关问题。请用中文回答，语气亲切友好。"),
                                    Map.of("role", "user", "content", userMessage)
                            }));

            String response = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(body.toString())
                    .execute()
                    .body();

            JSONObject result = JSONUtil.parseObj(response);
            return result.getByPath("output.text", String.class);
        } catch (Exception e) {
            return "抱歉，AI服务暂时不可用，请稍后再试！";
        }
    }

    @Override
    public void chatStream(String userMessage, String sessionId) {
        // TODO: 实现流式对话（SSE）
    }
}
