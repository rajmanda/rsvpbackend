package com.gala.celebrations.rsvpbackend.repo;

import com.gala.celebrations.rsvpbackend.entity.GalaEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GalaEventRepo extends ReactiveMongoRepository<GalaEvent, Integer> {
    Mono<GalaEvent> findByGalaEventDetails_Name(String name);
    Mono<GalaEvent> findByGalaEventId(int galaEventId);
    Mono<Void> deleteByGalaEventId(int galaEventId);
    Flux<GalaEvent> findByActiveTrue();
}
