package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import com.gala.celebrations.rsvpbackend.entity.GalaEvent;
import com.gala.celebrations.rsvpbackend.mapper.GalaEventMapper;
import com.gala.celebrations.rsvpbackend.repo.GalaEventRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalaEventService {

    @Autowired
    GalaEventRepo GalaEventRepo;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;

    public GalaEventDTO saveGalaEventInDB(String seqName, GalaEventDetails GalaEventDetails) {

        int GalaEventId = sequenceGeneratorService.getNextSequence(seqName);
        GalaEvent GalaEventToBeSaved =  new GalaEvent(GalaEventId, GalaEventDetails);
        GalaEvent GalaEventCreated = GalaEventRepo.save(GalaEventToBeSaved);
        return GalaEventMapper.INSTANCE.mapGalaEventToGalaEventDTO(GalaEventCreated);
    }

    public List<GalaEventDTO> getAllGalaEvents() {
        List<GalaEvent> GalaEvents = GalaEventRepo.findAll();
        return GalaEvents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private GalaEventDTO convertToDto(GalaEvent GalaEvent) {
        return GalaEventMapper.INSTANCE.mapGalaEventToGalaEventDTO(GalaEvent);
    }

    public void deleteGalaEvent(int GalaEventId) {
        GalaEventRepo.deleteByGalaEventId(GalaEventId);
    }

    public void deleteAllGalaEvents() {
        GalaEventRepo.deleteAll();
    }
}
