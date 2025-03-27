package com.gala.celebrations.rsvpbackend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.mapping.Document;

import com.gala.celebrations.rsvpbackend.entity.listener.AuditListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor  
@Document
@MappedSuperclass
@EntityListeners(AuditListener.class)
public abstract class Auditable {

    @LastModifiedDate
    private LocalDateTime lastUpdatedDate;

    @LastModifiedBy
    private String lastUpdatedBy;

    private Boolean active;
}
