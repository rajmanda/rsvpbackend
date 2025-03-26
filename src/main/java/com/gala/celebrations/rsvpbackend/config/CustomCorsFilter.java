package com.gala.celebrations.rsvpbackend.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("customCorsFilterBean")
@Order(Ordered.HIGHEST_PRECEDENCE) // Ensure this filter runs early
public class CustomCorsFilter implements Filter {

    @Value("${cors.allowed.origins:https://4200-cs-,https://rajmanda-dev.com,https://shravanikalyanam.com}")
    private String allowedOrigins;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String origin = httpRequest.getHeader("Origin");
        System.out.println("Incoming request from origin: " + origin);

        List<String> allowedOriginList = Arrays.asList(allowedOrigins.split(","));

        if (origin != null) {
            boolean isAllowed = false;
            for (String allowedOrigin : allowedOriginList) {
                if (origin.startsWith(allowedOrigin) || origin.equals(allowedOrigin)) {
                    isAllowed = true;
                    break;
                }
            }

            if (isAllowed) {
                System.out.println("Origin allowed: " + origin);
                httpResponse.setHeader("Access-Control-Allow-Origin", origin);
                httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
                httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpResponse.setHeader("Access-Control-Expose-Headers", "Authorization"); // Expose custom headers
            } else {
                System.out.println("Origin not allowed: " + origin);
                // Handle OPTIONS request even if the origin is not allowed
                if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                    System.out.println("Handling preflight OPTIONS request for not allowed origin");
                    httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            // Handle preflight OPTIONS request
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                System.out.println("Handling preflight OPTIONS request");
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}