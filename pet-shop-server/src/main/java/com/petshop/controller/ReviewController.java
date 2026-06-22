package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.entity.Review;
import com.petshop.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评价接口
 */
@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /** 商品评价列表 */
    @GetMapping("/product/{productId}")
    public Result<Page<Review>> listByProduct(@PathVariable Long productId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getProductId, productId)
               .orderByDesc(Review::getCreateTime);
        return Result.ok(reviewService.page(new Page<>(page, size), wrapper));
    }

    /** 提交评价 */
    @PostMapping("/submit")
    public Result<?> submit(@RequestBody Review review) {
        reviewService.save(review);
        return Result.ok();
    }
}
