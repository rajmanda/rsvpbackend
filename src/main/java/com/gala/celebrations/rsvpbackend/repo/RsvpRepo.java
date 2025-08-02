package com.gala.celebrations.rsvpbackend.repo;

import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RsvpRepo extends ReactiveMongoRepository<Rsvp, String> {
    Mono<Rsvp> findByRsvpDetails_NameAndRsvpDetails_UserEmail(String name, String userEmail);
    Mono<Rsvp> findByRsvpDetails_NameAndRsvpDetails_UserEmailAndRsvpDetails_ForGuest(String name, String userEmail, String forGuest);
    Mono<Void> deleteByRsvpId(String rsvpId);
}
