package com.petshop.vo;

import com.petshop.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户信息返回对象，不包含密码和删除标记。
 */
@Data
public class UserAdminVO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer memberLevel;
    private String role;
    private Integer status;
    private Long githubId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public static UserAdminVO from(User user) {
        UserAdminVO vo = new UserAdminVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setMemberLevel(user.getMemberLevel());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setGithubId(user.getGithubId());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
