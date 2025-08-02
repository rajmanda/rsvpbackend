package com.gala.celebrations.rsvpbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class ServerConfig {

    private final Environment environment;

    public ServerConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public WebFilter contextPathWebFilter() {
        String contextPath = "/";
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (request.getURI().getPath().startsWith(contextPath)) {
                return chain.filter(exchange);
            }
            return chain.filter(
                exchange.mutate()
                    .request(request.mutate().path(contextPath).build())
                    .build()
            );
        };
    }
}
