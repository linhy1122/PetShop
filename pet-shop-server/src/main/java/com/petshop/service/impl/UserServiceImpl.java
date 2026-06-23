package com.petshop.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.entity.User;
import com.petshop.mapper.UserMapper;
import com.petshop.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;
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

    @Override
    public User findByGithubId(Long githubId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getGithubId, githubId);
        return getOne(wrapper);
    }

    @Override
    public Map<String, Object> loginByGithub(Long githubId, String githubUsername, String avatarUrl, String email) {
        User user = findByGithubId(githubId);
        if (user == null) {
            // 自动注册新用户：递进式用户名策略，避免与普通用户冲突
            String username = githubUsername;
            if (StrUtil.isBlank(username)) {
                username = "gh_" + githubId;
            } else if (findByUsername(username) != null) {
                // 原始用户名已被占用，尝试加 _gh 后缀
                String alt = username + "_gh";
                if (findByUsername(alt) != null) {
                    // 仍被占用，兜底使用 GitHub ID（全局唯一）
                    username = "gh_" + githubId;
                } else {
                    username = alt;
                }
            }

            user = new User();
            user.setUsername(username);
            user.setPassword(BCrypt.hashpw(UUID.randomUUID().toString()));
            user.setNickname(StrUtil.isNotBlank(githubUsername) ? githubUsername : ("GitHub用户" + githubId));
            user.setAvatar(StrUtil.isNotBlank(avatarUrl) ? avatarUrl : "");
            user.setEmail(StrUtil.isNotBlank(email) ? email : "");
            user.setGithubId(githubId);
            user.setPhone("");
            user.setMemberLevel(0);
            user.setRole("user");
            user.setStatus(0);
            save(user);
        }

        if (user.getStatus() == 1) {
            throw new RuntimeException("账户已被禁用");
        }

        String token = "token-" + UUID.randomUUID();
        return Map.of(
                "token", token,
                "userId", user.getId(),
                "nickname", user.getNickname(),
                "role", user.getRole()
        );
    }
}
