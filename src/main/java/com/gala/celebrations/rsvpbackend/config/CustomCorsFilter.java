package com.gala.celebrations.rsvpbackend.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // HttpServletRequest httpRequest = (HttpServletRequest) request;
        // HttpServletResponse httpResponse = (HttpServletResponse) response;

        // String origin = httpRequest.getHeader("Origin");

        // if (origin != null && (origin.startsWith("https://4200-cs-") ||
        //         origin.equals("https://rajmanda-dev.com") ||
        //         origin.equals("https://shravanikalyanam.com"))) {

        //     System.out.println("Origin allowed: " + origin);
        //     httpResponse.setHeader("Access-Control-Allow-Origin", origin);
        //     httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        //     httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        //     httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        //     // Handle preflight OPTIONS request
        //     if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
        //         System.out.println("Handling preflight OPTIONS request");
        //         httpResponse.setStatus(HttpServletResponse.SC_OK);
        //         return;
        //     }
        // }

        // chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}