package com.gala.celebrations.rsvpbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("local") // Only active when the 'local' profile is active
public class SecurityConfigLocal {

    @Bean
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        http
                // Authorize all requests for local development
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Disable CSRF protection for local development,
                // as it's not needed and can interfere with Postman/curl
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
