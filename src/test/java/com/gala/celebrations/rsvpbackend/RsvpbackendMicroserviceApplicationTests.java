package com.gala.celebrations.rsvpbackend;

import com.gala.celebrations.rsvpbackend.config.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
class RsvpbackendMicroserviceApplicationTests extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

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
