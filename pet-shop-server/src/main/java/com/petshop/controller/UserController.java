package com.petshop.controller;

import com.petshop.common.Result;
import com.petshop.dto.LoginDto;
import com.petshop.dto.RegisterDto;
import com.petshop.entity.User;
import com.petshop.service.UserService;
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
        User user = userService.register(dto.getUsername(), dto.getPassword(), dto.getPhone());
        return Result.ok(Map.of("userId", user.getId()));
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDto dto) {
        String token = userService.login(dto.getUsername(), dto.getPassword());
        User user = userService.findByUsername(dto.getUsername());
        return Result.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "nickname", user.getNickname(),
                "role", user.getRole()
        ));
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
}
