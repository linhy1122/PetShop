package com.petshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.Review;
import com.petshop.mapper.ReviewMapper;
import com.petshop.service.ReviewService;
import org.springframework.stereotype.Service;

/**
 * 评价服务实现
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
}
