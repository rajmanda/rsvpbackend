package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.service.RsvpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rsvp")

public class RsvpController {

    @Autowired
    RsvpService rsvpService ;

    @PostMapping("/saversvp")
    public ResponseEntity<RsvpDTO> saveOrder(@RequestBody RsvpDetails rsvpDetails){
        RsvpDTO rsvpSavedInDB = rsvpService.saveRsvpInDB(rsvpDetails);
        return new ResponseEntity<>(rsvpSavedInDB, HttpStatus.CREATED);
    }

    @GetMapping("/allrsvps")
    public ResponseEntity<List<RsvpDTO>> getAllRsvps() {
        List<RsvpDTO> rsvps = rsvpService.getAllRsvps();
        return new ResponseEntity<>(rsvps, HttpStatus.OK);
    }

    @DeleteMapping("/deleteallrsvps")
    public ResponseEntity<Void> deleteAllRsvps() {
        rsvpService.deleteAllRsvps();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
