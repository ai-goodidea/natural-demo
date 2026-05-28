package org.example.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * iframe 嵌入支持过滤器
 * 添加必要的头部信息以支持iframe嵌入
 */
@Slf4j
@Component
public class IframeHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        
        // 设置X-Frame-Options允许同源iframe嵌入
        headers.set("X-Frame-Options", "SAMEORIGIN");
        
        // 设置Content-Security-Policy允许iframe嵌入
        headers.set("Content-Security-Policy", "frame-ancestors 'self' *");
        
        // 设置X-Content-Type-Options
        headers.set("X-Content-Type-Options", "nosniff");
        
        log.debug("Added iframe support headers for request: {}", exchange.getRequest().getPath());
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200; // 确保在其他过滤器之前执行
    }
}