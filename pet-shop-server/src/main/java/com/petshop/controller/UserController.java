package com.petshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.common.Result;
import com.petshop.dto.LoginDto;
import com.petshop.dto.RegisterDto;
import com.petshop.dto.UserAdminDto;
import com.petshop.dto.WxLoginDto;
import com.petshop.entity.User;
import com.petshop.service.UserService;
import com.petshop.vo.UserAdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterDto dto) {
        User user = userService.register(dto.getUsername(), dto.getPassword(), dto.getPhone(),
                dto.getCaptchaKey(), dto.getCaptchaX());
        return Result.ok(Map.of("userId", user.getId()));
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDto dto) {
        String token = userService.login(dto.getUsername(), dto.getPassword(),
                dto.getCaptchaKey(), dto.getCaptchaX());
        User user = userService.findByUsername(dto.getUsername());
        return Result.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "nickname", user.getNickname(),
                "role", user.getRole()
        ));
    }

    /** 微信小程序登录 */
    @PostMapping("/wx-login")
    public Result<?> wxLogin(@RequestBody WxLoginDto dto) {
        Map<String, Object> result = userService.loginByWechat(dto.getCode());
        return Result.ok(result);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return Result.ok(user);
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody User user) {
        userService.updateProfile(user);
        return Result.ok();
    }

    @PutMapping("/password")
    public Result<?> updatePassword(@RequestParam Long userId,
                                     @RequestParam String oldPassword,
                                     @RequestParam String newPassword) {
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.ok();
    }

    /** 管理端分页查询用户 */
    @GetMapping("/admin/list")
    public Result<Page<UserAdminVO>> adminList(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestParam(required = false) String username,
                                                @RequestParam(required = false) String nickname,
                                                @RequestParam(required = false) String phone,
                                                @RequestParam(required = false) Integer status,
                                                @RequestParam(required = false) String role,
                                                @RequestParam(required = false) Integer memberLevel) {
        return Result.ok(userService.pageAdminUsers(page, size, username, nickname,
                phone, status, role, memberLevel));
    }

    /** 管理端查询用户详情 */
    @GetMapping("/admin/{id}")
    public Result<UserAdminVO> adminDetail(@PathVariable Long id) {
        return Result.ok(userService.getAdminUser(id));
    }

    /** 管理端编辑用户基本信息 */
    @PutMapping("/admin/{id}")
    public Result<?> adminUpdate(@PathVariable Long id,
                                  @Valid @RequestBody UserAdminDto dto) {
        userService.updateAdminUser(id, dto);
        return Result.ok("修改用户信息成功", null);
    }

    /** 管理端启用或禁用普通用户 */
    @PutMapping("/admin/{id}/status")
    public Result<?> adminUpdateStatus(@PathVariable Long id,
                                        @RequestParam Integer status) {
        userService.updateAdminUserStatus(id, status);
        return Result.ok("用户状态修改成功", null);
    }

    /** 管理端新建管理员账号 */
    @PostMapping("/admin/create")
    public Result<?> adminCreate(@RequestBody User user) {
        userService.adminCreateUser(user);
        return Result.ok("创建成功", null);
    }

    /** 管理端删除无历史订单的普通用户 */
    @DeleteMapping("/admin/{id}")
    public Result<?> adminDelete(@PathVariable Long id) {
        userService.deleteAdminUser(id);
        return Result.ok("删除用户成功", null);
    }
}
