package org.example.common.auth;

/**
 * 业务服务用，存当前请求线程的用户上下文。
 * 由 {@link UserContextFilter} 从网关塞的 X-User-* 请求头填充。
 */
public final class UserContextHolder {

    private static final ThreadLocal<CurrentUser> HOLDER = new ThreadLocal<>();

    public static void set(CurrentUser u) {
        HOLDER.set(u);
    }

    public static CurrentUser get() {
        CurrentUser u = HOLDER.get();
        return u == null ? CurrentUser.anonymous() : u;
    }

    public static void clear() {
        HOLDER.remove();
    }

    private UserContextHolder() {}
}
