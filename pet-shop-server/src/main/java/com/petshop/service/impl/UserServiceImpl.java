package com.petshop.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.User;
import com.petshop.mapper.UserMapper;
import com.petshop.service.UserService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User register(String username, String password, String phone) {
        // 检查用户名是否已存在
        User exist = findByUsername(username);
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password));
        user.setPhone(phone);
        user.setNickname("用户" + UUID.randomUUID().toString().substring(0, 8));
        user.setMemberLevel(0);
        user.setRole("user");
        user.setStatus(0);

        save(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (user.getStatus() == 1) {
            throw new RuntimeException("账户已被禁用");
        }

        // 验证密码（使用hutool的bcrypt）
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // TODO: 生成JWT Token
        return "token-" + UUID.randomUUID();
    }

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        updateById(user);
    }

    @Override
    public void updateProfile(User user) {
        User dbUser = getById(user.getId());
        if (dbUser == null) {
            throw new RuntimeException("用户不存在");
        }
        // 只更新允许修改的字段
        if (StrUtil.isNotBlank(user.getNickname())) {
            dbUser.setNickname(user.getNickname());
        }
        if (StrUtil.isNotBlank(user.getPhone())) {
            dbUser.setPhone(user.getPhone());
        }
        if (StrUtil.isNotBlank(user.getEmail())) {
            dbUser.setEmail(user.getEmail());
        }
        if (StrUtil.isNotBlank(user.getAvatar())) {
            dbUser.setAvatar(user.getAvatar());
        }
        updateById(dbUser);
    }
}
