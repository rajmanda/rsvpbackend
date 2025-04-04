package com.gala.celebrations.rsvpbackend.repo;

import com.gala.celebrations.rsvpbackend.entity.GalaEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GalaEventRepo extends MongoRepository<GalaEvent, Integer> {
    Optional<GalaEvent> findByGalaEventDetails_Name(String name);
    // Delete operation by GalaEventId

    Optional<GalaEvent> findByGalaEventId(int galaEventId);
    void deleteByGalaEventId(int GalaEventId);

    List<GalaEvent> findByActiveTrue(); // Spring Data MongoDB will auto-implement this!
}
