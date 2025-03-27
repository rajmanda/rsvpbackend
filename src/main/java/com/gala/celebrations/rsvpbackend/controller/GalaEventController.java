package com.gala.celebrations.rsvpbackend.controller;


import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import com.gala.celebrations.rsvpbackend.service.GalaEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/gala-event")

public class GalaEventController {

    @Autowired
    GalaEventService galaEventService ;

    @PostMapping("/save-gala-event")
    public ResponseEntity<GalaEventDTO> saveOrder(@RequestBody GalaEventDetails galaEventDetails){
        System.out.println(galaEventDetails);
        GalaEventDTO galaEventSavedInDB = galaEventService.saveGalaEventInDB("galaEvent", galaEventDetails);
        return new ResponseEntity<>(galaEventSavedInDB, HttpStatus.CREATED);
    }

    @GetMapping("/all-gala-events")
    public ResponseEntity<List<GalaEventDTO>> getAllGalaEvents() {
        List<GalaEventDTO> galaEvents = galaEventService.getAllGalaEvents();
        return new ResponseEntity<>(galaEvents, HttpStatus.OK);
    }

    // @DeleteMapping("/delete-all-gala-Events")
    // public ResponseEntity<Void> deleteAllgalaEvents() {
    //     //galaEventService.deleteAllGalaEvents();
    //     return new ResponseEntity<>(HttpStatus.OK);
    // }

    @DeleteMapping("/delete-gala-event/{id}")
    public ResponseEntity<Void> deleteGalaEventById(@PathVariable int id) {
        try {
            galaEventService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Log the exception (optional)
            System.out.println("Error deleting gala event with id: " + id);
            System.out.println("Error deleting gala event with id: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    // Update an existing GalaEvent
    @PutMapping("/update-gala-event/{id}")
    public ResponseEntity<GalaEventDTO> updateGalaEvent(
            @PathVariable int id,
            @RequestBody GalaEventDetails updatedDetails) {
        try {
            GalaEventDTO updatedGalaEvent = galaEventService.updateGalaEvent(id, updatedDetails);
            return new ResponseEntity<>(updatedGalaEvent, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Log the exception (optional)
            System.out.println("Error updating gala event with id: " + id);
            System.out.println("Error message: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
