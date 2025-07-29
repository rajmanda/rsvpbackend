package com.gala.celebrations.rsvpbackend.repo;

import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// Update the ID type in the generic signature to String
public interface RsvpRepo extends MongoRepository<Rsvp, String> {
    Optional<Rsvp> findByRsvpDetails_NameAndRsvpDetails_UserEmail(String name, String userEmail);
    Optional<Rsvp> findByRsvpDetails_NameAndRsvpDetails_UserEmailAndRsvpDetails_ForGuest(String name, String userEmail, String forGuest);

    // Update the parameter type here as well
    void deleteByRsvpId(String rsvpId);
}

