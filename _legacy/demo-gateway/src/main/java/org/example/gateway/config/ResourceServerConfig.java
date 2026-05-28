package org.example.gateway.config;


import com.goodidea.gateway.authorization.AuthorizationManager;
import com.goodidea.gateway.component.RestAuthenticationEntryPoint;
import com.goodidea.gateway.component.RestfulAccessDeniedHandler;
import com.goodidea.gateway.constant.AuthConstant;
import com.goodidea.gateway.filter.IgnoreUrlsRemoveJwtFilter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Mono;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 资源服务器配置
 * Created by macro on 2020/6/19.
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    @Autowired
    private final AuthorizationManager authorizationManager;
    @Autowired
    private final IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private final IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        String[] urls = ignoreUrlsConfig.getUrls().toArray(new String[]{});
        
        return http
                .addFilterBefore(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(urls).permitAll()
                        .anyExchange().permitAll()  // 暂时允许所有请求通过，认证由AuthWebFilter处理
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .headers(headers -> headers
                        .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)
                )
                .build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        try {
            // 从classpath读取公钥
            ClassPathResource resource = new ClassPathResource("public.txt");
            byte[] keyBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String publicKeyContent = new String(keyBytes).replaceAll("\\n", "").replaceAll("\\r", "");
            
            // 解码Base64公钥
            byte[] decoded = Base64.getDecoder().decode(publicKeyContent);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            
            // 创建JWT解码器
            return NimbusReactiveJwtDecoder.withPublicKey(publicKey).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create JWT decoder", e);
        }
    }

}
