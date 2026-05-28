package org.example.gateway.entity;

import lombok.Data;

import java.util.List;

@Data
public class GatewayRouteItem {
    private String id;
    private String uri;
    private List<String> filters;
    private List<String> predicates;
}
