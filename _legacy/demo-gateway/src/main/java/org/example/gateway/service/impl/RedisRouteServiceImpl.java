package org.example.gateway.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.goodidea.core.util.JsonUtils;
import com.goodidea.gateway.entity.GatewayRouteItem;
import com.goodidea.gateway.service.RedisRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class RedisRouteServiceImpl implements RedisRouteService {
    
    private static final String ROUTE_ADD_KEY = "gateway_dynamic_route";
    private static final String ROUTE_REMOVE_KEY = "gateway_dynamic_route_remove";
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Override
    public List<GatewayRouteItem> getRoutesToAdd() {
        try {
            String routesJson = stringRedisTemplate.opsForValue().get(ROUTE_ADD_KEY);
            System.out.println(routesJson);
            if (StringUtils.hasText(routesJson)) {
                return JsonUtils.parseArray(routesJson,GatewayRouteItem.class);
            }
        } catch (Exception e) {
            log.error("Failed to parse routes from Redis key: {}", ROUTE_ADD_KEY, e);
        }
        return Collections.emptyList();
    }
    
    @Override
    public List<String> getRoutesToRemove() {
        try {
            String routeIdsJson = stringRedisTemplate.opsForValue().get(ROUTE_REMOVE_KEY);
            if (StringUtils.hasText(routeIdsJson)) {
                return JSON.parseObject(routeIdsJson, new TypeReference<List<String>>() {});
            }
        } catch (Exception e) {
            log.error("Failed to parse route IDs to remove from Redis key: {}", ROUTE_REMOVE_KEY, e);
        }
        return Collections.emptyList();
    }
    
    @Override
    public void clearProcessedRoutes() {
        try {
            //stringRedisTemplate.delete(ROUTE_ADD_KEY);
            stringRedisTemplate.delete(ROUTE_REMOVE_KEY);
            log.info("Cleared processed routes from Redis");
        } catch (Exception e) {
            log.error("Failed to clear processed routes from Redis", e);
        }
    }
}