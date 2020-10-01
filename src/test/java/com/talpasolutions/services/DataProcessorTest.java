package com.talpasolutions.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataProcessorTest extends GeneralServiceTest{

    @Autowired
    private DataProcessor dataProcessor;

    @Order(0)
    @Test
    public void testGetDepots() {
        Assert.assertEquals(dataProcessor.readDepots(), Arrays.asList(givenTestDepot1(), givenTestDepot2()));
    }

    @Order(1)
    @Test
    public void testGetStores() {
        Assert.assertEquals(dataProcessor.readStores(), Arrays.asList(givenTestStore1(), givenTestStore2()));
    }

    @Order(2)
    @Test
    public void testGetCustomers() {
        Assert.assertEquals(dataProcessor.readCustomers(), Collections.singletonList(givenTestCustomer1()));
    }

}
