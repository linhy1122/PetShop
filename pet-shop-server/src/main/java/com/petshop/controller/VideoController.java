package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.entity.Video;
import com.petshop.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 视频接口
 */
@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /** 视频列表 */
    @GetMapping("/list")
    public Result<Page<Video>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Video::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Video::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Video::getViewCount);
        return Result.ok(videoService.page(new Page<>(page, size), wrapper));
    }

    /** 视频详情 */
    @GetMapping("/{id}")
    public Result<Video> detail(@PathVariable Long id) {
        Video video = videoService.getById(id);
        if (video != null) {
            video.setViewCount(video.getViewCount() + 1);
            videoService.updateById(video);
        }
        return Result.ok(video);
    }

    /** 点赞 */
    @PutMapping("/{id}/like")
    public Result<?> like(@PathVariable Long id) {
        Video video = videoService.getById(id);
        if (video != null) {
            video.setLikeCount(video.getLikeCount() + 1);
            videoService.updateById(video);
        }
        return Result.ok();
    }
}
