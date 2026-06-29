package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.dto.StoreDto;
import com.petshop.entity.Product;
import com.petshop.entity.Store;
import com.petshop.mapper.ProductMapper;
import com.petshop.mapper.StoreMapper;
import com.petshop.service.StoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 店铺服务实现
 */
@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements StoreService {

    private final ProductMapper productMapper;

    public StoreServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

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

    @Override
    public Page<Store> pageAdminStores(Integer page, Integer size, String name,
                                        String city, Integer status) {
        validateStatusWhenPresent(status);
        long current = page == null ? 1 : Math.max(page, 1);
        long pageSize = size == null ? 10 : Math.min(Math.max(size, 1), 100);

        LambdaQueryWrapper<Store> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Store::getDeleted, 0);
        if (hasText(name)) {
            wrapper.like(Store::getName, name.trim());
        }
        if (hasText(city)) {
            wrapper.eq(Store::getCity, city.trim());
        }
        if (status != null) {
            wrapper.eq(Store::getStatus, status);
        }
        wrapper.orderByDesc(Store::getCreateTime);
        return page(new Page<>(current, pageSize), wrapper);
    }

    @Override
    @Transactional
    public Store createStore(StoreDto dto) {
        Store store = new Store();
        copyEditableFields(dto, store);
        LocalDateTime now = LocalDateTime.now();
        store.setCreateTime(now);
        store.setUpdateTime(now);
        store.setDeleted(0);
        if (!save(store)) {
            throw new RuntimeException("新增店铺失败");
        }
        return store;
    }

    @Override
    @Transactional
    public void updateStore(Long id, StoreDto dto) {
        Store store = getExistingStore(id);
        copyEditableFields(dto, store);
        store.setUpdateTime(LocalDateTime.now());
        if (!updateById(store)) {
            throw new RuntimeException("修改店铺失败");
        }
    }

    @Override
    @Transactional
    public void deleteStore(Long id) {
        Store store = getExistingStore(id);
        LambdaQueryWrapper<Product> productWrapper = new LambdaQueryWrapper<>();
        productWrapper.eq(Product::getStoreId, id)
                .eq(Product::getDeleted, 0);
        if (productMapper.selectCount(productWrapper) > 0) {
            throw new RuntimeException("该店铺下存在商品，不能删除");
        }
        // 项目当前未启用 @TableLogic，沿用现有 removeById 删除方式。
        if (!removeById(store.getId())) {
            throw new RuntimeException("删除店铺失败");
        }
    }

    @Override
    @Transactional
    public void updateStoreStatus(Long id, Integer status) {
        validateStatus(status);
        Store store = getExistingStore(id);
        store.setStatus(status);
        store.setUpdateTime(LocalDateTime.now());
        if (!updateById(store)) {
            throw new RuntimeException("店铺状态修改失败");
        }
    }

    private Store getExistingStore(Long id) {
        if (id == null) {
            throw new RuntimeException("店铺ID不能为空");
        }
        Store store = getById(id);
        if (store == null || Integer.valueOf(1).equals(store.getDeleted())) {
            throw new RuntimeException("店铺不存在");
        }
        return store;
    }

    private void copyEditableFields(StoreDto dto, Store store) {
        validateStatus(dto.getStatus());
        store.setName(dto.getName().trim());
        store.setDescription(trimToEmpty(dto.getDescription()));
        store.setImage(trimToEmpty(dto.getImage()));
        store.setPhone(dto.getPhone().trim());
        store.setBusinessHours(trimToEmpty(dto.getBusinessHours()));
        store.setProvince(trimToEmpty(dto.getProvince()));
        store.setCity(dto.getCity().trim());
        store.setDistrict(trimToEmpty(dto.getDistrict()));
        store.setAddress(dto.getAddress().trim());
        store.setLongitude(dto.getLongitude() == null ? BigDecimal.ZERO : dto.getLongitude());
        store.setLatitude(dto.getLatitude() == null ? BigDecimal.ZERO : dto.getLatitude());
        store.setRating(dto.getRating() == null ? new BigDecimal("5.0") : dto.getRating());
        store.setStatus(dto.getStatus());
    }

    private void validateStatusWhenPresent(Integer status) {
        if (status != null) {
            validateStatus(status);
        }
    }

    private void validateStatus(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("店铺状态只能是0或1");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
