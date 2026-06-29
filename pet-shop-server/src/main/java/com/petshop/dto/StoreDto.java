package com.petshop.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 后台店铺新增、编辑表单。
 * 仅包含允许管理端提交的字段，避免修改主键、创建时间和删除标记等系统字段。
 */
@Data
public class StoreDto {

    @NotBlank(message = "店铺名称不能为空")
    @Size(max = 100, message = "店铺名称不能超过100个字符")
    private String name;

    @Size(max = 500, message = "店铺简介不能超过500个字符")
    private String description;

    @Size(max = 500, message = "店铺图片地址不能超过500个字符")
    private String image;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String phone;

    @Size(max = 100, message = "营业时间不能超过100个字符")
    private String businessHours;

    @Size(max = 50, message = "省份不能超过50个字符")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 50, message = "城市不能超过50个字符")
    private String city;

    @Size(max = 50, message = "区县不能超过50个字符")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址不能超过255个字符")
    private String address;

    @DecimalMin(value = "-180", message = "经度不能小于-180")
    @DecimalMax(value = "180", message = "经度不能大于180")
    private BigDecimal longitude;

    @DecimalMin(value = "-90", message = "纬度不能小于-90")
    @DecimalMax(value = "90", message = "纬度不能大于90")
    private BigDecimal latitude;

    @DecimalMin(value = "0", message = "店铺评分不能小于0")
    @DecimalMax(value = "5", message = "店铺评分不能大于5")
    private BigDecimal rating;

    @NotNull(message = "店铺状态不能为空")
    @Min(value = 0, message = "店铺状态只能是0或1")
    @Max(value = 1, message = "店铺状态只能是0或1")
    private Integer status;
}
