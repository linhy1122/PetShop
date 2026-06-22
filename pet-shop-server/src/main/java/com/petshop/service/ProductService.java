package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.entity.Product;

/**
 * 商品服务
 */
public interface ProductService extends IService<Product> {

    /** 减少库存 */
    boolean deductStock(Long productId, Integer quantity);
}
