package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import com.gala.celebrations.rsvpbackend.entity.GalaEvent;
import com.gala.celebrations.rsvpbackend.mapper.GalaEventMapper;
import com.gala.celebrations.rsvpbackend.repo.GalaEventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GalaEventService {

    private final GalaEventRepo galaEventRepo;
    private final SequenceGeneratorService sequenceGeneratorService;

    public Mono<GalaEventDTO> saveGalaEventInDB(String seqName, GalaEventDetails galaEventDetails) {
        return sequenceGeneratorService.getNextSequence(seqName)
                .flatMap(galaEventId -> {
                    GalaEvent galaEventToBeSaved = new GalaEvent(galaEventId, galaEventDetails);
                    return galaEventRepo.save(galaEventToBeSaved)
                            .map(GalaEventMapper.INSTANCE::mapGalaEventToGalaEventDTO);
                });
    }

    public Flux<GalaEventDTO> getAllGalaEvents() {
        return galaEventRepo.findByActiveTrue()
                .map(this::convertToDto);
    }

    public Mono<GalaEventDTO> getGalaEventById(int galaEventId) {
        return galaEventRepo.findByGalaEventId(galaEventId)
                .map(this::convertToDto);
    }

    public Mono<GalaEventDTO> getGalaEventByName(String name) {
        return galaEventRepo.findByGalaEventDetails_Name(name)
                .map(this::convertToDto);
    }

    public Mono<Void> deleteGalaEvent(int galaEventId) {
        return galaEventRepo.deleteByGalaEventId(galaEventId);
    }

    public Mono<GalaEventDTO> updateGalaEvent(int galaEventId, GalaEventDetails updatedDetails) {
        return galaEventRepo.findByGalaEventId(galaEventId)
                .flatMap(existingGalaEvent -> {
                    existingGalaEvent.setGalaEventDetails(updatedDetails);
                    return galaEventRepo.save(existingGalaEvent)
                            .map(GalaEventMapper.INSTANCE::mapGalaEventToGalaEventDTO);
                });
    }

    public Mono<Void> deleteById(int galaEventId) {
        return galaEventRepo.findByGalaEventId(galaEventId)
                .flatMap(existingGalaEvent -> {
                    existingGalaEvent.setActive(false);
                    return galaEventRepo.save(existingGalaEvent)
                            .then();
                });
    }

    private GalaEventDTO convertToDto(GalaEvent galaEvent) {
        return GalaEventMapper.INSTANCE.mapGalaEventToGalaEventDTO(galaEvent);
    }
}
