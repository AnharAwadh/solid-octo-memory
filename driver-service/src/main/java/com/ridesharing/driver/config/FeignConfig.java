package com.ridesharing.driver.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import com.ridesharing.driver.exception.BadRequestException;
import com.ridesharing.driver.exception.ResourceNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign Clients.
 */
@Configuration
public class FeignConfig {
    
    /**
     * Set Feign logging level.
     * FULL - Log headers, body, and metadata.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
    
    /**
     * Custom error decoder to convert HTTP errors to exceptions.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            switch (response.status()) {
                case 400:
                    return new BadRequestException("Bad request to Customer Service");
                case 404:
                    return new ResourceNotFoundException("Resource not found in Customer Service");
                default:
                    return new RuntimeException("Error calling Customer Service: " + response.status());
            }
        };
    }
}
