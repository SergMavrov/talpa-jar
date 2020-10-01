package com.talpasolutions.services;

import com.talpasolutions.model.RouteInfo;
import org.springframework.stereotype.Service;

@Service
public class EarthDistanceService implements DistanceService {

    private static final double EARTH_RADIUS = 6371;// Approximate radius of the earth in kilometers

    public Double defineDistanceInMeters(RouteInfo start, RouteInfo finish) {
        return Math.acos(
                Math.sin(start.getLatitude() * Math.PI / 180.0) * Math.sin(finish.getLatitude() * Math.PI / 180.0) +
                        Math.cos(start.getLatitude() * Math.PI / 180.0) * Math.cos(finish.getLatitude() * Math.PI / 180.0) *
                                Math.cos((start.getLongitude() - finish.getLongitude()) * Math.PI / 180.0))
                * EARTH_RADIUS * 1000;
    }

}
