package com.talpasolutions.services;

import com.talpasolutions.model.RouteResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DispatcherServiceTest extends GeneralServiceTest {

    private static final Double SPEED_METER_PRO_SEC = 16.6667; // 60 km/h = 16.6667 m/s

    @Autowired
    private DispatcherService dispatcherService;

    @Autowired
    private DistanceService earthDistanceService;

    @Order(0)
    @Test
    public void testBestRouteForTestData() {
        List<RouteResult> testResult = dispatcherService.getMinimalRoutesForRegisteredCustomers();
        Assert.assertNotNull(testResult);
        Assert.assertEquals(1, testResult.size()); // one customer in test data - one route
        Assert.assertEquals(testResult, Collections.singletonList(givenBestRouteResult()));
        Double distanceDepotStore = earthDistanceService.defineDistanceInMeters(givenTestDepot1(), givenTestStore1());
        Assert.assertEquals(Double.valueOf(7045.686070946999), distanceDepotStore);
        Double distanceStoreCustomer = earthDistanceService.defineDistanceInMeters(givenTestStore1(), givenTestCustomer1());
        Assert.assertEquals(Double.valueOf(4995.107389227975), distanceStoreCustomer);
        Double distanceDepotCustomer = distanceDepotStore + distanceStoreCustomer; //full distance must be a sum depot->store->customer
        Assert.assertEquals(distanceDepotCustomer, testResult.get(0).getDistance());
        Assert.assertEquals(Double.valueOf(distanceDepotCustomer/SPEED_METER_PRO_SEC), testResult.get(0).getDuration());
    }

}
