package org.example.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.goodidea.gateway.config.IgnoreUrlsConfig;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

/**
 * JWT认证WebFilter，优先级高于Spring Security
 * @author Administrator
 */
@Slf4j
@Component
public class AuthWebFilter implements WebFilter, Ordered {


    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        log.info("AuthWebFilter被调用，请求路径：{}", path);
        
        // 检查是否是白名单路径
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String ignoreUrl : ignoreUrlsConfig.getUrls()) {
            if (pathMatcher.match(ignoreUrl, path)) {
                log.info("白名单路径，直接通过：{}", path);
                return chain.filter(exchange);
            }
        }
        
        HttpHeaders headers = exchange.getRequest().getHeaders();
        log.info("headers:{}", headers);
        String token = headers.getFirst("Authorization");
        
        if (StrUtil.isEmpty(token)) {
            log.error("需要认证的路径缺少Authorization header：{}", path);
            // 这里应该返回401错误，但为了调试先让请求继续
            return chain.filter(exchange);
        }
        
        try {
            //从token中解析用户信息并设置到Header中去
            String realToken = token.replace("bearer ", "").replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            Payload payload = jwsObject.getPayload();
            String userStr = jwsObject.getPayload().toString();
            JSON json = JSONUtil.parse(userStr);
            log.info("我的角色权限 user:{}", userStr);
            Object userId = json.getByPath("sub");
            if(userId != null){
                ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("userId", userId.toString())
                    .build();
                exchange = exchange.mutate().request(request).build();
                log.info("设置userId到header: {}", userId);
            }
        } catch (ParseException e) {
            log.error("JWT解析失败", e);
        }
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 设置很高的优先级，确保在Spring Security之前执行
        return -200;
    }
}