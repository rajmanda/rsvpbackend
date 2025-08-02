package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.reactivestreams.client.MongoClient;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
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

    @Bean
    @Primary
    public MongoConverter mongoConverter(MongoMappingContext mappingContext) {
        logger.info("Creating MappingMongoConverter for tests");
        MappingMongoConverter converter = new MappingMongoConverter(Mockito.mock(com.mongodb.reactivestreams.client.MongoDatabase.class), mappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }

    @Bean
    @Primary
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient, MongoConverter mongoConverter) {
        logger.info("Creating mock ReactiveMongoTemplate for tests");
        ReactiveMongoTemplate template = Mockito.mock(ReactiveMongoTemplate.class);
        when(template.getConverter()).thenReturn(mongoConverter);
        return template;
    }
}