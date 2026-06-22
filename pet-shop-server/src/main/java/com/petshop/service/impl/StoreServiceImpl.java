package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.Store;
import com.petshop.mapper.StoreMapper;
import com.petshop.service.StoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺服务实现
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    /**
     * 简易地理位置搜索（利用MySQL计算直线距离）
     * TODO: 后续可替换为高德地图POI搜索API
     */
    @Override
    public List<Store> searchNearby(BigDecimal lng, BigDecimal lat, Integer radius) {
        // 简易实现：根据城市查询，后续集成高德地图API
        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getStatus, 0)
               .orderByAsc(Store::getRating);
        // TODO: 使用经纬度范围筛选 + 距离计算
        return list(wrapper);
    }
}
