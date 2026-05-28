package org.example.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Nacos配置变更监听器，用于动态刷新Gateway路由
 */
@Component
@RefreshScope
public class NacosRouteConfigListener {
    
    private static final Logger logger = LoggerFactory.getLogger(NacosRouteConfigListener.class);
    
    @Autowired
    private ApplicationEventPublisher publisher;
    
    @NacosConfigListener(dataId = "${spring.application.name}-${spring.profiles.active}.yaml", groupId = "DEFAULT_GROUP")
    public void onConfigChange(String configInfo) {
        logger.info("Nacos配置发生变化，刷新Gateway路由: {}", configInfo);
        // 发布路由刷新事件
        publisher.publishEvent(new RefreshRoutesEvent(this));
    }
}