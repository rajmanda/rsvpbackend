package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.reactivestreams.client.MongoClient;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@TestConfiguration
@Profile("test")
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "com.gala.celebrations.rsvpbackend.repo")
public class TestMongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestMongoConfig.class);

    @Bean
    @Primary
    public MongoClient reactiveMongoClient() {
        logger.info("Creating mock MongoClient for tests");
        return Mockito.mock(MongoClient.class);
    }

    @Bean
    @Primary
    public MongoMappingContext mongoMappingContext() {
        logger.info("Creating MongoMappingContext for tests");
        MongoMappingContext context = new MongoMappingContext();
        context.setAutoIndexCreation(false);
        return context;
    }
}