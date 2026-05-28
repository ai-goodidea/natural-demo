package org.example.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发/解析。secret 通过配置 auth.jwt.secret 注入。
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long ttlMillis;

    public JwtTokenProvider(
            @Value("${auth.jwt.secret:natural-demo-jwt-secret-key-please-replace-me-32+chars}") String secret,
            @Value("${auth.jwt.ttl-seconds:86400}") long ttlSeconds) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            for (int i = bytes.length; i < 32; i++) padded[i] = '0';
            bytes = padded;
        }
        this.secretKey = Keys.hmacShaKeyFor(bytes);
        this.ttlMillis = ttlSeconds * 1000L;
    }

    public String issue(JwtPayload payload) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(payload.getUserId()))
                .claim("username", payload.getUsername())
                .claim("nickname", payload.getNickname())
                .claim("avatar", payload.getAvatar())
                .claim("roles", payload.getRoles())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMillis))
                .signWith(secretKey)
                .compact();
    }

    public JwtPayload parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return JwtPayload.builder()
                .userId(Long.valueOf(claims.getSubject()))
                .username(claims.get("username", String.class))
                .nickname(claims.get("nickname", String.class))
                .avatar(claims.get("avatar", String.class))
                .roles(claims.get("roles", String.class))
                .build();
    }

    /** 仅做签名校验，不抛异常 */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            log.debug("invalid jwt: {}", e.getMessage());
            return false;
        }
    }
}
