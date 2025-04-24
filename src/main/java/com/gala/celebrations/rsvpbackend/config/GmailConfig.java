package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

@Configuration
@EnableMongoAuditing
public class GmailConfig {

    private final Environment environment;

    @Value("${spring.mail.password}")
    private String gmailPassword;


    @Value("${spring.mail.passwordFilePath:}")
    private String gmailPasswordFilePath;

    @Value("${spring.profiles.active:}")
    private String activeProfile;


    public GmailConfig(Environment environment) {
        this.environment = environment;
    }

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

                // Print the trimmed password for debugging
                System.out.println("Debug: Trimmed password: " + gmailPassword);
            } catch (NoSuchFileException e) {
                System.out.println("Debug: File not found, using environment variable for password.");
                gmailPassword = environment.getProperty("spring.data.mongodb.password", "");
            }

            if (gmailPassword == null || gmailPassword.isEmpty()) {
                throw new IllegalStateException("GMAIL_PASSWORD environment variable or file is required for non-local profiles.");
            }

            System.out.println("Debug: GMAIL Password updated.");
        } else {
            System.out.println("Debug: Active profile is 'local', skipping password fetch.");
        }
    }

}
