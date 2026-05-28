package org.example.gateway.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 网关白名单配置
 *
 * @author macro
 * @date 2020/6/17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@ConfigurationProperties(prefix="idea.security.ignore")
public class IgnoreUrlsConfig {
    private List<String> urls;
}
