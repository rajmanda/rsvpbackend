package com.gala.celebrations.rsvpbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!local & !test") // Activates when profile is NOT local AND NOT test
public class ProductionGmailConfig {

    // It's a best practice to use a structured logger instead of System.out
    private static final Logger logger = LoggerFactory.getLogger(ProductionGmailConfig.class);

    private final ConfigurableEnvironment environment;
    private final String gmailPasswordFilePath;

    public ProductionGmailConfig(ConfigurableEnvironment environment,
                                 @Value("${spring.mail.passwordFilePath}") String gmailPasswordFilePath) {
        this.environment = environment;
        this.gmailPasswordFilePath = gmailPasswordFilePath;
    }

    @PostConstruct
    public void init() throws IOException {
        logger.debug("ProductionGmailConfig is active. Reading password from file path: {}", gmailPasswordFilePath);

        if (gmailPasswordFilePath == null || gmailPasswordFilePath.isBlank()) {
            throw new IllegalStateException("spring.mail.passwordFilePath is required for non-local/non-test profiles.");
        }

        // Read the file contents
        String passwordFromFile = new String(Files.readAllBytes(Paths.get(gmailPasswordFilePath))).trim();

        // --- START: Added for debugging ---
        // SECURITY WARNING: Avoid logging raw secrets in production environments.
        // This is intended for temporary debugging only.
        logger.info("Debug: Password successfully read from file. Length: {}", passwordFromFile.length());
        // For higher security, you might log a masked version instead:
        // logger.debug("Password read. Starts with: {}", passwordFromFile.length() > 4 ? passwordFromFile.substring(0, 4) : "****");
        // --- END: Added for debugging ---

        if (passwordFromFile.isEmpty()) {
            throw new IllegalStateException("Password file is empty: " + gmailPasswordFilePath);
        }

        // Dynamically set the spring.mail.password property
        setSpringMailPassword(passwordFromFile);
        logger.debug("spring.mail.password has been dynamically set from file.");

        // --- START: Added for debugging ---
        printAllMailConfigurations();
        // --- END: Added for debugging ---
    }

    private void setSpringMailPassword(String password) {
        MutablePropertySources propertySources = environment.getPropertySources();
        String propertySourceName = "dynamicMailPassword";

        Map<String, Object> passwordMap = new HashMap<>();
        passwordMap.put("spring.mail.password", password);

        PropertySource<?> passwordPropertySource = new MapPropertySource(propertySourceName, passwordMap);
        propertySources.addFirst(passwordPropertySource);
    }

    /**
     * Helper method to print the current state of mail-related properties
     * directly from the Spring Environment for debugging purposes.
     */
    private void printAllMailConfigurations() {
        logger.info("--- Verifying Final Mail Configurations ---");
        logger.info("spring.mail.host = {}", environment.getProperty("spring.mail.host"));
        logger.info("spring.mail.port = {}", environment.getProperty("spring.mail.port"));
        logger.info("spring.mail.username = {}", environment.getProperty("spring.mail.username"));
        // This will now reflect the value read from the file because we used addFirst()
        logger.info("spring.mail.password = [VERIFIED FROM FILE]");
        logger.info("spring.mail.properties.mail.smtp.auth = {}", environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        logger.info("spring.mail.properties.mail.smtp.starttls.enable = {}", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        logger.info("-------------------------------------------");
    }
}