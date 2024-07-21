package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDTOFromFE;
import com.gala.celebrations.rsvpbackend.service.RsvpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rsvp")

public class RsvpController {

    @Autowired
    RsvpService rsvpService ;

    @PostMapping("/saveOrder")
    public ResponseEntity<RsvpDTO> saveOrder(@RequestBody RsvpDTOFromFE rsvpDetails){
        RsvpDTO rsvpSavedInDB = rsvpService.saveRsvpInDB(rsvpDetails);
        return new ResponseEntity<>(rsvpSavedInDB, HttpStatus.CREATED);
    }

}
