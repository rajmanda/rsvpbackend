package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import org.springframework.core.env.Environment;

@Configuration
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "com.gala.celebrations.rsvpbackend.repo")
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
                mongoPassword = Files.readString(Paths.get(mongoPasswordFilePath)).trim();
                mongoUri = mongoUri.replace("${MONGO_PASSWORD}", mongoPassword);
                logger.info("MongoDB URI configured with password from file");
            } catch (NoSuchFileException e) {
                logger.warn("MongoDB password file not found at {}", mongoPasswordFilePath);
                logger.info("Using default MongoDB URI configuration");
            }
        } else {
            logger.info("Using local MongoDB configuration");
        }
    }

    @Bean
    public MongoClient reactiveMongoClient() {
        logger.info("Connecting to MongoDB with URI: {}", maskSensitiveInfo(mongoUri));
        return MongoClients.create(mongoUri);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), databaseName);
    }

    private String maskSensitiveInfo(String connectionString) {
        if (connectionString == null) {
            return "";
        }
        return connectionString.replaceAll("(?<=:)[^@]*(?=@)", "****");
    }
}
