package com.talpasolutions.services;

import com.talpasolutions.model.RouteResult;

import java.util.List;

public interface DispatcherService {

    /**
     * Prepare best routes for all found in the data customers.
     *
     * @return list of best routes related with each found customer.
     */
    List<RouteResult> getMinimalRoutesForRegisteredCustomers();

}
