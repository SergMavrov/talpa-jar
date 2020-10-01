package com.talpasolutions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MapRoutes {

    private Set<RouteInfo> routes;

    public Set<RouteInfo> getRoutes() {
        if (routes == null) {
            routes = new HashSet<>();
        }
        return routes;
    }

    public void addRoute(RouteInfo route) {
        getRoutes().add(route);
    }

}
