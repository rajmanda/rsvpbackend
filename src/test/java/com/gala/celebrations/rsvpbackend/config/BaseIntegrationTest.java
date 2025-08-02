package com.gala.celebrations.rsvpbackend.config;

import com.gala.celebrations.rsvpbackend.repo.AdminsRepo;
import com.gala.celebrations.rsvpbackend.repo.GalaEventRepo;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, 
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration"
    })
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