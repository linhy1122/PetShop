package com.petshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.Video;
import com.petshop.mapper.VideoMapper;
import com.petshop.service.VideoService;
import org.springframework.stereotype.Service;

/**
 * 视频服务实现
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
}
