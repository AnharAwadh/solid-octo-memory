package com.ridesharing.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private Long driverId;
    private String driverName;
    private String pickupLocation;
    private String dropOffLocation;
    private RideStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
}
