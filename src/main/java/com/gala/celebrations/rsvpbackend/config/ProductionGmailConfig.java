package com.gala.celebrations.rsvpbackend.config;

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
@Profile("!local & !test") // <-- This is the key: Activates when profile is NOT local AND NOT test
public class ProductionGmailConfig {

    private final ConfigurableEnvironment environment;
    private final String gmailPasswordFilePath;

    // Use constructor injection for required dependencies
    public ProductionGmailConfig(ConfigurableEnvironment environment,
                                 @Value("${spring.mail.passwordFilePath}") String gmailPasswordFilePath) {
        this.environment = environment;
        this.gmailPasswordFilePath = gmailPasswordFilePath;
    }

    @PostConstruct
    public void init() throws IOException {
        System.out.println("Debug: ProductionGmailConfig is active. Reading password from file path: " + gmailPasswordFilePath);

        if (gmailPasswordFilePath == null || gmailPasswordFilePath.isBlank()) {
            throw new IllegalStateException("spring.mail.passwordFilePath is required for non-local/non-test profiles.");
        }

        // Read the file contents
        String passwordFromFile = new String(Files.readAllBytes(Paths.get(gmailPasswordFilePath))).trim();

        if (passwordFromFile.isEmpty()) {
            throw new IllegalStateException("Password file is empty: " + gmailPasswordFilePath);
        }

        // Dynamically set the spring.mail.password property
        setSpringMailPassword(passwordFromFile);
        System.out.println("Debug: spring.mail.password has been dynamically set from file.");
    }

    private void setSpringMailPassword(String password) {
        // 1. Get the live, mutable stack of "rulebooks" (PropertySources)
        MutablePropertySources propertySources = environment.getPropertySources();

        // 2. Give our new "sticky note" a name so we can identify it if needed.
        String propertySourceName = "dynamicMailPassword";

        // 3. Create a simple map to hold our new property.
        //    This is the content of our "sticky note".
        Map<String, Object> passwordMap = new HashMap<>();
        passwordMap.put("spring.mail.password", password);

        // 4. Wrap our map in an official Spring "PropertySource".
        //    This turns our map into a formal "rulebook" layer that Spring understands.
        PropertySource<?> passwordPropertySource = new MapPropertySource(propertySourceName, passwordMap);

        // 5. This is the most important step: Add our new rulebook to the very top of the stack.
        //    'addFirst' ensures it has the highest priority and will be checked before any other source.
        propertySources.addFirst(passwordPropertySource);
    }
}
