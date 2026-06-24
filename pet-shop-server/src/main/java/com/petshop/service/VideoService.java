package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.dto.VideoDto;
import com.petshop.entity.Video;

/**
 * 视频服务
 */
public interface VideoService extends IService<Video> {

    /** 新增视频 */
    boolean saveVideo(VideoDto dto);

    /** 编辑视频 */
    boolean updateVideo(Long id, VideoDto dto);

    /** 删除视频（逻辑删除） */
    boolean deleteVideo(Long id);
}
