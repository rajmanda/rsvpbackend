package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import com.gala.celebrations.rsvpbackend.service.GalaEventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gala-event")

public class GalaEventController {

    private final GalaEventService galaEventService;

    public GalaEventController(GalaEventService galaEventService) {
        this.galaEventService = galaEventService;
    }

    @PostMapping("/save-gala-event")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GalaEventDTO> saveOrder(@RequestBody GalaEventDetails galaEventDetails){
        return galaEventService.saveGalaEventInDB("galaEvent", galaEventDetails);
    }

    @GetMapping("/all-gala-events")
    public Flux<GalaEventDTO> getAllGalaEvents() {
        return galaEventService.getAllGalaEvents();
    }

    // @DeleteMapping("/delete-all-gala-Events")
    // public ResponseEntity<Void> deleteAllgalaEvents() {
    //     //galaEventService.deleteAllGalaEvents();
    //     return new ResponseEntity<>(HttpStatus.OK);
    // }

    @DeleteMapping("/delete-gala-event/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGalaEventById(@PathVariable int id) {
        return galaEventService.deleteGalaEvent(id)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error deleting gala event with id: " + id, e)));
    }
    // Update an existing GalaEvent
    @PutMapping("/update-gala-event/{id}")
    public Mono<GalaEventDTO> updateGalaEvent(
            @PathVariable int id,
            @RequestBody GalaEventDetails updatedDetails) {
        return galaEventService.updateGalaEvent(id, updatedDetails)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error updating gala event with id: " + id, e)));
    }
}
