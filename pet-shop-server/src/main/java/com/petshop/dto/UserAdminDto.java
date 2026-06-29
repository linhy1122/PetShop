package com.petshop.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 后台编辑用户表单，仅包含允许管理员修改的字段。
 */
@Data
public class UserAdminDto {

    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @Size(max = 500, message = "头像地址不能超过500个字符")
    private String avatar;

    @NotNull(message = "会员等级不能为空")
    @Min(value = 0, message = "会员等级只能是0到3")
    @Max(value = 3, message = "会员等级只能是0到3")
    private Integer memberLevel;

    @NotNull(message = "用户状态不能为空")
    @Min(value = 0, message = "用户状态只能是0或1")
    @Max(value = 1, message = "用户状态只能是0或1")
    private Integer status;
}
