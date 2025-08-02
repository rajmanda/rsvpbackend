package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.config.BaseIntegrationTest;
import com.gala.celebrations.rsvpbackend.config.TestSecurityConfig;
import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.service.RsvpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


@AutoConfigureWebTestClient
@Import(TestSecurityConfig.class)
class RsvpControllerTest extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext context;

    @MockBean
    private RsvpService rsvpService;

    @BeforeEach
    void setUp() {
        // Print all beans in the application context for debugging
        System.out.println("Beans in test context:");
        Arrays.stream(context.getBeanDefinitionNames())
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void saveRsvp_ShouldReturnCreated() {
        RsvpDetails details = new RsvpDetails();
        RsvpDTO savedRsvp = new RsvpDTO("RSVP1", details);

        Mockito.when(rsvpService.saveRsvpInDB(anyString(), any(RsvpDetails.class)))
                .thenReturn(Mono.just(savedRsvp));

        webTestClient.post()
                .uri("/rsvp/saversvp")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(details)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.rsvpId").isEqualTo("RSVP1");
    }

    @Test
    void getAllRsvps_ShouldReturnAllRsvps() {
        RsvpDetails details1 = new RsvpDetails();
        RsvpDetails details2 = new RsvpDetails();
        RsvpDTO rsvp1 = new RsvpDTO("RSVP1", details1);
        RsvpDTO rsvp2 = new RsvpDTO("RSVP2", details2);
        List<RsvpDTO> rsvps = Arrays.asList(rsvp1, rsvp2);

        Mockito.when(rsvpService.getAllRsvps())
                .thenReturn(Flux.fromIterable(rsvps));

        webTestClient.get()
                .uri("/rsvp/allrsvps")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RsvpDTO.class)
                .hasSize(2)
                .contains(rsvp1, rsvp2);
    }

    @Test
    void deleteRsvp_ShouldReturnNoContent() {
        String rsvpId = "RSVP1";
        Mockito.when(rsvpService.deleteRsvp(rsvpId))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/rsvp/{rsvpId}", rsvpId)
                .exchange()
                .expectStatus().isNoContent();
    }
}
