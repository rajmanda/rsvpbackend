package com.gala.celebrations.rsvpbackend.entity;

import com.gala.celebrations.rsvpbackend.dto.GalaEventDetails;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "galaEvent")
public class GalaEvent extends Auditable {
    @Id
    private int galaEventId;
    private GalaEventDetails galaEventDetails;
}
