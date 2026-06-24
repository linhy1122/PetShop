package com.petshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品表单 DTO
 */
@Data
public class ProductDto {

    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /** 商品描述 */
    private String description;

    /** 商品详情（富文本 HTML） */
    private String detail;

    /** 商品主图 URL */
    private String mainImage;

    /** 商品图片列表（JSON 数组字符串） */
    private String images;

    /** 所属分类ID */
    @NotNull(message = "请选择商品分类")
    private Long categoryId;

    /** 所属店铺ID */
    @NotNull(message = "请选择所属店铺")
    private Long storeId;

    /** 商品类型：1-宠物, 2-宠物周边 */
    private Integer productType;

    /** 价格 */
    @NotNull(message = "请输入价格")
    private BigDecimal price;

    /** 库存 */
    private Integer stock;

    /** 商品状态：0-下架, 1-上架 */
    private Integer status;

    /** 宠物品种（宠物类型专用） */
    private String breed;

    /** 宠物年龄（宠物类型专用） */
    private String age;

    /** 宠物性别（宠物类型专用）：0-未知, 1-公, 2-母 */
    private Integer gender;
}
