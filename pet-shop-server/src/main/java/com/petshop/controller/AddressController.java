package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.common.Result;
import com.petshop.entity.Address;
import com.petshop.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址接口
 */
@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressMapper addressMapper;

    /** 用户地址列表 */
    @GetMapping("/user/{userId}")
    public Result<List<Address>> listByUser(@PathVariable Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .orderByDesc(Address::getIsDefault);
        return Result.ok(addressMapper.selectList(wrapper));
    }

    /** 新增地址 */
    @PostMapping
    public Result<?> add(@RequestBody Address address) {
        addressMapper.insert(address);
        return Result.ok();
    }

    /** 修改地址 */
    @PutMapping
    public Result<?> update(@RequestBody Address address) {
        addressMapper.updateById(address);
        return Result.ok();
    }

    /** 删除地址 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        addressMapper.deleteById(id);
        return Result.ok();
    }

    /** 设置默认地址 */
    @PutMapping("/{id}/default")
    public Result<?> setDefault(@PathVariable Long id, @RequestParam Long userId) {
        // 取消其他默认
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId)
               .eq(Address::getIsDefault, 1);
        Address oldDefault = addressMapper.selectOne(wrapper);
        if (oldDefault != null) {
            oldDefault.setIsDefault(0);
            addressMapper.updateById(oldDefault);
        }
        // 设置新默认
        Address address = addressMapper.selectById(id);
        if (address != null) {
            address.setIsDefault(1);
            addressMapper.updateById(address);
        }
        return Result.ok();
    }
}
