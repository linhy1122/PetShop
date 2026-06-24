package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.dto.ProductDto;
import com.petshop.entity.Product;

/**
 * 商品服务
 */
public interface ProductService extends IService<Product> {

    /** 减少库存 */
    boolean deductStock(Long productId, Integer quantity);

    /** 新增商品 */
    boolean saveProduct(ProductDto dto);

    /** 编辑商品 */
    boolean updateProduct(Long id, ProductDto dto);

    /** 删除商品（逻辑删除） */
    boolean deleteProduct(Long id);

    /** 上下架 */
    boolean updateStatus(Long productId, Integer status);
}
