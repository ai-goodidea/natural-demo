package org.example.gateway.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;

/**
 * @author Administrator
 */
@Slf4j
//@Component  // 暂时注释掉，使用AuthWebFilter替代
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {


    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("AuthGlobalFilter被调用，请求路径：{}", exchange.getRequest().getPath());
        HttpHeaders headers = exchange.getRequest().getHeaders();
        log.info("headers:{}",headers);
        String token = headers.getFirst("Authorization");
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }
        try {
            //从token中解析用户信息并设置到Header中去
            String realToken = token.replace("bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            Payload payload = jwsObject.getPayload();
            String userStr = jwsObject.getPayload().toString();
            JSON json = JSONUtil.parse(userStr);
            log.info("我的角色权限 user:{}", userStr);
            Object userId = json.getByPath("userId");
            if(userId!=null){
                ServerHttpRequest request = exchange.getRequest().mutate().header("userId", userId.toString()).build();
                exchange = exchange.mutate().request(request).build();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return -100;
    }
}
