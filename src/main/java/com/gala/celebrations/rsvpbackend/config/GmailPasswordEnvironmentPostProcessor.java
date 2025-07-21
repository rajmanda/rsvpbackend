package com.gala.celebrations.rsvpbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class GmailPasswordEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(GmailPasswordEnvironmentPostProcessor.class);
    private static final String PROPERTY_SOURCE_NAME = "dynamicGmailPassword";
    private static final String GMAIL_PASSWORD_FILE_PATH_PROPERTY_DEV = "gmail.password.file";
    private static final String GMAIL_PASSWORD_FILE_PATH_PROPERTY_PROD = "spring.mail.passwordFilePath";
    private static final String SPRING_MAIL_PASSWORD_PROPERTY = "spring.mail.password";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String passwordFilePath = environment.getProperty(GMAIL_PASSWORD_FILE_PATH_PROPERTY_PROD);
        if (passwordFilePath == null || passwordFilePath.isBlank()) {
            passwordFilePath = environment.getProperty(GMAIL_PASSWORD_FILE_PATH_PROPERTY_DEV);
        }

        if (passwordFilePath == null || passwordFilePath.isBlank()) {
            logger.warn("Gmail password file path property not set. Skipping dynamic password configuration.");
            return;
        }

        Resource resource = new FileSystemResource(passwordFilePath);
        if (!resource.exists()) {
            logger.error("Gmail password file not found at: {}. Mail sending will likely fail.", passwordFilePath);
            return;
        }

        try {
            String gmailPassword = Files.readString(Paths.get(passwordFilePath)).trim();
            MapPropertySource propertySource = new MapPropertySource(
                    PROPERTY_SOURCE_NAME, Collections.singletonMap(SPRING_MAIL_PASSWORD_PROPERTY, gmailPassword));

            MutablePropertySources propertySources = environment.getPropertySources();
            propertySources.addFirst(propertySource); // Add with highest precedence
            logger.info("{} has been dynamically set from file.", SPRING_MAIL_PASSWORD_PROPERTY);
        } catch (IOException e) {
            logger.error("Error reading Gmail password from file: {}", passwordFilePath, e);
        }
    }
}


