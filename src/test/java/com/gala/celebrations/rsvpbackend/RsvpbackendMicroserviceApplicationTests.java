package com.gala.celebrations.rsvpbackend;

import com.gala.celebrations.rsvpbackend.config.BaseIntegrationTest;
import com.gala.celebrations.rsvpbackend.repo.AdminsRepo;
import com.gala.celebrations.rsvpbackend.repo.GalaEventRepo;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
class RsvpbackendMicroserviceApplicationTests extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AdminsRepo adminsRepo;

    @MockBean
    private GalaEventRepo galaEventRepo;

    @MockBean
    private RsvpRepo rsvpRepo;

    @Test
    void contextLoads() {
        // This test will verify that the application context loads successfully
    }

    @Test
    void shouldReturnOkStatus() {
        webTestClient
            .get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk();
    }
}
