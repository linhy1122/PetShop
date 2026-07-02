package com.petshop.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petshop.config.WechatProperties;
import com.petshop.dto.UserAdminDto;
import com.petshop.entity.Order;
import com.petshop.entity.User;
import com.petshop.mapper.OrderMapper;
import com.petshop.mapper.UserMapper;
import com.petshop.service.UserService;
import com.petshop.util.CaptchaUtil;
import com.petshop.vo.UserAdminVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final OrderMapper orderMapper;
    private final WechatProperties wechatProperties;


    public UserServiceImpl(OrderMapper orderMapper, WechatProperties wechatProperties) {
        this.orderMapper = orderMapper;
        this.wechatProperties = wechatProperties;
    }

    @Override
    public User register(String username, String password, String phone, String captchaKey, Integer captchaX) {
        // 验证滑块验证码
        verifyCaptcha(captchaKey, captchaX);

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
    public String login(String username, String password, String captchaKey, Integer captchaX) {
        // 验证滑块验证码
        verifyCaptcha(captchaKey, captchaX);

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

    @Override
    public Map<String, Object> loginByWechat(String code) {
        // 1. 用 code 向微信服务器换取 openid
        String url = "https://api.weixin.qq.com/sns/jscode2session"
                + "?appid=" + wechatProperties.getAppId()
                + "&secret=" + wechatProperties.getAppSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        String response = HttpRequest.get(url).execute().body();
        JSONObject result = JSONUtil.parseObj(response);

        String openid = result.getStr("openid");
        if (openid == null) {
            throw new RuntimeException("微信登录失败: " + result.getStr("errmsg", "未知错误"));
        }

        // 2. 根据 openid 查找用户
        User user = findByOpenid(openid);
        if (user == null) {
            // 3. 自动注册
            user = new User();
            user.setUsername("wx_" + openid.substring(0, 12));
            user.setPassword(BCrypt.hashpw(UUID.randomUUID().toString()));
            user.setNickname("微信用户" + openid.substring(openid.length() - 6));
            user.setOpenid(openid);
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

    private User findByOpenid(String openid) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        return getOne(wrapper);
    }

    @Override
    public Page<UserAdminVO> pageAdminUsers(Integer page, Integer size, String username,
                                             String nickname, String phone, Integer status,
                                             String role, Integer memberLevel) {
        validateStatusWhenPresent(status);
        validateRoleWhenPresent(role);
        validateMemberLevelWhenPresent(memberLevel);

        long current = page == null ? 1 : Math.max(page, 1);
        long pageSize = size == null ? 10 : Math.min(Math.max(size, 1), 100);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0);
        if (StrUtil.isNotBlank(username)) {
            wrapper.like(User::getUsername, username.trim());
        }
        if (StrUtil.isNotBlank(nickname)) {
            wrapper.like(User::getNickname, nickname.trim());
        }
        if (StrUtil.isNotBlank(phone)) {
            wrapper.like(User::getPhone, phone.trim());
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        if (StrUtil.isNotBlank(role)) {
            wrapper.eq(User::getRole, role.trim());
        }
        if (memberLevel != null) {
            wrapper.eq(User::getMemberLevel, memberLevel);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> userPage = page(new Page<>(current, pageSize), wrapper);
        List<UserAdminVO> records = userPage.getRecords().stream()
                .map(UserAdminVO::from)
                .collect(Collectors.toList());
        Page<UserAdminVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public UserAdminVO getAdminUser(Long id) {
        return UserAdminVO.from(getExistingUser(id));
    }

    @Override
    @Transactional
    public void updateAdminUser(Long id, UserAdminDto dto) {
        validateStatus(dto.getStatus());
        validateMemberLevel(dto.getMemberLevel());
        User user = getExistingUser(id);
        if ("admin".equals(user.getRole()) && dto.getStatus() == 1) {
            throw new RuntimeException("管理员账号不能被禁用");
        }
        user.setNickname(trimToEmpty(dto.getNickname()));
        user.setPhone(trimToEmpty(dto.getPhone()));
        user.setEmail(trimToEmpty(dto.getEmail()));
        user.setAvatar(trimToEmpty(dto.getAvatar()));
        user.setMemberLevel(dto.getMemberLevel());
        user.setStatus(dto.getStatus());
        user.setUpdateTime(LocalDateTime.now());
        if (!updateById(user)) {
            throw new RuntimeException("修改用户信息失败");
        }
    }

    @Override
    @Transactional
    public void updateAdminUserStatus(Long id, Integer status) {
        validateStatus(status);
        User user = getExistingUser(id);
        if ("admin".equals(user.getRole()) && status == 1) {
            throw new RuntimeException("管理员账号不能被禁用");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        if (!updateById(user)) {
            throw new RuntimeException("用户状态修改失败");
        }
    }

    @Override
    @Transactional
    public void adminCreateUser(User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new RuntimeException("用户名和密码不能为空");
        }
        User exist = findByUsername(user.getUsername());
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }

        user.setRole("admin");
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setMemberLevel(user.getMemberLevel() != null ? user.getMemberLevel() : 0);
        user.setStatus(0);
        if (StrUtil.isBlank(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        if (StrUtil.isBlank(user.getPhone())) {
            user.setPhone("");
        }
        if (StrUtil.isBlank(user.getEmail())) {
            user.setEmail("");
        }

        save(user);
    }

    @Override
    @Transactional
    public void deleteAdminUser(Long id) {
        User user = getExistingUser(id);
        if ("admin".equals(user.getRole())) {
            throw new RuntimeException("管理员账号不能删除");
        }
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Order::getUserId, id)
                .eq(Order::getDeleted, 0);
        if (orderMapper.selectCount(orderWrapper) > 0) {
            throw new RuntimeException("该用户存在订单，不能删除");
        }
        // 项目当前未启用 @TableLogic，沿用现有 removeById 删除方式。
        if (!removeById(id)) {
            throw new RuntimeException("删除用户失败");
        }
    }

    private User getExistingUser(Long id) {
        if (id == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        User user = getById(id);
        if (user == null || Integer.valueOf(1).equals(user.getDeleted())) {
            throw new RuntimeException("用户不存在");
        }
        return user;
    }

    private void validateStatusWhenPresent(Integer status) {
        if (status != null) {
            validateStatus(status);
        }
    }

    private void validateStatus(Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new RuntimeException("用户状态只能是0或1");
        }
    }

    private void validateMemberLevelWhenPresent(Integer memberLevel) {
        if (memberLevel != null) {
            validateMemberLevel(memberLevel);
        }
    }

    private void validateMemberLevel(Integer memberLevel) {
        if (memberLevel == null || memberLevel < 0 || memberLevel > 3) {
            throw new RuntimeException("会员等级只能是0到3");
        }
    }

    private void validateRoleWhenPresent(String role) {
        if (StrUtil.isNotBlank(role)
                && !"user".equals(role.trim())
                && !"admin".equals(role.trim())) {
            throw new RuntimeException("用户角色只能是user或admin");
        }
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    /** 校验滑块验证码：解析签名token → 比对X位置 */
    private void verifyCaptcha(String captchaKey, Integer captchaX) {
        if (captchaX == null) {
            throw new RuntimeException("请完成滑块验证");
        }
        if (!CaptchaUtil.verify(captchaKey, captchaX)) {
            throw new RuntimeException("验证码验证失败，请重试");
        }
    }
}
