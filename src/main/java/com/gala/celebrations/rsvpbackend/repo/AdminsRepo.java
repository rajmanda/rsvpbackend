package com.gala.celebrations.rsvpbackend.repo;

import com.gala.celebrations.rsvpbackend.entity.Admins;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AdminsRepo extends ReactiveMongoRepository<Admins, Integer> {
    Mono<Admins> findByEmail(String email);
}
