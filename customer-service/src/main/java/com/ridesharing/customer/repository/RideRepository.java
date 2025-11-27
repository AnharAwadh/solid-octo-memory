package com.ridesharing.customer.repository;

import com.ridesharing.customer.dto.RideStatus;
import com.ridesharing.customer.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    
    List<Ride> findByCustomerIdOrderByRequestedAtDesc(Long customerId);
    
    List<Ride> findByStatusOrderByRequestedAtAsc(RideStatus status);
    
    List<Ride> findByDriverIdOrderByAcceptedAtDesc(Long driverId);
}
