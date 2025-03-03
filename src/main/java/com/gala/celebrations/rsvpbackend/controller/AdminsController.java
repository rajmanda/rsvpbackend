package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.AdminsDTO;
import com.gala.celebrations.rsvpbackend.dto.Admins;
import com.gala.celebrations.rsvpbackend.service.AdminsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/admins")

public class AdminsController {

    @Autowired
    AdminsService adminsService ;

    @GetMapping("/alladmins")
    public ResponseEntity<List<AdminsDTO>> getAllAdmins() {
        List<AdminsDTO> adminsDto = adminsService.getAllAdminss();
        return new ResponseEntity<>(adminsDto, HttpStatus.OK);
    }
}
