package com.petshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petshop.dto.UserAdminDto;
import com.petshop.entity.User;
import com.petshop.vo.UserAdminVO;

import java.util.Map;

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

    /** 管理端分页查询用户 */
    Page<UserAdminVO> pageAdminUsers(Integer page, Integer size, String username,
                                     String nickname, String phone, Integer status,
                                     String role, Integer memberLevel);

    /** 管理端查询用户详情 */
    UserAdminVO getAdminUser(Long id);

    /** 管理端编辑用户基本信息 */
    void updateAdminUser(Long id, UserAdminDto dto);

    /** 管理端启用或禁用普通用户 */
    void updateAdminUserStatus(Long id, Integer status);

    /** 管理端删除无历史订单的普通用户 */
    void deleteAdminUser(Long id);

    /** 管理端新建管理员账号 */
    void adminCreateUser(User user);

    /** 查询 token 信息（Redis），token 无效返回 null */
    Map<String, Object> getTokenInfo(String token);
}
