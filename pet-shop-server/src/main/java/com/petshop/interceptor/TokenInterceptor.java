package com.petshop.interceptor;

import com.petshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Token 鉴权拦截器
 * 从请求头取 Authorization: Bearer xxx → Redis 查 token → 注入 userId
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 1. 取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }

        String token = authHeader.substring(7);

        // 2. Redis 查 token
        Map<String, Object> tokenInfo = userService.getTokenInfo(token);
        if (tokenInfo == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write("{\"code\":401,\"message\":\"token 无效或已过期，请重新登录\"}");
            return false;
        }

        // 3. 注入 userId 到 request，Controller 可以取出用
        request.setAttribute("userId", tokenInfo.get("userId"));
        request.setAttribute("nickname", tokenInfo.get("nickname"));
        request.setAttribute("role", tokenInfo.get("role"));

        return true;
    }
}
