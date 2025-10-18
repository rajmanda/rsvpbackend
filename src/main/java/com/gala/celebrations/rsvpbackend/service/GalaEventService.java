package com.gala.celebrations.rsvpbackend.service;

import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import com.gala.celebrations.rsvpbackend.entity.GalaEvent;
import com.gala.celebrations.rsvpbackend.mapper.GalaEventMapper;
import com.gala.celebrations.rsvpbackend.repo.GalaEventRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GalaEventService {

    private static final Logger logger = LoggerFactory.getLogger(GalaEventService.class);

    @Autowired
    GalaEventRepo galaEventRepo;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private GcsSignedUrlService gcsSignedUrlService;

    @Value("${gcs.bucket-name}")
    private String bucketName;

    public GalaEventDTO saveGalaEventInDB(String seqName, GalaEventDetails galaEventDetails) {
        int galaEventId = sequenceGeneratorService.getNextSequence(seqName);
        GalaEvent galaEventToBeSaved = new GalaEvent(galaEventId, galaEventDetails);
        GalaEvent galaEventCreated = galaEventRepo.save(galaEventToBeSaved);
        return GalaEventMapper.INSTANCE.mapGalaEventToGalaEventDTO(galaEventCreated);
    }

    public List<GalaEventDTO> getAllGalaEvents() {
        List<GalaEvent> galaEvents = galaEventRepo.findAll()
                .stream()
                .filter(event -> Boolean.TRUE.equals(event.getActive())) // Filter for active=true
                .collect(Collectors.toList());

        return galaEvents.stream()
                .map(this::convertToDtoWithSignedUrls)
                .collect(Collectors.toList());
    }

    /**
     * Converts a GalaEvent entity to its DTO and replaces the raw GCS image path
     * with a single, viewable, time-limited signed URL.
     *
     * @param galaEvent The entity from the database.
     * @return The enriched DTO ready for the client.
     */
    private GalaEventDTO convertToDtoWithSignedUrls(GalaEvent galaEvent) {
        GalaEventDTO dto = GalaEventMapper.INSTANCE.mapGalaEventToGalaEventDTO(galaEvent);
        GalaEventDetails details = dto.getGalaEventDetails();

        // Check if the single image string is present and not blank.
        if (details != null && details.getImage() != null && !details.getImage().isBlank()) {
            String gcsUri = details.getImage();
            try {
                String relativePath = parseGcsUri(gcsUri);
                if (relativePath != null) {
                    // Generate a single signed URL for the path.
                    String signedUrl = gcsSignedUrlService.generateSignedReadUrl(relativePath);
                    // Set the single signed URL back to the image field.
                    details.setImage(signedUrl);
                } else {
                    logger.warn("Could not parse GCS URI, leaving original value: {}", gcsUri);
                }
            } catch (Exception e) {
                logger.error("Failed to generate signed URL for path: {}", gcsUri, e);
                // Set to null so the frontend doesn't try to render a broken "gs://" link.
                details.setImage(null);
            }
        }
        return dto;
    }

    private String parseGcsUri(String path) {
        String prefix = String.format("gs://%s/", this.bucketName);
        if (path != null && path.startsWith(prefix)) {
            return path.substring(prefix.length());
        }
        if (path != null && !path.startsWith("gs://")) {
            return path;
        }
        return null;
    }

    /**
     * Performs a soft delete by setting the 'active' flag of a GalaEvent to false.
     * The event remains in the database but will be excluded from general queries.
     *
     * @param galaEventId The ID of the event to deactivate.
     */
    public void deactivateGalaEventById(int galaEventId) {
        GalaEvent existingGalaEvent = galaEventRepo.findByGalaEventId(galaEventId)
                .orElseThrow(() -> new RuntimeException("GalaEvent not found with id: " + galaEventId));

        logger.info("Deactivating GalaEvent with ID {}: {}", galaEventId, existingGalaEvent.getGalaEventDetails().getName());

        existingGalaEvent.setActive(false);
        galaEventRepo.save(existingGalaEvent);
    }

    /**
     * Performs a hard delete of all events. Use with caution.
     */
    public void deleteAllGalaEvents() {
        logger.warn("Performing a hard delete of all GalaEvents.");
        galaEventRepo.deleteAll();
    }

    public GalaEventDTO updateGalaEvent(int galaEventId, GalaEventDetails updatedDetails) {
        GalaEvent existingGalaEvent = galaEventRepo.findByGalaEventId(galaEventId)
                .orElseThrow(() -> new RuntimeException("GalaEvent not found with id: " + galaEventId));

        existingGalaEvent.setGalaEventDetails(updatedDetails);

        GalaEvent updatedGalaEvent = galaEventRepo.save(existingGalaEvent);

        return GalaEventMapper.INSTANCE.mapGalaEventToGalaEventDTO(updatedGalaEvent);
    }
}