package com.gala.celebrations.rsvpbackend.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestMongoConfig.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    
    @MockBean
    protected ReactiveMongoTemplate reactiveMongoTemplate;
    
    // Base configuration for integration tests with mocked MongoDB
}