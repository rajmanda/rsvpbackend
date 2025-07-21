package com.gala.celebrations.rsvpbackend.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

public class MailPasswordFileProcessor implements EnvironmentPostProcessor {

    private static final String DEV_PROFILE = "dev";
    private static final String PASSWORD_FILE_PATH_PROPERTY = "spring.mail.passwordFilePath";
    private static final String TARGET_PASSWORD_PROPERTY = "spring.mail.password";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Check if the 'dev' profile is active
        if (environment.getActiveProfiles().length == 0 || !Arrays.asList(environment.getActiveProfiles()).contains(DEV_PROFILE)) {
            System.out.println("MailPasswordFileProcessor: 'dev' profile not active. Skipping password file processing.");
            return;
        }

        // Get the path to the password file from the environment
        String passwordFilePath = environment.getProperty(PASSWORD_FILE_PATH_PROPERTY);

        if (passwordFilePath == null || passwordFilePath.isEmpty()) {
            System.out.println("MailPasswordFileProcessor: No '" + PASSWORD_FILE_PATH_PROPERTY + "' found in 'dev' profile. Skipping.");
            return;
        }

        FileSystemResource resource = new FileSystemResource(passwordFilePath);

        if (!resource.exists()) {
            System.err.println("MailPasswordFileProcessor: ERROR - Password file not found at " + passwordFilePath);
            return;
        }
        if (!resource.isReadable()) {
            System.err.println("MailPasswordFileProcessor: ERROR - Password file at " + passwordFilePath + " is not readable.");
            return;
        }

        try {
            // Read the content of the file
            String password = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();

            // Create a new Properties object to hold our dynamic password
            Properties props = new Properties();
            props.put(TARGET_PASSWORD_PROPERTY, password);

            // Add this new property source to the environment.
            // addFirst ensures it has the highest precedence,
            // overriding any spring.mail.password defined in application.yaml/properties.
            environment.getPropertySources().addFirst(new PropertiesPropertySource("mailPasswordFromFile", props));

            System.out.println("MailPasswordFileProcessor: Successfully loaded '" + TARGET_PASSWORD_PROPERTY + "' from file: " + passwordFilePath);

        } catch (IOException e) {
            System.err.println("MailPasswordFileProcessor: ERROR reading password file " + passwordFilePath + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("MailPasswordFileProcessor: An unexpected error occurred while processing password file: " + e.getMessage());
        }
    }
}
