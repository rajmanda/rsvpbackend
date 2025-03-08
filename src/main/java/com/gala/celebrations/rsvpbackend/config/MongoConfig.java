package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import jakarta.annotation.PostConstruct;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Value("${MONGODB_PASSWORD:}") // Default to empty if not set
    private String mongoPassword;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @PostConstruct
    public void init() {
        if (!"local".equals(activeProfile) && (mongoPassword == null || mongoPassword.isEmpty())) {
            throw new IllegalStateException("MONGODB_PASSWORD environment variable is required for non-local profiles.");
        }

        // If the profile is not local, construct the MongoDB URI with the password
        if (!"local".equals(activeProfile)) {
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
