package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product")
public class Product extends BaseEntity {

    /** 商品名称 */
    private String name;
    /** 商品描述 */
    private String description;
    /** 商品详情（富文本） */
    private String detail;
    /** 商品主图 */
    private String mainImage;
    /** 商品图片列表（JSON数组） */
    private String images;
    /** 所属分类ID */
    private Long categoryId;
    /** 所属店铺ID */
    private Long storeId;
    /** 商品类型：1-宠物（唯一）, 2-宠物周边（不限量） */
    private Integer productType;
    /** 价格 */
    private BigDecimal price;
    /** 库存（宠物类型固定为1） */
    private Integer stock;
    /** 销量 */
    private Integer sales;
    /** 商品状态：0-下架, 1-上架 */
    private Integer status;
    /** 宠物品种（宠物类型专用） */
    private String breed;
    /** 宠物年龄（宠物类型专用） */
    private String age;
    /** 宠物性别（宠物类型专用）：0-未知, 1-公, 2-母 */
    private Integer gender;
}
