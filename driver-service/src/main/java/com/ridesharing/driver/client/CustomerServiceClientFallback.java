package com.ridesharing.driver.client;

import com.ridesharing.driver.dto.ApiResponse;
import com.ridesharing.driver.dto.RideResponse;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
@Log4j2
public class CustomerServiceClientFallback implements CustomerServiceClient {
    

    @Override
    public ApiResponse<List<RideResponse>> getPendingRides() {
        log.warn("Fallback: Customer Service unavailable - getPendingRides");
        return ApiResponse.error("Customer Service is currently unavailable");
    }
    
    @Override
    public ApiResponse<RideResponse> assignDriver(Long rideId, Long driverId, String driverName) {
        log.warn("Fallback: Customer Service unavailable - assignDriver for ride {}", rideId);
        return ApiResponse.error("Customer Service is currently unavailable");
    }
    
    @Override
    public ApiResponse<List<RideResponse>> getDriverRideHistory(Long driverId) {
        log.warn("Fallback: Customer Service unavailable - getDriverRideHistory for driver {}", driverId);
        return ApiResponse.<List<RideResponse>>builder()
                .success(false)
                .message("Customer Service is currently unavailable")
                .data(Collections.emptyList())
                .build();
    }
    

}
