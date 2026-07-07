package com.petshop;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 宠物商店 - 启动类
 */
@Slf4j
@SpringBootApplication
@MapperScan("com.petshop.mapper")
@EnableScheduling
public class PetShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetShopApplication.class, args);
        log.info("========================================");
        log.info("  宠物商店后端服务启动成功！");
        log.info("  API文档: http://localhost:8080/doc.html");
        log.info("========================================");
    }
}
