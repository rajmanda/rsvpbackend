package com.gala.celebrations.rsvpbackend.entity;

import com.gala.celebrations.rsvpbackend.dto.RsvpDTOFromFE;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rsvp {
    private int rsvpId;
    private RsvpDTOFromFE rsvpDTOFromFE;

}
