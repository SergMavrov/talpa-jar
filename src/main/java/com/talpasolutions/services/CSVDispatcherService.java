package com.talpasolutions.services;

import com.talpasolutions.model.RouteInfo;
import com.talpasolutions.model.LocationType;
import com.talpasolutions.model.MapRoutes;
import com.talpasolutions.model.RouteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class implements Dijkstra algorithm to calculate the shortest route between nodes of graph.
 * The mapOfLocationAndRoutes is indeed graph which contains Locations as nodes.
 */
@Slf4j
@Service
public class CSVDispatcherService implements DispatcherService {

    private static final Double SPEED_METER_PRO_SEC = 16.6667; // 60 km/h = 16.6667 m/s

    private final DataProcessor dataProcessor;
    private final DistanceService distanceService;

    private final MapRoutes mapOfLocationsAndRoutes; // map of all possible locations and routes

    @Autowired
    public CSVDispatcherService(DataProcessor dataProcessor, DistanceService distanceService) {
        this.dataProcessor = dataProcessor;
        this.distanceService = distanceService;
        this.mapOfLocationsAndRoutes = new MapRoutes();
    }

    @PostConstruct
    public void init() {
        initPossibleGeoRoutes();
    }

    /**
     * Define all possible locations and routes based on the data from storage and put them into the map.
     */
    private void initPossibleGeoRoutes() {
        log.info("Init Geo Routes CUSTOMER <- STORE <- DEPOT, distance in METERS");
        //We know that we are starting from DEPOT, next stop is STORE and
        //the last destination is CUSTOMER. So the 3 stages all possible routes must be described.
        dataProcessor.readCustomers().forEach(
                customer -> {
                    dataProcessor.readStores().forEach(store -> {
                        //define all potential STORES with distances for the each CUSTOMER
                        customer.addSource(store, distanceService.defineDistanceInMeters(customer, store));
                        dataProcessor.readDepots().forEach(depot -> {
                            //define all potential DEPOTS with instances for the each STORE
                            store.addSource(depot, distanceService.defineDistanceInMeters(store, depot));
                            mapOfLocationsAndRoutes.addRoute(depot);
                        });
                        mapOfLocationsAndRoutes.addRoute(store);
                    });
                    mapOfLocationsAndRoutes.addRoute(customer);
                });
        log.info("Init Geo Routes is finished");
    }

    /**
     * Calculate the shortest path for all Locations using provided Location as destination.
     *
     * @param destination the location which must end point for all other location.
     */
    private void calculateShortestRouteFromDestination(RouteInfo destination) {
        destination.setDistance((double) 0);
        Set<RouteInfo> visitedLocations = new HashSet<>();
        Set<RouteInfo> unvisitedLocations = new HashSet<>();
        unvisitedLocations.add(destination);
        while (!unvisitedLocations.isEmpty()) {
            Optional<RouteInfo> closestLocation = unvisitedLocations.stream()
                    .min(Comparator.comparing(RouteInfo::getDistance));
            closestLocation.ifPresent(current -> { //will always present since we have checked the unvisitedLocations is not empty
                unvisitedLocations.remove(current);
                current.getSources()
                        .forEach((source, distance) -> {
                            if (!visitedLocations.contains(source)) {
                                calculateMinimumDistance(source, distance, current);
                                unvisitedLocations.add(source);
                            }
                        });
                visitedLocations.add(current);
            });
        }
    }

    private void calculateMinimumDistance(RouteInfo checkLocation, Double distance, RouteInfo currentLocation) {
        Double sourceDistance = currentLocation.getDistance();
        if (sourceDistance + distance < checkLocation.getDistance()) {
            checkLocation.setDistance(sourceDistance + distance);
            List<RouteInfo> shortestPath = new LinkedList<>(currentLocation.getShortestRoute());
            shortestPath.add(currentLocation);
            checkLocation.setShortestRoute(shortestPath);
        }
    }

    public List<RouteResult> getMinimalRoutesForRegisteredCustomers() {
        // take all CUSTOMERS from the map and prepare the best route to the closest combination STORE-DEPOT
        List<RouteResult> results = mapOfLocationsAndRoutes.getRoutes().stream()
                .filter(l -> l.getType().equals(LocationType.CUSTOMER))
                .sorted(Comparator.comparing(RouteInfo::getName))
                .map(this::getBestRoute).collect(Collectors.toList());
        dataProcessor.saveRoutes(results);
        return results;
    }

    private RouteResult getBestRoute(RouteInfo customer) {
        // clean route information from previous calculation
        mapOfLocationsAndRoutes.getRoutes().forEach(RouteInfo::clearRouteInfo);
        calculateShortestRouteFromDestination(customer); //calculate all minim routes from the CUSTOMER to DEPOTS
        // for all possible locations
        // We know that we must start from DEPOT so take the DEPOT from the closest combination DEPOT-STORE
        RouteInfo closestDepot = mapOfLocationsAndRoutes.getRoutes().stream()
                .filter(l -> l.getType().equals(LocationType.DEPOT))
                .min(Comparator.comparing(RouteInfo::getDistance))
                .orElseThrow(() -> new IllegalStateException(String.format("There is no closest DEPOT for %s", customer)));
        // take the STORE from the closest combination DEPOT-STORE route
        RouteInfo closestStore = closestDepot.getShortestRoute().stream()
                .filter(l -> l.getType().equals(LocationType.STORE)).findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("There is no closest STORE for %s", customer)));
        return RouteResult.builder()
                .customer(customer)
                .store(closestStore)
                .depot(closestDepot)
                .duration(closestDepot.getDistance() / SPEED_METER_PRO_SEC)
                .distance(closestDepot.getDistance())
                .build();
    }
}
