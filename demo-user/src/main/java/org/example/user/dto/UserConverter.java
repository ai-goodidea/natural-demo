package org.example.user.dto;

import cn.hutool.core.bean.BeanUtil;
import org.example.user.entity.User;

public final class UserConverter {

    private UserConverter() {}

    public static UserVO toVO(User entity) {
        if (entity == null) return null;
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }

    public static User toEntity(UserCreateRequest req) {
        User u = new User();
        BeanUtil.copyProperties(req, u);
        return u;
    }

    public static void applyUpdate(UserUpdateRequest req, User entity) {
        BeanUtil.copyProperties(req, entity, "id", "username", "password", "createTime", "updateTime");
    }
}
