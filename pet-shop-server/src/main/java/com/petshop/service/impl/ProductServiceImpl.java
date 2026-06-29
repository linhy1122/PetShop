package com.petshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.dto.ProductDto;
import com.petshop.entity.Product;
import com.petshop.mapper.ProductMapper;
import com.petshop.service.ProductService;
import org.springframework.beans.BeanUtils;
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
        // 宠物类型：购买后直接下架，需检查是否已被他人购买
        if (product.getProductType() == 1) {
            if (product.getStatus() == 0 || product.getStock() == 0) {
                throw new RuntimeException("该宠物已被其他用户购买");
            }
            product.setStatus(0);
            product.setStock(0);
        } else {
            product.setStock(product.getStock() - quantity);
            product.setSales(product.getSales() + quantity);
        }
        return updateById(product);
    }

    @Override
    public boolean saveProduct(ProductDto dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        if (product.getStatus() == null) {
            product.setStatus(1);
        }
        if (product.getStock() == null) {
            product.setStock(0);
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        if (product.getImages() == null) {
            product.setImages("[]");
        }
        return save(product);
    }

    @Override
    public boolean updateProduct(Long id, ProductDto dto) {
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setId(id);
        return updateById(product);
    }

    @Override
    public boolean deleteProduct(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateStatus(Long productId, Integer status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        return updateById(product);
    }
}
