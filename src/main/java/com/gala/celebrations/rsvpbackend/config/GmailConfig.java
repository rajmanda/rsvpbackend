package com.gala.celebrations.rsvpbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ResourceUtils;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!local & !test") // Activates when profile is NOT local AND NOT test 
public class GmailConfig {

     private static final Logger logger = LoggerFactory.getLogger(GmailConfig.class);

    @Autowired // Use @Autowired to inject ConfigurableEnvironment
    private ConfigurableEnvironment environment;

    @Value("${spring.mail.password}")
    private String gmailPassword;


    @Value("${spring.mail.passwordFilePath:}")
    private String gmailPasswordFilePath;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @PostConstruct
    public void init() throws IOException {
       
        try {
            // Read the file contents
            String fileContents = new String(Files.readAllBytes(Paths.get(gmailPasswordFilePath)));
    
            // Print the file contents for debugging
            logger.debug("Debug: File contents:");
            logger.debug(fileContents);
    
            // Trim the password and assign it
            gmailPassword = fileContents.trim();
            setSpringMailPassword(gmailPassword);
            
            // Print the trimmed password for debugging
            logger.debug("Debug: Trimmed password: " + gmailPassword);
        } catch (NoSuchFileException e) {
            logger.error("Debug: File not found, using environment variable for password.");
            gmailPassword = environment.getProperty("spring.mail.password", "");
        }
        
        if (gmailPassword == null || gmailPassword.isEmpty()) {
            throw new IllegalStateException("GMAIL_PASSWORD environment variable or file is required for non-local profiles.");
        }
        
        logger.debug("Debug: GMAIL Password updated.");
    }
                
    private void setSpringMailPassword(String gmailPassword) {
        logger.debug("Debug: setSpringMailPassword method called."); // Debug 1
        logger.debug("Debug: gmailPassword received: " + gmailPassword); // Debug 2

        MutablePropertySources propertySources = environment.getPropertySources();
        logger.debug("Debug: Number of property sources before modification: " + propertySources.size()); // Debug 3

        String propertySourceName = "gmailPasswordPropertySource";
        logger.debug("Debug: propertySourceName: " + propertySourceName); // Debug 4

        if (propertySources.contains(propertySourceName)) {
            logger.debug("Debug: Property source '" + propertySourceName + "' already exists. Removing it."); // Debug 5
            propertySources.remove(propertySourceName);
        } else {
            logger.error("Debug: Property source '" + propertySourceName + "' does not exist."); // Debug 6
        }

        logger.debug("Debug: Number of property sources after potential removal: " + propertySources.size()); // Debug 7

        Map<String, Object> passwordMap = new HashMap<>();
        passwordMap.put("spring.mail.password", gmailPassword);
        logger.debug("Debug: passwordMap created: " + passwordMap); // Debug 8

        PropertySource<?> passwordPropertySource = new MapPropertySource(propertySourceName, passwordMap);
        logger.debug("Debug: passwordPropertySource created: " + passwordPropertySource); // Debug 9

        propertySources.addFirst(passwordPropertySource);
        logger.debug("Debug: New property source added to the beginning."); // Debug 10
        logger.debug("Debug: Number of property sources after adding: " + propertySources.size()); // Debug 11
        logger.debug("Debug: Property sources after adding: " + propertySources); // Debug 12
        logger.debug("Debug: setSpringMailPassword method finished."); // Debug 13
    }

}
