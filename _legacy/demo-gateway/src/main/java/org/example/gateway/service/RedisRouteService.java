package org.example.gateway.service;

import com.goodidea.gateway.entity.GatewayRouteItem;
import java.util.List;

public interface RedisRouteService {
    
    List<GatewayRouteItem> getRoutesToAdd();
    
    List<String> getRoutesToRemove();
    
    void clearProcessedRoutes();
}