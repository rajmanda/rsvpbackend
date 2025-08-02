package com.gala.celebrations.rsvpbackend.config;

import com.gala.celebrations.rsvpbackend.repo.AdminsRepo;
import com.gala.celebrations.rsvpbackend.repo.GalaEventRepo;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@Import({TestMongoConfig.class, TestSecurityConfig.class})
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    
    // Mock all MongoDB repositories to avoid repository initialization issues
    @MockBean
    protected AdminsRepo adminsRepo;
    
    @MockBean
    protected GalaEventRepo galaEventRepo;
    
    @MockBean
    protected RsvpRepo rsvpRepo;
    
    // Base configuration for integration tests with mocked MongoDB
}