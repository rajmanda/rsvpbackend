package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDTOFromFE;
import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import com.gala.celebrations.rsvpbackend.mapper.RsvpMapper;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RsvpService {

    @Autowired
    RsvpRepo rsvpRepo;

    @Autowired
    SequenceGenerator sequenceGenerator;

    public RsvpDTO saveRsvpInDB(RsvpDTOFromFE rsvpDetails) {

        int rsvpId = sequenceGenerator.generateNextRsvpId();
        Rsvp rsvpToBeSaved =  new Rsvp(rsvpId, rsvpDetails);
        Rsvp rsvpCreated = rsvpRepo.save(rsvpToBeSaved);
        return RsvpMapper.INSTANCE.mapRsvpToRsvpDTO(rsvpCreated);
    }
}
