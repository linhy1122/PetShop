package com.petshop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.entity.User;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /** 用户注册 */
    User register(String username, String password, String phone, String captchaKey, Integer captchaX);

    /** 用户登录，返回token */
    String login(String username, String password, String captchaKey, Integer captchaX);

    /** 根据用户名查询 */
    User findByUsername(String username);

    /** 修改密码 */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /** 更新个人信息 */
    void updateProfile(User user);

    /** 根据GitHub ID查询用户 */
    User findByGithubId(Long githubId);

    /** GitHub OAuth登录（自动注册），返回 token + 用户信息 */
    java.util.Map<String, Object> loginByGithub(Long githubId, String githubUsername, String avatarUrl, String email);
}
