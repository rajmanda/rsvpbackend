package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.config.TestSecurityConfig;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import com.gala.celebrations.rsvpbackend.repo.GalaEventRepo;
import com.gala.celebrations.rsvpbackend.service.GalaEventService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@WebFluxTest(controllers = GalaEventController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class GalaEventControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GalaEventService galaEventService;

    @MockBean
    private GalaEventRepo galaEventRepo;

    @Test
    void saveGalaEvent_ShouldReturnCreated() {
        GalaEventDetails details = new GalaEventDetails();
        GalaEventDTO savedEvent = new GalaEventDTO(1, details);

        Mockito.when(galaEventService.saveGalaEventInDB(anyString(), any(GalaEventDetails.class)))
                .thenReturn(Mono.just(savedEvent));

        webTestClient.post()
                .uri("/gala-event/save-gala-event")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(details)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.galaEventId").isEqualTo(1);
    }

    @Test
    void getAllGalaEvents_ShouldReturnAllEvents() {
        GalaEventDetails details1 = new GalaEventDetails();
        GalaEventDetails details2 = new GalaEventDetails();
        GalaEventDTO event1 = new GalaEventDTO(1, details1);
        GalaEventDTO event2 = new GalaEventDTO(2, details2);
        List<GalaEventDTO> events = Arrays.asList(event1, event2);

        Mockito.when(galaEventService.getAllGalaEvents())
                .thenReturn(Flux.fromIterable(events));

        webTestClient.get()
                .uri("/gala-event/all-gala-events")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(GalaEventDTO.class)
                .hasSize(2)
                .contains(event1, event2);
    }

    @Test
    void deleteGalaEvent_ShouldReturnNoContent() {
        int eventId = 1;
        Mockito.when(galaEventService.deleteGalaEvent(eventId))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/gala-event/delete-gala-event/{id}", eventId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateGalaEvent_ShouldReturnUpdatedEvent() {
        int eventId = 1;
        GalaEventDetails updatedDetails = new GalaEventDetails();
        GalaEventDTO updatedEvent = new GalaEventDTO(eventId, updatedDetails);

        Mockito.when(galaEventService.updateGalaEvent(anyInt(), any(GalaEventDetails.class)))
                .thenReturn(Mono.just(updatedEvent));

        webTestClient.put()
                .uri("/gala-event/update-gala-event/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedDetails)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.galaEventId").isEqualTo(eventId);
    }
}
