package com.gala.celebrations.rsvpbackend.entity;

import lombok.Data;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
public abstract class Auditable {

    @LastModifiedDate
    private LocalDateTime lastUpdatedDate;

    @LastModifiedBy
    private String lastUpdatedBy;

    private Boolean active = true; // Default to true
}
