package com.petshop.controller;

import com.petshop.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传接口
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${pet-shop.upload.path:./uploads/}")
    private String uploadPathConfig;

    private File uploadDir;

    @PostConstruct
    public void init() {
        uploadDir = new File(uploadPathConfig);
        if (!uploadDir.isAbsolute()) {
            uploadDir = new File(System.getProperty("user.dir"), uploadPathConfig);
        }
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if (created) {
                log.info("已创建上传目录: {}", uploadDir.getAbsolutePath());
            }
        }
        log.info("文件上传目录: {}", uploadDir.getAbsolutePath());
    }

    /** 上传图片 */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + ext;

        try {
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);
            log.info("文件上传成功: {}", dest.getAbsolutePath());
            return Result.ok("/uploads/" + fileName);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
