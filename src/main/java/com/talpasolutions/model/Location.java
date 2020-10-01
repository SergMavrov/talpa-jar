package com.talpasolutions.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@Slf4j
public class Location implements RouteInfo {

    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private Double latitude;
    @NotBlank
    private Double longitude;
    @NotBlank
    private LocationType type;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RouteInfo> shortestRoute;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Double distance;
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Map<RouteInfo, Double> sources;

    public Double getDistance() {
        if (distance == null) {
            distance = Double.MAX_VALUE;
        }
        return distance;
    }

    public List<RouteInfo> getShortestRoute() {
        if (shortestRoute == null) {
            shortestRoute = new LinkedList<>();
        }
        return shortestRoute;
    }

    public Map<RouteInfo, Double> getSources() {
        if (sources == null) {
            sources = new HashMap<>();
        }
        return sources;
    }

    @Override
    public void addSource(RouteInfo source, Double distance) {
        log.info(String.format("%s <- %s = %s", name, source.getName(), distance));
        getSources().put(source, distance);
    }

    @Override
    public void clearRouteInfo() {
        distance = null;
        shortestRoute = null;
    }
}
