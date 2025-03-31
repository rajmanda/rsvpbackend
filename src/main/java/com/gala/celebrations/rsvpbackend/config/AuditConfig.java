package com.gala.celebrations.rsvpbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth == null || !auth.isAuthenticated()) {
                return Optional.of("system"); // Fallback
            }

            Object principal = auth.getPrincipal();

            if (principal instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) principal;
                System.out.println("oidcUser:");
                // Try to get the preferred_username claim, or fallback to email or name
                String username = oidcUser.getPreferredUsername();
                if (username == null || username.isEmpty()) {
                    username = oidcUser.getEmail();
                }
                if (username == null || username.isEmpty()) {
                    username = oidcUser.getFullName();
                }
                if (username == null || username.isEmpty()) {
                    username = oidcUser.getName();
                }
                return Optional.ofNullable(username);
            } else if (principal instanceof OAuth2User) {
                System.out.println("OAuth2User:");
                OAuth2User oauth2User = (OAuth2User) principal;
                // Try to get the name attribute, or fallback to email
                String username = oauth2User.getAttribute("name");
                if (username == null || username.isEmpty()) {
                    username = oauth2User.getAttribute("email");
                }
                return Optional.ofNullable(username);
            } else {
                // Handle other types of principals if needed
                return Optional.of("unknown");
            }
        };
    }
}
