package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.entity.Store;
import com.petshop.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 店铺接口
 */
@RestController
@RequestMapping("/api/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    /** 分页查询店铺列表 */
    @GetMapping("/list")
    public Result<Page<Store>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String city) {
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 0);
        if (city != null && !city.isEmpty()) {
            wrapper.eq(Store::getCity, city);
        }
        Page<Store> result = storeService.page(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    /** 查询附近店铺 */
    @GetMapping("/nearby")
    public Result<?> nearby(@RequestParam BigDecimal lng,
                            @RequestParam BigDecimal lat,
                            @RequestParam(defaultValue = "5000") Integer radius) {
        return Result.ok(storeService.searchNearby(lng, lat, radius));
    }

    /** 店铺详情 */
    @GetMapping("/{id}")
    public Result<Store> detail(@PathVariable Long id) {
        return Result.ok(storeService.getById(id));
    }
}
