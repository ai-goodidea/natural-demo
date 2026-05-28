package org.example.gateway.service.impl;

import com.goodidea.gateway.config.DynamicRouteServiceImpl;
import com.goodidea.gateway.entity.GatewayRouteItem;
import com.goodidea.gateway.service.GatewayRouteCacheService;
import com.goodidea.gateway.service.RedisRouteService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GatewayRouteCacheServiceImpl implements GatewayRouteCacheService {

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;
    
    @Autowired
    private DynamicRouteServiceImpl dynamicRouteService;
    
    @Autowired
    private RedisRouteService redisRouteService;
    
    private final Map<String, GatewayRouteItem> localRouteCache = new ConcurrentHashMap<>();
    
    @PostConstruct
    @Override
    public void initializeLocalCache() {
        try {
            List<RouteDefinition> currentRoutes = getCurrentRouteDefinitions();
            for (RouteDefinition route : currentRoutes) {
                GatewayRouteItem item = convertToGatewayRouteItem(route);
                localRouteCache.put(route.getId(), item);
            }
            log.info("Initialized local route cache with {} routes", localRouteCache.size());
        } catch (Exception e) {
            log.error("Failed to initialize local route cache", e);
        }
    }
    
    @Override
    public void addRouteToCache(GatewayRouteItem routeItem) {
        if (routeItem != null && routeItem.getId() != null) {
            localRouteCache.put(routeItem.getId(), routeItem);
            log.debug("Added route to cache: {}", routeItem.getId());
        }
    }
    
    @Override
    public void removeRouteFromCache(String routeId) {
        if (routeId != null) {
            localRouteCache.remove(routeId);
            log.debug("Removed route from cache: {}", routeId);
        }
    }
    
    @Override
    public Map<String, GatewayRouteItem> getLocalRoutes() {
        return new ConcurrentHashMap<>(localRouteCache);
    }
    
    @Override
    public List<RouteDefinition> getCurrentRouteDefinitions() {
        try {
            Flux<RouteDefinition> routeDefinitionFlux = routeDefinitionLocator.getRouteDefinitions();
            return routeDefinitionFlux.collectList().block();
        } catch (Exception e) {
            log.error("Failed to get current route definitions", e);
            return List.of();
        }
    }
    
    @Scheduled(fixedDelay = 10000)
    @Override
    public void refreshDynamicRoutes() {
        try {
            List<GatewayRouteItem> routesToAdd = redisRouteService.getRoutesToAdd();
            List<String> routesToRemove = redisRouteService.getRoutesToRemove();
            
            for (String routeId : routesToRemove) {
                removeRouteFromGateway(routeId);
                removeRouteFromCache(routeId);
                log.info("Removed route: {}", routeId);
            }
            
            for (GatewayRouteItem routeItem : routesToAdd) {
                RouteDefinition routeDefinition = convertToRouteDefinition(routeItem);
                dynamicRouteService.add(routeDefinition);
                addRouteToCache(routeItem);
                log.info("Added route: {}", routeItem.getId());
            }
            
            if (!routesToAdd.isEmpty() || !routesToRemove.isEmpty()) {
                redisRouteService.clearProcessedRoutes();
                log.info("Processed {} routes to add and {} routes to remove", 
                        routesToAdd.size(), routesToRemove.size());
            }
            
        } catch (Exception e) {
            log.error("Failed to refresh dynamic routes", e);
        }
    }
    
    private void removeRouteFromGateway(String routeId) {
        try {
            dynamicRouteService.delete(routeId).block();
        } catch (Exception e) {
            log.error("Failed to remove route from gateway: {}", routeId, e);
        }
    }
    
    private RouteDefinition convertToRouteDefinition(GatewayRouteItem routeItem) {
        RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.setId(routeItem.getId());
        routeDefinition.setUri(URI.create(routeItem.getUri()));
        
        if (routeItem.getPredicates() != null) {
            List<PredicateDefinition> predicates = routeItem.getPredicates().stream()
                    .map(this::createPredicateDefinition)
                    .collect(Collectors.toList());
            routeDefinition.setPredicates(predicates);
        }
        
        if (routeItem.getFilters() != null) {
            List<FilterDefinition> filters = routeItem.getFilters().stream()
                    .map(this::createFilterDefinition)
                    .collect(Collectors.toList());
            routeDefinition.setFilters(filters);
        }
        
        return routeDefinition;
    }
    
    private GatewayRouteItem convertToGatewayRouteItem(RouteDefinition routeDefinition) {
        GatewayRouteItem item = new GatewayRouteItem();
        item.setId(routeDefinition.getId());
        item.setUri(routeDefinition.getUri().toString());
        
        if (routeDefinition.getPredicates() != null) {
            List<String> predicates = routeDefinition.getPredicates().stream()
                    .map(pred -> pred.getName() + "=" + pred.getArgs())
                    .collect(Collectors.toList());
            item.setPredicates(predicates);
        }
        
        if (routeDefinition.getFilters() != null) {
            List<String> filters = routeDefinition.getFilters().stream()
                    .map(filter -> filter.getName() + "=" + filter.getArgs())
                    .collect(Collectors.toList());
            item.setFilters(filters);
        }
        
        return item;
    }
    
    private PredicateDefinition createPredicateDefinition(String predicateStr) {
        PredicateDefinition predicate = new PredicateDefinition();
        String[] parts = predicateStr.split("=", 2);
        predicate.setName(parts[0]);
        if (parts.length > 1) {
            predicate.addArg("pattern", parts[1]);
        }
        return predicate;
    }
    
    private FilterDefinition createFilterDefinition(String filterStr) {
        FilterDefinition filter = new FilterDefinition();
        String[] parts = filterStr.split("=", 2);
        filter.setName(parts[0]);
        if (parts.length > 1) {
            filter.addArg("args", parts[1]);
        }
        return filter;
    }
}
