package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.reverse.transitions.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import jakarta.annotation.PreDestroy;

@Configuration
@Profile("test")
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "com.gala.celebrations.rsvpbackend.repo")
@AutoConfigureBefore({
    MongoAutoConfiguration.class,
    MongoReactiveAutoConfiguration.class,
    MongoDataAutoConfiguration.class,
    MongoReactiveDataAutoConfiguration.class,
    EmbeddedMongoAutoConfiguration.class
})
public class TestMongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestMongoConfig.class);
    private static final String DEFAULT_DATABASE_NAME = "testdb";
    private static final int DEFAULT_PORT = 27017;
    
    private Mongod mongod;

    @Bean
    @Primary
    public MongoClient reactiveMongoClient() {
        try {
            // Configure embedded MongoDB
            mongod = Mongod.instance()
                .withNet(Start.to(Net.class).initializedWith(Net.defaults().withPort(DEFAULT_PORT)))
                .withMongodArguments(Start.to(MongodArguments.class).initializedWith(MongodArguments.defaults()))
                .start(Version.Main.V6_0);

            String connectionString = "mongodb://localhost:" + DEFAULT_PORT + "/" + DEFAULT_DATABASE_NAME;
            logger.info("Test MongoDB started on: {}", connectionString);
            
            return MongoClients.create(connectionString);
        } catch (Exception e) {
            logger.error("Failed to start embedded MongoDB", e);
            throw new RuntimeException("Could not start embedded MongoDB", e);
        }
    }

    @Bean
    @Primary
    public ReactiveMongoTemplate reactiveMongoTemplate(@Autowired MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, DEFAULT_DATABASE_NAME);
    }

    @Bean
    @Primary
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext context = new MongoMappingContext();
        context.setAutoIndexCreation(true);
        return context;
    }

    @PreDestroy
    public void cleanup() {
        if (mongod != null) {
            try {
                mongod.stop();
                logger.info("Embedded MongoDB stopped");
            } catch (Exception e) {
                logger.warn("Error stopping embedded MongoDB", e);
            }
        }
    }
}