package com.gala.celebrations.rsvpbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Configuration

public class GmailConfig {

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
        if (!"local".equals(activeProfile)) {
            try {
                // Read the file contents
                String fileContents = new String(Files.readAllBytes(Paths.get(gmailPasswordFilePath)));

                // Print the file contents for debugging
                System.out.println("Debug: File contents:");
                System.out.println(fileContents);

                // Trim the password and assign it
                gmailPassword = fileContents.trim();
                setSpringMailPassword(gmailPassword);
                
                                // Print the trimmed password for debugging
                                System.out.println("Debug: Trimmed password: " + gmailPassword);
                            } catch (NoSuchFileException e) {
                                System.out.println("Debug: File not found, using environment variable for password.");
                                gmailPassword = environment.getProperty("spring.mail.password", "");
                            }
                
                            if (gmailPassword == null || gmailPassword.isEmpty()) {
                                throw new IllegalStateException("GMAIL_PASSWORD environment variable or file is required for non-local profiles.");
                            }
                
                            System.out.println("Debug: GMAIL Password updated.");
                        } else {
                            System.out.println("Debug: Active profile is 'local', skipping password fetch.");
                        }
                    }
                
                    private void setSpringMailPassword(String gmailPassword) {
                        System.out.println("Debug: setSpringMailPassword method called."); // Debug 1
                        System.out.println("Debug: gmailPassword received: " + gmailPassword); // Debug 2
                
                        MutablePropertySources propertySources = environment.getPropertySources();
                        System.out.println("Debug: Number of property sources before modification: " + propertySources.size()); // Debug 3
                
                        String propertySourceName = "gmailPasswordPropertySource";
                        System.out.println("Debug: propertySourceName: " + propertySourceName); // Debug 4
                
                        if (propertySources.contains(propertySourceName)) {
                            System.out.println("Debug: Property source '" + propertySourceName + "' already exists. Removing it."); // Debug 5
                            propertySources.remove(propertySourceName);
                        } else {
                            System.out.println("Debug: Property source '" + propertySourceName + "' does not exist."); // Debug 6
                        }
                
                        System.out.println("Debug: Number of property sources after potential removal: " + propertySources.size()); // Debug 7
                
                        Map<String, Object> passwordMap = new HashMap<>();
                        passwordMap.put("spring.mail.password", gmailPassword);
                        System.out.println("Debug: passwordMap created: " + passwordMap); // Debug 8
                
                        PropertySource<?> passwordPropertySource = new MapPropertySource(propertySourceName, passwordMap);
                        System.out.println("Debug: passwordPropertySource created: " + passwordPropertySource); // Debug 9
                
                        propertySources.addFirst(passwordPropertySource);
                        System.out.println("Debug: New property source added to the beginning."); // Debug 10
                        System.out.println("Debug: Number of property sources after adding: " + propertySources.size()); // Debug 11
                        System.out.println("Debug: Property sources after adding: " + propertySources); // Debug 12
                        System.out.println("Debug: setSpringMailPassword method finished."); // Debug 13
                    }

}
