package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 宠物商店
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_store")
public class Store extends BaseEntity {

    /** 店铺名称 */
    private String name;
    /** 店铺简介 */
    private String description;
    /** 店铺图片 */
    private String image;
    /** 联系电话 */
    private String phone;
    /** 营业时间 */
    private String businessHours;
    /** 省份 */
    private String province;
    /** 城市 */
    private String city;
    /** 区/县 */
    private String district;
    /** 详细地址 */
    private String address;
    /** 经度 */
    private BigDecimal longitude;
    /** 纬度 */
    private BigDecimal latitude;
    /** 店铺评分 */
    private BigDecimal rating;
    /** 店铺状态：0-营业中, 1-休息中 */
    private Integer status;
}
