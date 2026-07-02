package com.petshop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.dto.ProductDto;
import com.petshop.entity.Product;
import com.petshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商品接口
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /** 分页查询商品列表（管理端可传 status 查看全部/下架商品） */
    @GetMapping("/list")
    public Result<Page<Product>> list(@RequestParam(defaultValue = "1") Integer page,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) Long storeId,
                                       @RequestParam(required = false) Integer productType,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) String sortBy) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        } else {
            wrapper.eq(Product::getStatus, 1);
        }
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        if (storeId != null) {
            wrapper.eq(Product::getStoreId, storeId);
        }
        if (productType != null) {
            wrapper.eq(Product::getProductType, productType);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword);
        }
        // 排序：综合(默认)=销量降序, price_asc=价格升序, price_desc=价格降序, sales=销量降序
        if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(sortBy)) {
            wrapper.orderByDesc(Product::getPrice);
        } else {
            // 默认综合排序：按销量降序
            wrapper.orderByDesc(Product::getSales);
        }
        Page<Product> result = productService.page(new Page<>(page, size), wrapper);
        return Result.ok(result);
    }

    /** 商品详情 */
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }

    /** 热门商品 */
    @GetMapping("/hot")
    public Result<?> hot(@RequestParam(defaultValue = "8") Integer limit) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1)
               .orderByDesc(Product::getSales)
               .last("LIMIT " + limit);
        return Result.ok(productService.list(wrapper));
    }

    /** 新增商品 */
    @PostMapping
    public Result<?> create(@Valid @RequestBody ProductDto dto) {
        productService.saveProduct(dto);
        return Result.ok("新增成功");
    }

    /** 编辑商品 */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @Valid @RequestBody ProductDto dto) {
        productService.updateProduct(id, dto);
        return Result.ok("修改成功");
    }

    /** 删除商品 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.ok("删除成功");
    }

    /** 上下架 */
    @PutMapping("/status")
    public Result<?> status(@RequestParam Long productId, @RequestParam Integer status) {
        productService.updateStatus(productId, status);
        return Result.ok("状态更新成功");
    }
}
