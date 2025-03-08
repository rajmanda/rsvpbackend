package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import org.springframework.core.env.Environment;

@Configuration
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
        // If the profile is not "local", fetch the password from the file
        if (!"local".equals(activeProfile)) {
            if (mongoPasswordFilePath != null && !mongoPasswordFilePath.isEmpty()) {
                try {
                    mongoPassword = new String(Files.readAllBytes(Paths.get(mongoPasswordFilePath))).trim();
                } catch (NoSuchFileException e) {
                    mongoPassword = environment.getProperty("spring.data.mongodb.password", "");
                }
            }

            if (mongoPassword == null || mongoPassword.isEmpty()) {
                throw new IllegalStateException("MONGODB_PASSWORD environment variable or file is required for non-local profiles.");
            }

            // Replace the password in the URI
            mongoUri = mongoUri.replace(":<password>", ":" + mongoPassword);
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
