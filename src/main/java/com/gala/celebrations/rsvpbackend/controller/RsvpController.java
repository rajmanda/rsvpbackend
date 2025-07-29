package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.service.RsvpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/rsvp")

public class RsvpController {

    @Autowired
    RsvpService rsvpService ;

    @PostMapping("/saversvp")
    public ResponseEntity<RsvpDTO> saveOrder(@RequestBody RsvpDetails rsvpDetails){
        System.out.println(rsvpDetails);
        RsvpDTO rsvpSavedInDB = rsvpService.saveRsvpInDB("rsvp", rsvpDetails);
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
    @DeleteMapping("/{rsvpId}")
    public ResponseEntity<Void> deleteRsvpById(@PathVariable int rsvpId) {
        try {
            rsvpService.deleteRsvp(rsvpId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
