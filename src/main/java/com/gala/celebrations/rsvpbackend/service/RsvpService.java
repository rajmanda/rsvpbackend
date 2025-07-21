package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.EmailDetails;
import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import com.gala.celebrations.rsvpbackend.helper.EmailSender;
import com.gala.celebrations.rsvpbackend.mapper.RsvpMapper;
import com.gala.celebrations.rsvpbackend.repo.RsvpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    private final RsvpRepo rsvpRepo;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final EmailSender emailSender;

    @Autowired
    public RsvpService(RsvpRepo rsvpRepo, SequenceGeneratorService sequenceGeneratorService, EmailSender emailSender) {
        this.rsvpRepo = rsvpRepo;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.emailSender = emailSender;
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
        sendRSVPConfirmationEmail(rsvpSaved.getRsvpDetails());
        return RsvpMapper.INSTANCE.mapRsvpToRsvpDTO(rsvpSaved);
    }


    
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter OUTPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private void sendRSVPConfirmationEmail(RsvpDetails rsvpDetails) {
        String formattedDate;
        if (rsvpDetails.getDate() != null) {
            LocalDateTime dateTime = LocalDateTime.parse(rsvpDetails.getDate(), INPUT_DATE_FORMATTER);
            formattedDate = dateTime.format(OUTPUT_DATE_FORMATTER);
        } else {
            formattedDate = "TBD";
        }

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(rsvpDetails.getUserEmail());
        emailDetails.setSubject("RSVP Confirmation for " + rsvpDetails.getName());

        String body = String.format("""
                  Dear %s,
                  
                  Thank you for confirming your attendance! We’re thrilled you’ll be joining us and look forward to making it a memorable experience at: %s.
                  
                  Date: %s
                  Location: %s
                  
                  We have recorded your response:
                  RSVP Status: %s
                  Adults: %d
                  Children: %d
                  Comments: %s
                
                  We look forward to celebrating with you!
                  
                  Best regards,
                  Raj Manda
                  (On behalf of Vijayram & Bhargavi Manda)
                  https://shravanikalyanam.com/login
                  """,
                rsvpDetails.getUserName(),      // Argument for %s (Dear %s,)
                rsvpDetails.getName(),          // Argument for %s (event: %s)
                formattedDate,                  // Argument for %s (Date: %s)
                rsvpDetails.getLocation(),      // Argument for %s (Location: %s)
                rsvpDetails.getRsvp(),          // Argument for %s (RSVP Status: %s)
                rsvpDetails.getAdults(),        // Argument for %d (Adults: %d)
                rsvpDetails.getChildren(),      // Argument for %d (Children: %d)
                rsvpDetails.getComments() != null ? rsvpDetails.getComments() : "None" // Handle null comments
        );

        emailDetails.setBody(body);
        emailSender.sendSimpleMail(emailDetails);
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
