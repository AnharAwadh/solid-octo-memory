package com.ridesharing.customer.service;

import com.ridesharing.customer.dto.RideRequest;
import com.ridesharing.customer.dto.RideResponse;
import com.ridesharing.customer.dto.RideStatus;
import com.ridesharing.customer.entity.Ride;
import com.ridesharing.customer.exception.BadRequestException;
import com.ridesharing.customer.exception.ResourceNotFoundException;
import com.ridesharing.customer.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for ride operations.
 */
@Service
@RequiredArgsConstructor
public class RideService {
    
    private static final Logger log = LoggerFactory.getLogger(RideService.class);
    
    private final RideRepository rideRepository;
    
    @Transactional
    public RideResponse requestRide(Long customerId, String customerName, RideRequest request) {
        log.info("Creating ride request for customer: {} (ID: {})", customerName, customerId);
        
        Ride ride = Ride.builder()
                .customerId(customerId)
                .customerName(customerName)
                .pickupLocation(request.getPickupLocation())
                .dropOffLocation(request.getDropOffLocation())
                .status(RideStatus.PENDING)
                .build();
        
        Ride savedRide = rideRepository.save(ride);
        log.info("Ride created with ID: {}", savedRide.getId());
        
        return mapToResponse(savedRide);
    }
    
    @Transactional(readOnly = true)
    public List<RideResponse> getCustomerRideHistory(Long customerId) {
        log.info("Fetching ride history for customer ID: {}", customerId);
        return rideRepository.findByCustomerIdOrderByRequestedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    


    
    @Transactional(readOnly = true)
    public List<RideResponse> getPendingRides() {
        log.info("Fetching all pending rides");
        return rideRepository.findByStatusOrderByRequestedAtAsc(RideStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public RideResponse assignDriver(Long rideId, Long driverId, String driverName) {
        log.info("Assigning driver {} to ride ID: {}", driverName, rideId);
        
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));
        
        if (ride.getStatus() != RideStatus.PENDING) {
            throw new BadRequestException("Ride is no longer available");
        }
        
        ride.setDriverId(driverId);
        ride.setDriverName(driverName);
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());
        
        return mapToResponse(rideRepository.save(ride));
    }
    
    @Transactional(readOnly = true)
    public List<RideResponse> getDriverRideHistory(Long driverId) {
        log.info("Fetching ride history for driver ID: {}", driverId);
        return rideRepository.findByDriverIdOrderByAcceptedAtDesc(driverId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    

    
    private RideResponse mapToResponse(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .customerId(ride.getCustomerId())
                .customerName(ride.getCustomerName())
                .driverId(ride.getDriverId())
                .driverName(ride.getDriverName())
                .pickupLocation(ride.getPickupLocation())
                .dropOffLocation(ride.getDropOffLocation())
                .status(ride.getStatus())
                .requestedAt(ride.getRequestedAt())
                .acceptedAt(ride.getAcceptedAt())
                .completedAt(ride.getCompletedAt())
                .build();
    }
}
