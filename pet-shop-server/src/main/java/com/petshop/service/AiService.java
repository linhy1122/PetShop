package com.petshop.service;

/**
 * AI 大模型服务
 */
public interface AiService {

    /** 发送消息给AI，返回回答 */
    String chat(String userMessage, String sessionId);

    /** 流式对话 */
    void chatStream(String userMessage, String sessionId);
}
