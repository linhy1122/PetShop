package com.petshop.dto;

import lombok.Data;

/**
 * 微信小程序登录请求
 */
@Data
public class WxLoginDto {
    /** wx.login() 返回的临时code */
    private String code;
}
