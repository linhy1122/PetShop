package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.dto.VideoDto;
import com.petshop.entity.Video;
import com.petshop.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 视频接口
 */
@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /** 视频列表（可指定 status，不指定默认只看已发布） */
    @GetMapping("/list")
    public Result<Page<Video>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Video::getStatus, status);
        }
        // status 未传时不限制，返回所有状态的视频
        if (categoryId != null) {
            wrapper.eq(Video::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Video::getCreateTime);
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

    /** 新增视频 */
    @PostMapping
    public Result<?> create(@Valid @RequestBody VideoDto dto) {
        videoService.saveVideo(dto);
        return Result.ok("上传成功");
    }

    /** 编辑视频 */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody VideoDto dto) {
        videoService.updateVideo(id, dto);
        return Result.ok("修改成功");
    }

    /** 删除视频 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return Result.ok("删除成功");
    }
}
