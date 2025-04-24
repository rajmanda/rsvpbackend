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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    @Autowired
    RsvpRepo rsvpRepo;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    EmailSender emailSender;

    public RsvpDTO saveRsvpInDB(String seqName, RsvpDetails rsvpDetails) {
        // Check for an existing record (excluding the RSVP ID)
        //Optional<Rsvp> existingRsvp = rsvpRepo.findByRsvpDetails_NameAndRsvpDetails_UserEmail(rsvpDetails.getName(),rsvpDetails.getUserEmail());
        Optional<Rsvp> existingRsvp = rsvpRepo.findByRsvpDetails_NameAndRsvpDetails_UserEmailAndRsvpDetails_ForGuest(rsvpDetails.getName(),rsvpDetails.getUserEmail(),rsvpDetails.getForGuest());
        if (existingRsvp.isPresent()) {
            // Deactivate the existing entry
            Rsvp oldRsvp = existingRsvp.get();
            //oldRsvp.setActive(false); // Assuming there's an 'active' field
            rsvpRepo.deleteByRsvpId(oldRsvp.getRsvpId()); // Update the existing entry
        }

        int rsvpId = sequenceGeneratorService.getNextSequence(seqName);
        Rsvp rsvpToBeSaved =  new Rsvp(rsvpId, rsvpDetails);
        Rsvp rsvpCreated = rsvpRepo.save(rsvpToBeSaved);
        sendRSVPConfirmationEmail(rsvpCreated.getRsvpDetails());
        return RsvpMapper.INSTANCE.mapRsvpToRsvpDTO(rsvpCreated);
    }


    private void sendRSVPConfirmationEmail(RsvpDetails rsvpDetails) {

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(rsvpDetails.getUserEmail());
        // Consider making the subject more specific too
        emailDetails.setSubject("RSVP Confirmation for " + rsvpDetails.getName());

        // Use String.format to insert the username and other relevant details
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
                rsvpDetails.getDate(),          // Argument for %s (Date: %s)
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
