package com.ridesharing.customer.controller;

import com.ridesharing.customer.dto.ApiResponse;
import com.ridesharing.customer.dto.RideRequest;
import com.ridesharing.customer.dto.RideResponse;
import com.ridesharing.customer.security.CurrentUser;
import com.ridesharing.customer.security.UserPrincipal;
import com.ridesharing.customer.service.RideService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/customer/rides")
@RequiredArgsConstructor
@Log4j2
public class CustomerController {
    

    private final RideService rideService;
    

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<RideResponse>> requestRide(
            @CurrentUser UserPrincipal currentUser,
            @Valid @RequestBody RideRequest request) {
        
        log.info("Ride request from authenticated customer: {} (ID: {})", 
                currentUser.getUsername(), currentUser.getId());
        
        RideResponse ride = rideService.requestRide(currentUser.getId(), currentUser.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ride requested successfully", ride));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<RideResponse>>> getRideHistory(
            @CurrentUser UserPrincipal currentUser) {
        
        log.info("Fetching ride history for authenticated customer: {} (ID: {})", 
                currentUser.getUsername(), currentUser.getId());
        
        List<RideResponse> rides = rideService.getCustomerRideHistory(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(rides));
    }
    

    

}
