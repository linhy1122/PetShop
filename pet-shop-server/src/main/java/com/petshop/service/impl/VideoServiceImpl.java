package com.petshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.dto.VideoDto;
import com.petshop.entity.Video;
import com.petshop.mapper.VideoMapper;
import com.petshop.service.VideoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 视频服务实现
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Override
    public boolean saveVideo(VideoDto dto) {
        Video video = new Video();
        BeanUtils.copyProperties(dto, video);
        if (video.getStatus() == null) video.setStatus(0);
        if (video.getViewCount() == null) video.setViewCount(0);
        if (video.getLikeCount() == null) video.setLikeCount(0);
        if (video.getDuration() == null) video.setDuration(0);
        return save(video);
    }

    @Override
    public boolean updateVideo(Long id, VideoDto dto) {
        Video video = new Video();
        BeanUtils.copyProperties(dto, video);
        video.setId(id);
        return updateById(video);
    }

    @Override
    public boolean deleteVideo(Long id) {
        return removeById(id);
    }
}
