package com.talpasolutions.services;

import com.talpasolutions.model.RouteInfo;
import com.talpasolutions.model.RouteResult;

import java.util.List;

public interface DataProcessor {

    List<RouteInfo> readDepots();

    List<RouteInfo> readStores();

    List<RouteInfo> readCustomers();

    void saveRoutes(List<RouteResult> results);

}
