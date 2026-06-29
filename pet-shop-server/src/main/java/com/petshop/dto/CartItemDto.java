package com.petshop.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车项 DTO —— 聚合商品信息
 */
@Data
public class CartItemDto {

    /** 购物车记录ID */
    private Long id;
    /** 商品ID */
    private Long productId;
    /** 商品名称 */
    private String productName;
    /** 商品主图 */
    private String productImage;
    /** 商品单价 */
    private BigDecimal price;
    /** 商品类型：1-宠物, 2-周边 */
    private Integer productType;
    /** 是否已下架 */
    private Integer productStatus;
    /** 当前库存 */
    private Integer stock;
    /** 购买数量 */
    private Integer quantity;
    /** 是否选中：0-否, 1-是 */
    private Integer checked;
}
