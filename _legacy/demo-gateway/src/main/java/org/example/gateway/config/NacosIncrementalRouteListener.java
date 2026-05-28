package org.example.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.goodidea.gateway.entity.GatewayRouteItem;
import com.goodidea.gateway.service.GatewayRouteCacheService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@RefreshScope
public class NacosIncrementalRouteListener {

    private final ConcurrentHashMap<String, RouteDefinition> nacosRouteCache = new ConcurrentHashMap<>();
    
    @Autowired
    private GatewayRouteCacheService gatewayRouteCacheService;
    
    @Autowired
    private ApplicationEventPublisher publisher;
    
    @Autowired
    private DynamicRouteServiceImpl dynamicRouteService;

    @PostConstruct
    public void initializeNacosCache() {
        try {
            List<RouteDefinition> currentRoutes = gatewayRouteCacheService.getCurrentRouteDefinitions();
            for (RouteDefinition route : currentRoutes) {
                if (route.getId().startsWith("nacos-")) {
                    nacosRouteCache.put(route.getId(), route);
                }
            }
            log.info("Initialized Nacos route cache with {} routes", nacosRouteCache.size());
        } catch (Exception e) {
            log.error("Failed to initialize Nacos route cache", e);
        }
    }

    @NacosConfigListener(dataId = "${spring.application.name}-${spring.profiles.active}.yaml", groupId = "DEFAULT_GROUP")
    public void incrementalRefreshRoutes(String newConfig) {
        log.info("Received Nacos configuration change, processing routes...");
        
        try {
            List<RouteDefinition> newRoutes = parseConfigToRoutes(newConfig);
            Map<String, RouteDefinition> newRouteMap = newRoutes.stream()
                    .collect(Collectors.toMap(RouteDefinition::getId, r -> r));

            Set<String> oldRouteIds = nacosRouteCache.keySet();
            Set<String> newRouteIds = newRouteMap.keySet();

            Set<String> toDeleteIds = oldRouteIds.stream()
                    .filter(id -> !newRouteIds.contains(id))
                    .collect(Collectors.toSet());

            int deletedCount = 0;
            for (String routeId : toDeleteIds) {
                deleteRoute(routeId);
                deletedCount++;
            }

            int addedCount = 0;
            int updatedCount = 0;
            for (RouteDefinition newRoute : newRoutes) {
                String routeId = newRoute.getId();
                RouteDefinition oldRoute = nacosRouteCache.get(routeId);
                
                if (oldRoute == null) {
                    addRoute(newRoute);
                    addedCount++;
                } else if (!routeDefinitionsEqual(oldRoute, newRoute)) {
                    updateRoute(newRoute);
                    updatedCount++;
                }
            }

            log.info("Nacos route changes processed: {} added, {} updated, {} deleted", 
                    addedCount, updatedCount, deletedCount);

            if (addedCount > 0 || updatedCount > 0 || deletedCount > 0) {
                publisher.publishEvent(new RefreshRoutesEvent(this));
                log.info("Published RefreshRoutesEvent due to route changes");
            }

        } catch (Exception e) {
            log.error("Failed to process Nacos configuration change", e);
        }
    }

    private void addRoute(RouteDefinition route) {
        try {
            String result = dynamicRouteService.add(route);
            if ("success".equals(result)) {
                nacosRouteCache.put(route.getId(), route);
                
                GatewayRouteItem routeItem = convertToGatewayRouteItem(route);
                gatewayRouteCacheService.addRouteToCache(routeItem);
                
                log.debug("Successfully added Nacos route: {}", route.getId());
            } else {
                log.warn("Failed to add Nacos route: {}, result: {}", route.getId(), result);
            }
        } catch (Exception e) {
            log.error("Error adding Nacos route: {}", route.getId(), e);
        }
    }

    private void deleteRoute(String routeId) {
        try {
            dynamicRouteService.delete(routeId).subscribe();
            nacosRouteCache.remove(routeId);
            gatewayRouteCacheService.removeRouteFromCache(routeId);
            log.debug("Successfully deleted Nacos route: {}", routeId);
        } catch (Exception e) {
            log.error("Error deleting Nacos route: {}", routeId, e);
        }
    }
    
    private void updateRoute(RouteDefinition route) {
        try {
            String result = dynamicRouteService.update(route);
            if ("success".equals(result)) {
                nacosRouteCache.put(route.getId(), route);
                
                GatewayRouteItem routeItem = convertToGatewayRouteItem(route);
                gatewayRouteCacheService.addRouteToCache(routeItem);
                
                log.debug("Successfully updated Nacos route: {}", route.getId());
            } else {
                log.warn("Failed to update Nacos route: {}, result: {}", route.getId(), result);
            }
        } catch (Exception e) {
            log.error("Error updating Nacos route: {}", route.getId(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<RouteDefinition> parseConfigToRoutes(String config) {
        List<RouteDefinition> routes = new ArrayList<>();
        
        if (!StringUtils.hasText(config)) {
            return routes;
        }
        
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> configMap = yaml.load(config);
            
            Object springObj = configMap.get("spring");
            if (springObj instanceof Map) {
                Map<String, Object> spring = (Map<String, Object>) springObj;
                Object cloudObj = spring.get("cloud");
                
                if (cloudObj instanceof Map) {
                    Map<String, Object> cloud = (Map<String, Object>) cloudObj;
                    Object gatewayObj = cloud.get("gateway");
                    
                    if (gatewayObj instanceof Map) {
                        Map<String, Object> gateway = (Map<String, Object>) gatewayObj;
                        Object routesObj = gateway.get("routes");
                        
                        if (routesObj instanceof List) {
                            List<Map<String, Object>> routesList = (List<Map<String, Object>>) routesObj;
                            
                            for (Map<String, Object> routeMap : routesList) {
                                RouteDefinition routeDef = parseRouteDefinition(routeMap);
                                if (routeDef != null) {
                                    routeDef.setId("nacos-" + routeDef.getId());
                                    routes.add(routeDef);
                                }
                            }
                        }
                    }
                }
            }
            
            log.debug("Parsed {} routes from Nacos config", routes.size());
            
        } catch (Exception e) {
            log.error("Failed to parse Nacos configuration", e);
        }
        
        return routes;
    }
    
    @SuppressWarnings("unchecked")
    private RouteDefinition parseRouteDefinition(Map<String, Object> routeMap) {
        try {
            RouteDefinition routeDef = new RouteDefinition();
            
            String id = (String) routeMap.get("id");
            String uri = (String) routeMap.get("uri");
            
            if (!StringUtils.hasText(id) || !StringUtils.hasText(uri)) {
                return null;
            }
            
            routeDef.setId(id);
            routeDef.setUri(URI.create(uri));
            
            Object predicatesObj = routeMap.get("predicates");
            if (predicatesObj instanceof List) {
                List<Object> predicatesList = (List<Object>) predicatesObj;
                List<PredicateDefinition> predicates = new ArrayList<>();
                
                for (Object predObj : predicatesList) {
                    if (predObj instanceof String) {
                        PredicateDefinition pred = new PredicateDefinition((String) predObj);
                        predicates.add(pred);
                    } else if (predObj instanceof Map) {
                        Map<String, Object> predMap = (Map<String, Object>) predObj;
                        PredicateDefinition pred = new PredicateDefinition();
                        pred.setName((String) predMap.get("name"));
                        
                        Object argsObj = predMap.get("args");
                        if (argsObj instanceof Map) {
                            Map<String, String> args = (Map<String, String>) argsObj;
                            pred.setArgs(args);
                        }
                        predicates.add(pred);
                    }
                }
                routeDef.setPredicates(predicates);
            }
            
            Object filtersObj = routeMap.get("filters");
            if (filtersObj instanceof List) {
                List<Object> filtersList = (List<Object>) filtersObj;
                List<FilterDefinition> filters = new ArrayList<>();
                
                for (Object filterObj : filtersList) {
                    if (filterObj instanceof String) {
                        FilterDefinition filter = new FilterDefinition((String) filterObj);
                        filters.add(filter);
                    } else if (filterObj instanceof Map) {
                        Map<String, Object> filterMap = (Map<String, Object>) filterObj;
                        FilterDefinition filter = new FilterDefinition();
                        filter.setName((String) filterMap.get("name"));
                        
                        Object argsObj = filterMap.get("args");
                        if (argsObj instanceof Map) {
                            Map<String, String> args = (Map<String, String>) argsObj;
                            filter.setArgs(args);
                        }
                        filters.add(filter);
                    }
                }
                routeDef.setFilters(filters);
            }
            
            return routeDef;
            
        } catch (Exception e) {
            log.error("Failed to parse route definition from map: {}", routeMap, e);
            return null;
        }
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
    
    private boolean routeDefinitionsEqual(RouteDefinition route1, RouteDefinition route2) {
        if (!Objects.equals(route1.getId(), route2.getId())) return false;
        if (!Objects.equals(route1.getUri(), route2.getUri())) return false;
        if (!Objects.equals(route1.getPredicates(), route2.getPredicates())) return false;
        if (!Objects.equals(route1.getFilters(), route2.getFilters())) return false;
        return Objects.equals(route1.getMetadata(), route2.getMetadata());
    }
}