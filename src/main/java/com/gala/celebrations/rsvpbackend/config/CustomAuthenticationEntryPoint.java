package com.gala.celebrations.rsvpbackend.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {

        // Extract the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Check if the Authorization header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the token
            String token = authHeader.substring(7);
            // Print the token for debugging purposes
            System.out.println("Received JWT token: " + token);
        } else {
            System.out.println("No JWT token found in the Authorization header.");
        }

        // Set the response status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Write a custom error message to the response
        System.out.println("error: Invalid or missing JWT token.");
        response.getWriter().write("{\"error\": \"Invalid or missing JWT token.\"}");
    }
}


