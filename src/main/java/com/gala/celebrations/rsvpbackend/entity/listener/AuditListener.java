package com.gala.celebrations.rsvpbackend.entity.listener;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.gala.celebrations.rsvpbackend.entity.Auditable;

import java.time.LocalDateTime;

@Component
public class AuditListener extends AbstractMongoEventListener<Auditable> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Auditable> event) {
        Auditable auditable = event.getSource();
        auditable.setLastUpdatedDate(LocalDateTime.now());
        // Set the lastUpdatedBy field based on your authentication mechanism
        auditable.setLastUpdatedBy("system"); // Replace with actual user
        if (auditable.getActive() == null) {
            auditable.setActive(true); // Default to active if not set
        }
    }
}
