package com.gala.celebrations.rsvpbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class MailConfigLogger {

    private static final Logger logger = LoggerFactory.getLogger(MailConfigLogger.class);

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @PostConstruct
    public void logMailCredentials() {
        logger.debug("--- DEBUG: SMTP Credentials (REMOVE THIS IN PRODUCTION) ---");
        logger.debug("SMTP Username: {}", username);
        logger.debug("SMTP Password: {}", password);
        logger.debug("----------------------------------------------------------");
    }
}
