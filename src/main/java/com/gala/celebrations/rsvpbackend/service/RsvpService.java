package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import com.gala.celebrations.rsvpbackend.mapper.RsvpMapper;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    @Autowired
    RsvpRepo rsvpRepo;

    @Autowired
    SequenceGenerator sequenceGenerator;

    public RsvpDTO saveRsvpInDB(RsvpDetails rsvpDetails) {

        int rsvpId = sequenceGenerator.generateNextRsvpId();
        Rsvp rsvpToBeSaved =  new Rsvp(rsvpId, rsvpDetails);
        Rsvp rsvpCreated = rsvpRepo.save(rsvpToBeSaved);
        return RsvpMapper.INSTANCE.mapRsvpToRsvpDTO(rsvpCreated);
    }

    public List<RsvpDTO> getAllRsvps() {
        List<Rsvp> rsvps = rsvpRepo.findAll();
        return rsvps.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RsvpDTO convertToDto(Rsvp rsvp) {
        return RsvpMapper.INSTANCE.mapRsvpToRsvpDTO(rsvp);
    }
}
