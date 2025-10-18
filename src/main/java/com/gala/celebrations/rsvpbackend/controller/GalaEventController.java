package com.gala.celebrations.rsvpbackend.controller;


import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import com.gala.celebrations.rsvpbackend.service.GalaEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gala-event")
public class GalaEventController {

    private static final Logger logger = LoggerFactory.getLogger(GalaEventController.class);

    @Autowired
    GalaEventService galaEventService;

    @PostMapping("/save-gala-event")
    public ResponseEntity<GalaEventDTO> saveOrder(@RequestBody GalaEventDetails galaEventDetails) {
        logger.info("Received request to save gala event: {}", galaEventDetails.getName());
        GalaEventDTO galaEventSavedInDB = galaEventService.saveGalaEventInDB("galaEvent", galaEventDetails);
        return new ResponseEntity<>(galaEventSavedInDB, HttpStatus.CREATED);
    }

    @GetMapping("/all-gala-events")
    public ResponseEntity<List<GalaEventDTO>> getAllGalaEvents() {
        List<GalaEventDTO> galaEvents = galaEventService.getAllGalaEvents();
        return new ResponseEntity<>(galaEvents, HttpStatus.OK);
    }

    @DeleteMapping("/delete-gala-event/{id}")
    public ResponseEntity<Void> deleteGalaEventById(@PathVariable int id) {
        try {
            // Corrected method call from deleteById to deactivateGalaEventById
            galaEventService.deactivateGalaEventById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            logger.error("Error deactivating gala event with id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update-gala-event/{id}")
    public ResponseEntity<GalaEventDTO> updateGalaEvent(
            @PathVariable int id,
            @RequestBody GalaEventDetails updatedDetails) {
        try {
            GalaEventDTO updatedGalaEvent = galaEventService.updateGalaEvent(id, updatedDetails);
            return new ResponseEntity<>(updatedGalaEvent, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error updating gala event with id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}