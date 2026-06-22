package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.petshop.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    /** 用户名 */
    private String username;
    /** 密码（加密存储） */
    private String password;
    /** 昵称 */
    private String nickname;
    /** 手机号 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** 头像URL */
    private String avatar;
    /** 会员等级：0-普通用户, 1-银卡会员, 2-金卡会员, 3-钻石会员 */
    private Integer memberLevel;
    /** 用户角色：user-普通用户, admin-管理员 */
    private String role;
    /** 账户状态：0-正常, 1-禁用 */
    private Integer status;
}
