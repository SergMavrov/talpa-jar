package com.talpasolutions.services;

import com.talpasolutions.model.RouteInfo;

public interface DistanceService {

    /**
     * Calculate the distance in meters between two locations.
     * @param start the location as start point
     * @param finish the location as finish point
     * @return the distance in meters.
     */
    Double defineDistanceInMeters(RouteInfo start, RouteInfo finish);

}
