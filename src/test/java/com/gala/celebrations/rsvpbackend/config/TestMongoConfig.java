package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.reverse.TransitionWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
@Profile("test")
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages = "com.gala.celebrations.rsvpbackend.repo")
@AutoConfigureBefore({
    MongoAutoConfiguration.class,
    MongoReactiveAutoConfiguration.class,
    MongoDataAutoConfiguration.class,
    MongoReactiveDataAutoConfiguration.class
})
public class TestMongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(TestMongoConfig.class);
    private static final String DEFAULT_DATABASE_NAME = "testdb";
    private static final int DEFAULT_PORT = 27017;
    
    private TransitionWalker.ReachedState<RunningMongodProcess> mongodProcess;

    @PostConstruct
    public void startEmbeddedMongo() {
        try {
            // Start embedded MongoDB with specified version
            Mongod mongod = Mongod.instance();
            mongodProcess = mongod.start(Version.Main.V6_0);

            logger.info("Test embedded MongoDB started on default port (typically 27017)");
        } catch (Exception e) {
            logger.error("Failed to start embedded MongoDB", e);
            throw new RuntimeException("Could not start embedded MongoDB", e);
        }
    }

    @Bean
    @Primary
    public MongoClient reactiveMongoClient() {
        String connectionString = "mongodb://localhost:" + DEFAULT_PORT + "/" + DEFAULT_DATABASE_NAME;
        logger.info("Creating MongoClient with connection string: {}", connectionString);
        return MongoClients.create(connectionString);
    }

    @Bean
    @Primary
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), DEFAULT_DATABASE_NAME);
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
        if (mongodProcess != null) {
            try {
                mongodProcess.close();
                logger.info("Embedded MongoDB stopped");
            } catch (Exception e) {
                logger.warn("Error stopping embedded MongoDB", e);
            }
        }
    }
}