package com.petshop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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

    @Value("${pet-shop.upload.path:./uploads/}")
    private String uploadPathConfig;

    private String resourceLocation;

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
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}
