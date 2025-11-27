package com.ridesharing.customer.controller;

import com.ridesharing.customer.dto.ApiResponse;
import com.ridesharing.customer.dto.RideResponse;
import com.ridesharing.customer.service.RideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/customer/internal")
@RequiredArgsConstructor
@Log4j2
public class InternalController {
    

    private final RideService rideService;
    
    @GetMapping("/rides/pending")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getPendingRides() {
        log.info("Internal: Fetching pending rides");
        return ResponseEntity.ok(ApiResponse.success(rideService.getPendingRides()));
    }
    
    @PostMapping("/rides/{rideId}/assign")
    public ResponseEntity<ApiResponse<RideResponse>> assignDriver(
            @PathVariable Long rideId,
            @RequestParam Long driverId,
            @RequestParam String driverName) {
        
        log.info("Internal: Assigning driver {} to ride ID: {}", driverName, rideId);
        RideResponse ride = rideService.assignDriver(rideId, driverId, driverName);
        return ResponseEntity.ok(ApiResponse.success("Driver assigned successfully", ride));
    }
    
    @GetMapping("/rides/driver/{driverId}")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getDriverRideHistory(
            @PathVariable Long driverId) {
        
        log.info("Internal: Fetching ride history for driver ID: {}", driverId);
        return ResponseEntity.ok(ApiResponse.success(rideService.getDriverRideHistory(driverId)));
    }
    

}
