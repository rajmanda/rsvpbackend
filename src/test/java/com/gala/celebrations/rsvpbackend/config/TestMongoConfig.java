package com.gala.celebrations.rsvpbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
@EnableAutoConfiguration(exclude = {
    MongoAutoConfiguration.class,
    MongoReactiveAutoConfiguration.class,
    MongoDataAutoConfiguration.class,
    MongoReactiveDataAutoConfiguration.class
})
public class TestMongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestMongoConfig.class);

    public TestMongoConfig() {
        logger.info("TestMongoConfig initialized - MongoDB auto-configuration disabled for tests");
    }
}