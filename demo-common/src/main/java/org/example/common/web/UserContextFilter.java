package org.example.common.web;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.common.auth.AuthConstant;
import org.example.common.auth.CurrentUser;
import org.example.common.auth.UserContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 业务服务通用过滤器：从网关塞过来的 X-User-* 头里读用户上下文，写入 ThreadLocal。
 * 只在 servlet 应用里生效；reactive 网关本身不会装配本 bean。
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        try {
            String userIdStr = req.getHeader(AuthConstant.USER_HEADER);
            if (StrUtil.isNotBlank(userIdStr)) {
                CurrentUser u = CurrentUser.builder()
                        .userId(Long.valueOf(userIdStr))
                        .username(req.getHeader(AuthConstant.USERNAME_HEADER))
                        .roles(req.getHeader(AuthConstant.ROLES_HEADER))
                        .build();
                UserContextHolder.set(u);
            } else {
                UserContextHolder.set(CurrentUser.anonymous());
            }
            chain.doFilter(req, resp);
        } finally {
            UserContextHolder.clear();
        }
    }
}
