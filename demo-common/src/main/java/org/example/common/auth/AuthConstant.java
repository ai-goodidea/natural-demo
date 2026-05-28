package org.example.common.auth;

public final class AuthConstant {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 网关解析 JWT 后塞给下游业务服务的请求头 */
    public static final String USER_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-User-Name";
    public static final String ROLES_HEADER = "X-User-Roles";

    /** redis 中保存 token -> 用户信息 JSON，TTL 24 小时 */
    public static final String TOKEN_REDIS_PREFIX = "auth:token:";
    public static final long TOKEN_TTL_SECONDS = 24 * 60 * 60L;

    private AuthConstant() {}
}
