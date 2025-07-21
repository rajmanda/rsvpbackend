package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.EmailDetails;
import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import com.gala.celebrations.rsvpbackend.mapper.RsvpMapper;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    private final RsvpRepo rsvpRepo;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    public RsvpService(RsvpRepo rsvpRepo, SequenceGeneratorService sequenceGeneratorService) {
        this.rsvpRepo = rsvpRepo;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    public RsvpDTO saveRsvpInDB(String seqName, RsvpDetails rsvpDetails) {
        Optional<Rsvp> existingRsvpOpt = rsvpRepo.findByRsvpDetails_NameAndRsvpDetails_UserEmailAndRsvpDetails_ForGuest(
                rsvpDetails.getName(), rsvpDetails.getUserEmail(), rsvpDetails.getForGuest());

        Rsvp rsvpToSave;
        if (existingRsvpOpt.isPresent()) {
            // Update the existing RSVP record
            rsvpToSave = existingRsvpOpt.get();
            rsvpToSave.setRsvpDetails(rsvpDetails); // Update the details
        } else {
            // Create a new RSVP record
            int rsvpId = sequenceGeneratorService.getNextSequence(seqName);
            rsvpToSave = new Rsvp(rsvpId, rsvpDetails);
        }

        Rsvp rsvpSaved = rsvpRepo.save(rsvpToSave);
        return RsvpMapper.INSTANCE.mapRsvpToRsvpDTO(rsvpSaved);
    }


    private static final java.text.SimpleDateFormat DATE_FORMATTER = new java.text.SimpleDateFormat("MMMM dd, yyyy");


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
