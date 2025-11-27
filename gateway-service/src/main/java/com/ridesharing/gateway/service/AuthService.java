package com.ridesharing.gateway.service;

import com.ridesharing.gateway.dto.*;
import com.ridesharing.gateway.entity.User;
import com.ridesharing.gateway.exception.BadRequestException;
import com.ridesharing.gateway.exception.ConflictException;
import com.ridesharing.gateway.exception.UnauthorizedException;
import com.ridesharing.gateway.repository.UserRepository;
import com.ridesharing.gateway.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {
    

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    
    private static final long SESSION_TIMEOUT = 3600L; // 1 hour in seconds
    
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        try {
            // Authenticate using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            // Set authentication in SecurityContext
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            
            // Create session and store SecurityContext
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);
            session.setMaxInactiveInterval((int) SESSION_TIMEOUT);
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            UserResponse userResponse = UserResponse.builder()
                    .id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .phone(userDetails.getPhone())
                    .role(Role.valueOf(userDetails.getRole()))
                    .build();
            
            log.info("Login successful for user: {}, session: {}", request.getUsername(), session.getId());
            return LoginResponse.of(session.getId(), SESSION_TIMEOUT, userResponse);
            
        } catch (BadCredentialsException e) {
            log.warn("Login failed: Invalid credentials for user - {}", request.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        }
    }
    
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Registration attempt for user: {}, role: {}", request.getUsername(), request.getRole());
        
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: Username already exists - {}", request.getUsername());
            throw new ConflictException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new ConflictException("Email already exists");
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .enabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}, id: {}", savedUser.getUsername(), savedUser.getId());
        
        return mapToUserResponse(savedUser);
    }
    


    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
