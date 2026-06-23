package com.petshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GitHub OAuth 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "github.oauth")
public class GitHubOAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
