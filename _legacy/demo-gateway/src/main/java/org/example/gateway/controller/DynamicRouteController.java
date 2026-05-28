package org.example.gateway.controller;

import com.goodidea.gateway.api.CommonResult;
import com.goodidea.gateway.entity.GatewayRouteItem;
import com.goodidea.gateway.service.GatewayRouteCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/route")
public class DynamicRouteController {
    
    @Autowired
    private GatewayRouteCacheService gatewayRouteCacheService;
    
    @PostMapping("/refresh")
    public CommonResult<String> refreshRoutes() {
        try {
            gatewayRouteCacheService.refreshDynamicRoutes();
            return CommonResult.success("Routes refreshed successfully");
        } catch (Exception e) {
            log.error("Failed to refresh routes", e);
            return CommonResult.failed("Failed to refresh routes: " + e.getMessage());
        }
    }
    
    @GetMapping("/cache")
    public CommonResult<Map<String, GatewayRouteItem>> getLocalRouteCache() {
        try {
            Map<String, GatewayRouteItem> routes = gatewayRouteCacheService.getLocalRoutes();
            return CommonResult.success(routes);
        } catch (Exception e) {
            log.error("Failed to get local route cache", e);
            return CommonResult.failed("Failed to get local route cache: " + e.getMessage());
        }
    }
    
    @PostMapping("/initialize")
    public CommonResult<String> initializeCache() {
        try {
            gatewayRouteCacheService.initializeLocalCache();
            return CommonResult.success("Cache initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize cache", e);
            return CommonResult.failed("Failed to initialize cache: " + e.getMessage());
        }
    }
}