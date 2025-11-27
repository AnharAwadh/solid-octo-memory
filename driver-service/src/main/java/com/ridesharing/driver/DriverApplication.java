package com.ridesharing.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients
public class DriverApplication {
    
    private static final Logger log = LoggerFactory.getLogger(DriverApplication.class);
    
    public static void main(String[] args) {
        log.info("Starting Driver Service...");
        SpringApplication.run(DriverApplication.class, args);
        log.info("Driver Service started successfully on port 8082");
    }
}
