package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;

import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import org.springframework.core.env.Environment;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    private final Environment environment;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${spring.data.mongodb.passwordFilePath:}")
    private String mongoPasswordFilePath;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private String mongoPassword;

    public MongoConfig(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() throws IOException {
        if (!"local".equals(activeProfile)) {
            try {
                // Read the file contents
                String fileContents = new String(Files.readAllBytes(Paths.get(mongoPasswordFilePath)));

                // Print the file contents for debugging
                System.out.println("Debug: File contents:");
                System.out.println(fileContents);

                // Trim the password and assign it
                mongoPassword = fileContents.trim();

                // Print the trimmed password for debugging
                System.out.println("Debug: Trimmed password: " + mongoPassword);
            } catch (NoSuchFileException e) {
                System.out.println("Debug: File not found, using environment variable for password.");
                mongoPassword = environment.getProperty("spring.data.mongodb.password", "");
            }

            if (mongoPassword == null || mongoPassword.isEmpty()) {
                throw new IllegalStateException("MONGODB_PASSWORD environment variable or file is required for non-local profiles.");
            }

            // Replace the password in the URI
            mongoUri = mongoUri.replace(":<password>", ":" + mongoPassword);
            System.out.println("Debug: MongoDB URI updated.");
        } else {
            System.out.println("Debug: Active profile is 'local', skipping password fetch.");
        }
    }


    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, databaseName);
    }
}
