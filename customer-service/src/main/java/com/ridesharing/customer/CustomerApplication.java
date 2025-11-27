package com.ridesharing.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for Customer Service.
 */
@SpringBootApplication
public class CustomerApplication {
    
    private static final Logger log = LoggerFactory.getLogger(CustomerApplication.class);
    
    public static void main(String[] args) {
        log.info("Starting Customer Service...");
        SpringApplication.run(CustomerApplication.class, args);
        log.info("Customer Service started successfully on port 8081");
    }
}
