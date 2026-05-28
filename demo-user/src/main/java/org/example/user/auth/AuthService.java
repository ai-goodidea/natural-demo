package org.example.user.auth;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.auth.AuthConstant;
import org.example.common.auth.JwtPayload;
import org.example.common.auth.JwtTokenProvider;
import org.example.user.auth.dto.LoginRequest;
import org.example.user.auth.dto.LoginResult;
import org.example.user.auth.dto.UserInfoVO;
import org.example.user.entity.User;
import org.example.user.mapper.UserMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public LoginResult login(LoginRequest req) {
        String username = req.getUsername().trim();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            // 简单 demo：账号不存在则自动创建一个 inspector
            user = new User();
            user.setUsername(username);
            user.setNickname(username);
            user.setRoles("inspector");
            user.setStatus(1);
            userMapper.insert(user);
        }

        UserInfoVO info = toUserInfo(user);

        String token = jwtTokenProvider.issue(JwtPayload.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(StrUtil.nullToEmpty(user.getRoles()))
                .build());
        info.setToken(token);

        // 把 userInfo 缓存到 redis：auth:token:{token} -> JSON
        try {
            redis.opsForValue().set(
                    AuthConstant.TOKEN_REDIS_PREFIX + token,
                    objectMapper.writeValueAsString(info),
                    Duration.ofSeconds(AuthConstant.TOKEN_TTL_SECONDS));
        } catch (JsonProcessingException e) {
            log.warn("redis cache token failed", e);
        }

        return LoginResult.builder().accessToken(token).userInfo(info).build();
    }

    public UserInfoVO getUserInfo(String token) {
        if (StrUtil.isBlank(token)) return null;
        String json = redis.opsForValue().get(AuthConstant.TOKEN_REDIS_PREFIX + token);
        if (StrUtil.isBlank(json)) return null;
        try {
            return objectMapper.readValue(json, UserInfoVO.class);
        } catch (JsonProcessingException e) {
            log.warn("read userInfo from redis failed", e);
            return null;
        }
    }

    public UserInfoVO getUserInfoByUserId(Long userId) {
        User user = userMapper.selectById(userId);
        return user == null ? null : toUserInfo(user);
    }

    public void logout(String token) {
        if (StrUtil.isNotBlank(token)) {
            redis.delete(AuthConstant.TOKEN_REDIS_PREFIX + token);
        }
    }

    public List<String> codes(String roles) {
        if (StrUtil.isBlank(roles)) return List.of();
        // demo：admin → 全权限；其他 → 只读
        if (roles.contains("admin")) {
            return Arrays.asList("AC_100100", "AC_100110", "AC_100120", "AC_100010");
        }
        return Arrays.asList("AC_100010");
    }

    private UserInfoVO toUserInfo(User user) {
        List<String> roles = StrUtil.isBlank(user.getRoles())
                ? List.of()
                : Arrays.stream(user.getRoles().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        return UserInfoVO.builder()
                .userId(String.valueOf(user.getId()))
                .username(user.getUsername())
                .realName(StrUtil.nullToDefault(user.getNickname(), user.getUsername()))
                .avatar(StrUtil.nullToDefault(user.getAvatar(), ""))
                .desc(roles.contains("admin") ? "系统管理员" : "验收人员")
                .homePath("/dashboard")
                .roles(roles.isEmpty() ? List.of("inspector") : roles)
                .build();
    }
}
