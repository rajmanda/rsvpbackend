package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.AdminsDTO;
import com.gala.celebrations.rsvpbackend.entity.Admins;
import com.gala.celebrations.rsvpbackend.mapper.AdminsMapper;
import com.gala.celebrations.rsvpbackend.repo.AdminsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AdminsService {

    private final AdminsRepo adminsRepo;

    public Flux<AdminsDTO> getAllAdmins() {
        return adminsRepo.findAll()
                .map(this::convertToDto);
    }

    private AdminsDTO convertToDto(Admins admins) {
        return AdminsMapper.INSTANCE.mapAdminsToAdminsDTO(admins);
    }

    public Mono<Void> deleteAdmins(int adminsId) {
        return adminsRepo.deleteById(adminsId);
    }
    
    public Mono<AdminsDTO> findByEmail(String email) {
        return adminsRepo.findByEmail(email)
                .map(this::convertToDto);
    }
}
