package com.petshop.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求
 */
@Data
public class LoginDto {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captchaKey;

    private Integer captchaX;
}
