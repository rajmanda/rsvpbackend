package com.gala.celebrations.rsvpbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            
            // Log the authentication error
            log.warn("Authentication error: {}", ex.getMessage());
            
            // Extract and log the Authorization header if present
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.debug("Received JWT token (first 10 chars): {}...", 
                    token.length() > 10 ? token.substring(0, 10) : token);
            } else {
                log.debug("No JWT token found in the Authorization header");
            }
            
            // Create error response
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Invalid or missing JWT token");
            responseBody.put("message", ex.getMessage());
            
            try {
                byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                response.writeWith(Mono.just(buffer)).subscribe();
            } catch (Exception e) {
                log.error("Error writing error response", e);
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }
}
