package com.petshop.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 创建订单请求
 */
@Data
public class OrderCreateDto {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    private String remark;
}
