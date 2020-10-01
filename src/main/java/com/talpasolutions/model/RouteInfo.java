package com.talpasolutions.model;

import java.util.List;
import java.util.Map;

/**
 * The object contains geo coordinates and address. Additionally it contains the information about
 * possible sources of delivery. The class will be used for the shortest route to end destination calculation.
 * It means that after calculation each GeoInfo knows the shortest distance and shortest combination of location
 * which can rule us to the final destination. Usually the final destination is the customer so we can expect
 * that DEPOT will contains two locations: STORE->CUSTOMER, the STORE will know closest CUSTOMER, the CUSTOMER
 * usually has nothing since it is the final destination.
 */
public interface RouteInfo {

    String getName();
    String getAddress();
    Double getLatitude();
    Double getLongitude();

    /**
     * The distance to the final destination after calculation.
     * @return the distance in meters
     */
    Double getDistance();
    void setDistance(Double distance);

    /**
     * The combination of location which rule as the the final destination by shortest way.
     * @return the list of locations with the minim distance to the final destination.
     */
    List<RouteInfo> getShortestRoute();
    void setShortestRoute(List<RouteInfo> shortestRoute);

    /**
     * The list of locations which can potentially send us goods. Also we have the distances to these locations.
     * @return the list of potential locations-sources with distances.
     */
    Map<RouteInfo, Double> getSources();
    void addSource(RouteInfo destination, Double distance);

    /**
     * It can be DEPOT, STORE, CUSTOMER.
     * @return The type of the location.
     */
    LocationType getType();

    /**
     * Forget all precalculated routes and back to init values.
     */
    void clearRouteInfo();

}
