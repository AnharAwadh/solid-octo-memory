package com.ridesharing.customer.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String userId = request.getHeader("X-User-Id");
            String userName = request.getHeader("X-User-Name");
            String userRole = request.getHeader("X-User-Role");
            
            log.debug("Received headers - UserId: {}, UserName: {}, UserRole: {}", userId, userName, userRole);
            
            if (StringUtils.hasText(userId) && StringUtils.hasText(userName) && StringUtils.hasText(userRole)) {
                UserPrincipal userPrincipal = UserPrincipal.builder()
                        .id(Long.parseLong(userId))
                        .username(userName)
                        .role(userRole)
                        .build();
                
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Set authentication for user: {}, role: ROLE_{}", userName, userRole);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}
