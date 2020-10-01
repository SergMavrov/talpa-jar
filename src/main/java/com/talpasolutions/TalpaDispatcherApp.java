package com.talpasolutions;

import com.talpasolutions.model.RouteResult;
import com.talpasolutions.services.DispatcherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = {"com.talpasolutions"})
public class TalpaDispatcherApp implements CommandLineRunner {

    private final DispatcherService dispatcherService;

    @Autowired
    public TalpaDispatcherApp(DispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }

    public static void main(String[] args) {
        SpringApplication.run(TalpaDispatcherApp.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("The application is started");
        log.info("-*-*-*-*-*-*-*-*-*-*-*-*-*");
        for (RouteResult routeResult : dispatcherService.getMinimalRoutesForRegisteredCustomers()) {
            log.info(String.format("%nFor CUSTOMER %s : %s",
                    routeResult.getCustomer().getName(), routeResult));
        }
    }

}
