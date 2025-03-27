package com.gala.celebrations.rsvpbackend.config;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class CrossOriginLoggerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        String origin = request.getHeader("Origin");
        
        // Only log if Origin header exists (cross-origin request)
        if (origin != null && !origin.isEmpty()) {
            System.out.println("Cross-origin request detected. Origin: " + origin);
        }
        
        filterChain.doFilter(request, response);
    }
}