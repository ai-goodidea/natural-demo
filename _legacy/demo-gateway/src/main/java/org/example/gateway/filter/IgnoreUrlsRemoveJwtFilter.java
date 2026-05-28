package org.example.gateway.filter;


import cn.hutool.core.util.StrUtil;
import com.goodidea.gateway.config.IgnoreUrlsConfig;
import com.goodidea.gateway.helper.AppGatewayHelper;
import com.goodidea.gateway.helper.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

/**
 * 白名单路径访问时需要移除JWT请求头
 *
 * @author macro
 * @date 2020/7/24
 */
@Slf4j
@Component
public class IgnoreUrlsRemoveJwtFilter implements WebFilter {
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private AppGatewayHelper appGatewayHelper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        URI uri = request.getURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        //白名单路径移除JWT请求头
        appGatewayHelper.bindNetInfo(exchange);
        List<String> ignoreUrls = ignoreUrlsConfig.getUrls();
        log.info("uri:{}",uri);
        String token = headers.getFirst("Authorization");
        if(!StrUtil.isBlank(token)){
            token = token.substring("Bearer ".length());
        }
        boolean tokenExpired = jwtHelper.isTokenExpired(token);
        for (String ignoreUrl : ignoreUrls) {
            if (pathMatcher.match(ignoreUrl, uri.getPath())) {
                log.info("忽略的URI请求：{}",uri.getPath());
                log.info("token= {}",token);
                if(StrUtil.isNotEmpty(token)){
                    if(tokenExpired){
                        exchange.getRequest().mutate().header("Authorization", "").build();
                        exchange = exchange.mutate().request(request).build();
                    }
                }
                return chain.filter(exchange);
            }
        }
        return chain.filter(exchange);
    }
}
