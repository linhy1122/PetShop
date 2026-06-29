package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.dto.StoreDto;
import com.petshop.entity.Store;
import com.petshop.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    /** 管理端分页查询全部状态店铺 */
    @GetMapping("/admin/list")
    public Result<Page<Store>> adminList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String name,
                                          @RequestParam(required = false) String city,
                                          @RequestParam(required = false) Integer status) {
        return Result.ok(storeService.pageAdminStores(page, size, name, city, status));
    }

    /** 管理端新增店铺 */
    @PostMapping("/admin")
    public Result<Store> adminCreate(@Valid @RequestBody StoreDto dto) {
        return Result.ok("新增店铺成功", storeService.createStore(dto));
    }

    /** 管理端编辑店铺 */
    @PutMapping("/admin/{id}")
    public Result<?> adminUpdate(@PathVariable Long id, @Valid @RequestBody StoreDto dto) {
        storeService.updateStore(id, dto);
        return Result.ok("修改店铺成功", null);
    }

    /** 管理端删除店铺 */
    @DeleteMapping("/admin/{id}")
    public Result<?> adminDelete(@PathVariable Long id) {
        storeService.deleteStore(id);
        return Result.ok("删除店铺成功", null);
    }

    /** 管理端切换店铺营业状态 */
    @PutMapping("/admin/{id}/status")
    public Result<?> adminUpdateStatus(@PathVariable Long id,
                                        @RequestParam Integer status) {
        storeService.updateStoreStatus(id, status);
        return Result.ok("店铺状态修改成功", null);
    }
}
