package com.gala.celebrations.rsvpbackend.controller;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.service.RsvpService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/rsvp")
public class RsvpController {

    private final RsvpService rsvpService;

    public RsvpController(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @PostMapping("/saversvp")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RsvpDTO> saveOrder(@RequestBody RsvpDetails rsvpDetails) {
        return rsvpService.saveRsvpInDB("rsvp", rsvpDetails);
    }

    @GetMapping("/allrsvps")
    public Flux<RsvpDTO> getAllRsvps() {
        return rsvpService.getAllRsvps();
    }

    @DeleteMapping("/{rsvpId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteRsvpById(@PathVariable String rsvpId) {
        return rsvpService.deleteRsvp(rsvpId)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error deleting RSVP with id: " + rsvpId, e)));
    }
}
