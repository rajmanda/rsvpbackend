package com.gala.celebrations.rsvpbackend.repo;

import com.gala.celebrations.rsvpbackend.entity.Admins;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminsRepo extends MongoRepository<Admins, Integer> {
    Optional<Admins> findByEmail(String email);
    void deleteByAdminsId(int AdminsId);
}
