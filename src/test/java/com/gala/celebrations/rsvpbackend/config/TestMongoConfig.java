package com.gala.celebrations.rsvpbackend.config;

import com.mongodb.ConnectionString;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class TestMongoConfig {

    @Bean
    public ReactiveMongoDatabaseFactory mongoDatabaseFactory() {
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017/test");
        return new SimpleReactiveMongoDatabaseFactory(connectionString);
    }

    @Bean
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter() {
        MappingMongoConverter converter = new MappingMongoConverter(
            NoOpDbRefResolver.INSTANCE, 
            mongoMappingContext()
        );
        converter.afterPropertiesSet();
        return converter;
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoDatabaseFactory(), mappingMongoConverter());
    }
}