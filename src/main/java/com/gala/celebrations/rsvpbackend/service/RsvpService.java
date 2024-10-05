package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import com.gala.celebrations.rsvpbackend.mapper.RsvpMapper;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    @Autowired
    RsvpRepo rsvpRepo;

    @Autowired
    SequenceGenerator sequenceGenerator;

    public RsvpDTO saveRsvpInDB(RsvpDetails rsvpDetails) {
        // Check for an existing record (excluding the RSVP ID)
        Optional<Rsvp> existingRsvp = rsvpRepo.findByRsvpDetails_NameAndRsvpDetails_UserEmail(rsvpDetails.getName(),rsvpDetails.getUserEmail());
        if (existingRsvp.isPresent()) {
            // Deactivate the existing entry
            Rsvp oldRsvp = existingRsvp.get();
            //oldRsvp.setActive(false); // Assuming there's an 'active' field
            rsvpRepo.deleteByRsvpId(oldRsvp.getRsvpId()); // Update the existing entry
        }

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

    public void deleteRsvp(int rsvpId) {
        rsvpRepo.deleteByRsvpId(rsvpId);
    }

    public void deleteAllRsvps() {
        rsvpRepo.deleteAll();
    }
}
