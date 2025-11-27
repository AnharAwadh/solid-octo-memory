package com.ridesharing.gateway.controller;

import com.ridesharing.gateway.security.CurrentUser;
import com.ridesharing.gateway.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@Log4j2
public class CustomerProxyController {
    

    private final RestTemplate restTemplate;
    
    @Value("${customer-service.url:http://localhost:8081}")
    private String customerServiceUrl;
    
    @PostMapping("/rides")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> requestRide(
            @CurrentUser CustomUserDetails currentUser,
            @RequestBody String body) {
        
        log.info("Proxying ride request for customer: {}", currentUser.getUsername());
        return forwardRequest("/api/customer/rides", HttpMethod.POST, body, currentUser);
    }
    
    @GetMapping("/rides/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> getRideHistory(@CurrentUser CustomUserDetails currentUser) {
        log.info("Proxying ride history request for customer: {}", currentUser.getUsername());
        return forwardRequest("/api/customer/rides/history", HttpMethod.GET, null, currentUser);
    }
    

    

    
    private ResponseEntity<String> forwardRequest(String path, HttpMethod method, String body, CustomUserDetails user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (user != null) {
            headers.set("X-User-Id", String.valueOf(user.getId()));
            headers.set("X-User-Name", user.getUsername());
            headers.set("X-User-Role", user.getRole());
        }
        
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        String url = customerServiceUrl + path;
        
        log.debug("Forwarding {} request to: {}", method, url);
        
        try {
            return restTemplate.exchange(url, method, entity, String.class);
        } catch (Exception e) {
            log.error("Error forwarding request to Customer Service: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("{\"success\":false,\"message\":\"Customer Service unavailable\"}");
        }
    }
}
