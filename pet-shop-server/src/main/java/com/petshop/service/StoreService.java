package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.entity.Store;

import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺服务
 */
public interface StoreService extends IService<Store> {

    /** 根据位置搜索附近店铺 */
    List<Store> searchNearby(BigDecimal lng, BigDecimal lat, Integer radius);
}
