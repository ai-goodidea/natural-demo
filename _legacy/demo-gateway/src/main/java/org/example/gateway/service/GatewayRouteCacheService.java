package org.example.gateway.service;

import com.goodidea.gateway.entity.GatewayRouteItem;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;
import java.util.Map;

public interface GatewayRouteCacheService {
    
    void initializeLocalCache();
    
    void addRouteToCache(GatewayRouteItem routeItem);
    
    void removeRouteFromCache(String routeId);
    
    Map<String, GatewayRouteItem> getLocalRoutes();
    
    List<RouteDefinition> getCurrentRouteDefinitions();
    
    void refreshDynamicRoutes();
}
