package com.petshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.dto.StoreDto;
import com.petshop.entity.Store;

import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺服务
 */
public interface StoreService extends IService<Store> {

    /** 根据位置搜索附近店铺 */
    List<Store> searchNearby(BigDecimal lng, BigDecimal lat, Integer radius);

    /** 管理端分页查询全部状态店铺 */
    Page<Store> pageAdminStores(Integer page, Integer size, String name, String city, Integer status);

    /** 管理端新增店铺 */
    Store createStore(StoreDto dto);

    /** 管理端编辑店铺 */
    void updateStore(Long id, StoreDto dto);

    /** 管理端删除没有关联商品的店铺 */
    void deleteStore(Long id);

    /** 管理端修改店铺营业状态 */
    void updateStoreStatus(Long id, Integer status);
}
