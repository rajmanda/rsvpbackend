package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

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
                // Use the logger
                logger.debug("Reading MongoDB password from file path: {}", mongoPasswordFilePath);
                mongoPassword = new String(Files.readAllBytes(Paths.get(mongoPasswordFilePath))).trim();
                logger.debug("MongoDB password loaded successfully from file.");
            } catch (NoSuchFileException e) {
                logger.warn("MongoDB password file not found at '{}'. Falling back to environment variable.", mongoPasswordFilePath);
                mongoPassword = environment.getProperty("MONGODB_PASSWORD", ""); // Use a more specific env var
            }
            // ...
            mongoUri = mongoUri.replace("<password>", mongoPassword); // Note: I corrected the placeholder from :<password> to <password> to match the URI
            logger.info("MongoDB URI has been dynamically configured.");
        } else {
            logger.debug("Active profile is 'local', skipping dynamic password fetch for MongoDB.");
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
