package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.AdminsDTO;
import com.gala.celebrations.rsvpbackend.entity.Admins;
import com.gala.celebrations.rsvpbackend.mapper.AdminsMapper;
import com.gala.celebrations.rsvpbackend.repo.AdminsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminsService {

    @Autowired
    AdminsRepo adminsRepo;

    public List<AdminsDTO> getAllAdminss() {
        List<Admins> admins = adminsRepo.findAll();
        return admins.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AdminsDTO convertToDto(Admins admins) {
        return AdminsMapper.INSTANCE.mapAdminsToAdminsDTO(admins);
    }

    public void deleteAdmins(int adminsId) {
        adminsRepo.deleteById(adminsId);
    }

    // public void deleteAllAdminss() {
    //     adminsRepo.deleteAll();
    // }
}
