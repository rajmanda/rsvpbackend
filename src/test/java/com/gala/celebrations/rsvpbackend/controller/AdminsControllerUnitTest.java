package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.AdminsDTO;
import com.gala.celebrations.rsvpbackend.service.AdminsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

class AdminsControllerUnitTest {

    @Mock
    private AdminsService adminsService;

    private AdminsController adminsController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminsController = new AdminsController(adminsService);
    }

    @Test
    void getAllAdmins_ShouldReturnAllAdmins() {
        // Arrange
        AdminsDTO admin1 = new AdminsDTO(1, "admin1@example.com");
        AdminsDTO admin2 = new AdminsDTO(2, "admin2@example.com");
        List<AdminsDTO> admins = Arrays.asList(admin1, admin2);

        when(adminsService.getAllAdmins()).thenReturn(Flux.fromIterable(admins));

        // Act
        Flux<AdminsDTO> result = adminsController.getAllAdmins();

        // Assert
        StepVerifier.create(result)
                .expectNext(admin1)
                .expectNext(admin2)
                .verifyComplete();
    }

    @Test
    void getAllAdmins_WhenNoAdmins_ShouldReturnEmptyFlux() {
        // Arrange
        when(adminsService.getAllAdmins()).thenReturn(Flux.empty());

        // Act
        Flux<AdminsDTO> result = adminsController.getAllAdmins();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}