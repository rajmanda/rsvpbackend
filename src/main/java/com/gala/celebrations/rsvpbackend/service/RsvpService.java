package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.EmailDetails;
import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import com.gala.celebrations.rsvpbackend.helper.EmailSender;
import com.gala.celebrations.rsvpbackend.mapper.RsvpMapper;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RsvpService {

    private final RsvpRepo rsvpRepo;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final EmailSender emailSender;
    private final TemplateEngine templateEngine;

    public Mono<RsvpDTO> saveRsvpInDB(String seqName, RsvpDetails rsvpDetails) {
        return sequenceGeneratorService.getNextSequence(seqName)
            .flatMap(sequence -> {
                Rsvp newRsvp = new Rsvp();
                newRsvp.setRsvpId("RSVP" + sequence);
                newRsvp.setRsvpDetails(rsvpDetails);
                newRsvp.setCreatedAt(LocalDateTime.now());
                newRsvp.setLastUpdated(LocalDateTime.now());
                
                return rsvpRepo.save(newRsvp)
                    .flatMap(savedRsvp -> {
                        // Send email asynchronously
                        return sendRsvpEmail(savedRsvp.getRsvpDetails(), "rsvp-email-template")
                            .thenReturn(RsvpMapper.INSTANCE.mapRsvpToRsvpDTO(savedRsvp));
                    });
            });
    }

    public Mono<RsvpDTO> updateRsvp(Rsvp rsvp) {
        return rsvpRepo.findById(rsvp.getRsvpId())
            .flatMap(existingRsvp -> {
                existingRsvp.setRsvpDetails(rsvp.getRsvpDetails());
                existingRsvp.setLastUpdated(LocalDateTime.now());
                return rsvpRepo.save(existingRsvp)
                    .map(RsvpMapper.INSTANCE::mapRsvpToRsvpDTO);
            });
    }

    public Flux<RsvpDTO> getAllRsvps() {
        return rsvpRepo.findAll()
                .doOnNext(rsvp -> log.debug("Found RSVP: {}", rsvp.getRsvpId()))
                .map(RsvpMapper.INSTANCE::mapRsvpToRsvpDTO);
    }

    public Mono<RsvpDTO> getRsvpById(String rsvpId) {
        return rsvpRepo.findById(rsvpId)
                .map(RsvpMapper.INSTANCE::mapRsvpToRsvpDTO);
    }

    public Mono<Void> deleteRsvp(String rsvpId) {
        return rsvpRepo.deleteById(rsvpId);
    }

    private Mono<Void> sendRsvpEmail(RsvpDetails rsvpDetails, String templateName) {
        try {
            Context context = new Context();
            context.setVariable("rsvp", rsvpDetails);
            
            String emailContent = templateEngine.process(templateName, context);
            String subject = "RSVP " + ("true".equals(rsvpDetails.getRsvp()) ? "Confirmation" : "Regrets") + " - " + rsvpDetails.getName();
            
            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setRecipient(rsvpDetails.getUserEmail());
            emailDetails.setSubject(subject);
            emailDetails.setBody(emailContent);
            emailDetails.setAttachment(null);
            
            return emailSender.sendSimpleMail(emailDetails)
                    .doOnError(e -> log.error("Error sending email: {}", e.getMessage()));
        } catch (Exception e) {
            log.error("Error in sendRsvpEmail: {}", e.getMessage(), e);
            return Mono.empty();
        }
    }
}
