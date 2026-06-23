package com.petshop.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.petshop.config.GitHubOAuthProperties;
import com.petshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * GitHub OAuth 控制器
 */
@Controller
@RequestMapping("/oauth/github")
public class OAuthController {

    @Autowired
    private GitHubOAuthProperties gitHubOAuth;

    @Autowired
    private UserService userService;

    /** GitHub授权入口 - 重定向到GitHub授权页面 */
    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response) throws IOException {
        String url = StrUtil.format(
                "https://github.com/login/oauth/authorize?client_id={}&redirect_uri={}&scope=user:email",
                gitHubOAuth.getClientId(),
                gitHubOAuth.getRedirectUri()
        );
        response.sendRedirect(url);
    }

    /** GitHub回调 - 用code换token，获取用户信息，登录/注册，重定向回前端 */
    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         HttpServletResponse response) throws IOException {
        try {
            // Step 1: 用code换取access_token
            Map<String, Object> tokenParams = new HashMap<>();
            tokenParams.put("client_id", gitHubOAuth.getClientId());
            tokenParams.put("client_secret", gitHubOAuth.getClientSecret());
            tokenParams.put("code", code);
            tokenParams.put("redirect_uri", gitHubOAuth.getRedirectUri());

            String tokenResult = HttpUtil.createPost("https://github.com/login/oauth/access_token")
                    .header("Accept", "application/json")
                    .form(tokenParams)
                    .execute()
                    .body();

            JSONObject tokenJson = JSONUtil.parseObj(tokenResult);
            String accessToken = tokenJson.getStr("access_token");
            if (StrUtil.isBlank(accessToken)) {
                response.sendRedirect("http://localhost:3000/login?error=oauth_failed");
                return;
            }

            // Step 2: 获取GitHub用户信息
            String userResult = HttpUtil.createGet("https://api.github.com/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .execute()
                    .body();

            JSONObject githubUser = JSONUtil.parseObj(userResult);
            Long githubId = githubUser.getLong("id");
            String login = githubUser.getStr("login");
            String avatarUrl = githubUser.getStr("avatar_url");

            if (githubId == null) {
                response.sendRedirect("http://localhost:3000/login?error=oauth_failed");
                return;
            }

            // Step 3: 获取GitHub邮箱
            String email = "";
            try {
                String emailResult = HttpUtil.createGet("https://api.github.com/user/emails")
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Accept", "application/json")
                        .execute()
                        .body();
                JSONArray emails = JSONUtil.parseArray(emailResult);
                for (int i = 0; i < emails.size(); i++) {
                    JSONObject e = emails.getJSONObject(i);
                    if (e.getBool("primary", false) && e.getBool("verified", false)) {
                        email = e.getStr("email", "");
                        break;
                    }
                }
            } catch (Exception ignored) {
                // 邮箱获取失败不影响登录流程
            }

            // Step 4: 查找或创建用户，生成令牌
            Map<String, Object> loginResult = userService.loginByGithub(githubId, login, avatarUrl, email);

            // Step 5: 重定向到前端回调页面
            String frontendUrl = StrUtil.format(
                    "http://localhost:3000/oauth/callback?token={}&userId={}&nickname={}&role={}",
                    URLEncoder.encode((String) loginResult.get("token"), StandardCharsets.UTF_8),
                    loginResult.get("userId"),
                    URLEncoder.encode((String) loginResult.get("nickname"), StandardCharsets.UTF_8),
                    URLEncoder.encode((String) loginResult.get("role"), StandardCharsets.UTF_8)
            );
            response.sendRedirect(frontendUrl);

        } catch (Exception e) {
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect("http://localhost:3000/login?error=" + errorMsg);
        }
    }
}
