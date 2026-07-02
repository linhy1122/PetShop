package com.petshop.config;

import com.petshop.interceptor.TokenInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * Web MVC 配置
 */
@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Value("${pet-shop.upload.path:./uploads/}")
    private String uploadPathConfig;

    private String resourceLocation;

    /** 无需鉴权的公开路径 */
    private static final String[] PUBLIC_PATHS = {
            "/api/user/login",
            "/api/user/register",
            "/api/captcha/**",
            "/api/oauth/**",
            "/api/product/**",           // 商品浏览（list/hot/detail）公开
            "/api/category/**",
            "/api/store/**",
            "/api/video/**",
            "/uploads/**",
            "/doc.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**"
    };

    @PostConstruct
    public void init() {
        File dir = new File(uploadPathConfig);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("user.dir"), uploadPathConfig);
        }
        // 确保以 / 结尾，Spring 资源映射要求
        String absPath = dir.getAbsolutePath().replace('\\', '/');
        if (!absPath.endsWith("/")) {
            absPath += "/";
        }
        resourceLocation = "file:" + absPath;
        log.info("静态资源映射: /uploads/** → {}", resourceLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**")        // 拦截所有 /api/ 请求
                .excludePathPatterns(PUBLIC_PATHS); // 放过公开路径
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
