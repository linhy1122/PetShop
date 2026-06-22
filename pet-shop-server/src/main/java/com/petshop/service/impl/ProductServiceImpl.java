package com.petshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.Product;
import com.petshop.mapper.ProductMapper;
import com.petshop.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品服务实现
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    @Transactional
    public boolean deductStock(Long productId, Integer quantity) {
        Product product = getById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        // 宠物周边（类型2）才检查库存，宠物（类型1）库存固定为1
        if (product.getProductType() == 2 && product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        // 宠物类型：购买后直接下架
        if (product.getProductType() == 1) {
            product.setStatus(0);
            product.setStock(0);
        } else {
            product.setStock(product.getStock() - quantity);
            product.setSales(product.getSales() + quantity);
        }
        return updateById(product);
    }
}
