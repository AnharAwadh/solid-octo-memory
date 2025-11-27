package com.ridesharing.driver.client;

import com.ridesharing.driver.dto.ApiResponse;
import com.ridesharing.driver.dto.RideResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(
    name = "customer-service",
    url = "${customer-service.url:http://localhost:8081}",
    fallback = CustomerServiceClientFallback.class
)
public interface CustomerServiceClient {

    @GetMapping("/api/customer/internal/rides/pending")
    ApiResponse<List<RideResponse>> getPendingRides();
    

    @PostMapping("/api/customer/internal/rides/{rideId}/assign")
    ApiResponse<RideResponse> assignDriver(
            @PathVariable("rideId") Long rideId,
            @RequestParam("driverId") Long driverId,
            @RequestParam("driverName") String driverName
    );
    

    @GetMapping("/api/customer/internal/rides/driver/{driverId}")
    ApiResponse<List<RideResponse>> getDriverRideHistory(@PathVariable("driverId") Long driverId);
    


}
