package com.gala.celebrations.rsvpbackend.entity;

import com.gala.celebrations.rsvpbackend.dto.RsvpDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rsvp {
    @Id
    private String rsvpId;
    private RsvpDetails rsvpDetails;

}
