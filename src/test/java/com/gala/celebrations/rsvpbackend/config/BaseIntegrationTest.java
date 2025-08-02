package com.gala.celebrations.rsvpbackend.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestMongoConfig.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // Base configuration for integration tests with mocked MongoDB
}