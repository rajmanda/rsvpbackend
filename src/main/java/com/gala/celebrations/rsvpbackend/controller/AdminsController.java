package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.AdminsDTO;
import com.gala.celebrations.rsvpbackend.service.AdminsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/admins")

public class AdminsController {

    private final AdminsService adminsService;

    public AdminsController(AdminsService adminsService) {
        this.adminsService = adminsService;
    }

    @GetMapping("/alladmins")
    public Flux<AdminsDTO> getAllAdmins() {
        return adminsService.getAllAdmins();
    }
}
