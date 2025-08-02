package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.config.TestSecurityConfig;
import com.gala.celebrations.rsvpbackend.dto.AdminsDTO;
import com.gala.celebrations.rsvpbackend.repo.AdminsRepo;
import com.gala.celebrations.rsvpbackend.service.AdminsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@WebFluxTest(AdminsController.class)
class AdminsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AdminsService adminsService;

    @Test
    void getAllAdmins_ShouldReturnAllAdmins() {
        // Arrange
        AdminsDTO admin1 = new AdminsDTO(1, "admin1@example.com");
        AdminsDTO admin2 = new AdminsDTO(2, "admin2@example.com");
        
        List<AdminsDTO> admins = Arrays.asList(admin1, admin2);

        Mockito.when(adminsService.getAllAdmins())
                .thenReturn(Flux.fromIterable(admins));

        // Act & Assert
        webTestClient.get()
                .uri("/admins/alladmins")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AdminsDTO.class)
                .hasSize(2)
                .contains(admin1, admin2);
    }

    @Test
    void getAllAdmins_WhenNoAdmins_ShouldReturnEmptyList() {
        // Arrange
        Mockito.when(adminsService.getAllAdmins())
                .thenReturn(Flux.empty());

        // Act & Assert
        webTestClient.get()
                .uri("/admins/alladmins")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AdminsDTO.class)
                .hasSize(0);
    }
}
