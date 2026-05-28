package org.example.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBufAllocator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.auth.AuthConstant;
import org.example.common.auth.JwtPayload;
import org.example.common.auth.JwtTokenProvider;
import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.gateway.config.AuthWhitelistProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final ReactiveStringRedisTemplate redis;
    private final AuthWhitelistProperties whitelist;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String path = req.getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String header = req.getHeaders().getFirst(AuthConstant.TOKEN_HEADER);
        if (header == null || !header.startsWith(AuthConstant.TOKEN_PREFIX)) {
            return unauthorized(exchange, "缺少 Authorization Bearer token");
        }
        String token = header.substring(AuthConstant.TOKEN_PREFIX.length());
        if (!jwtTokenProvider.isValid(token)) {
            return unauthorized(exchange, "token 无效或已过期");
        }

        String redisKey = AuthConstant.TOKEN_REDIS_PREFIX + token;
        return redis.hasKey(redisKey).flatMap(exists -> {
            if (Boolean.FALSE.equals(exists)) {
                return unauthorized(exchange, "会话已过期，请重新登录");
            }
            JwtPayload payload = jwtTokenProvider.parse(token);
            ServerHttpRequest mutated = req.mutate()
                    .header(AuthConstant.USER_HEADER, String.valueOf(payload.getUserId()))
                    .header(AuthConstant.USERNAME_HEADER, nullToEmpty(payload.getUsername()))
                    .header(AuthConstant.ROLES_HEADER, nullToEmpty(payload.getRoles()))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        });
    }

    private boolean isWhitelisted(String path) {
        return whitelist.getWhitelist().stream().anyMatch(p -> matcher.match(p, path));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        ServerHttpResponse resp = exchange.getResponse();
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        resp.getHeaders().set(HttpHeaders.CACHE_CONTROL, "no-store");
        Result<Void> body = Result.failed(ResultCode.UNAUTHORIZED.getCode(), msg);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":401,\"message\":\"" + msg + "\"}").getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT).wrap(bytes);
        return resp.writeWith(Mono.just(buffer));
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
