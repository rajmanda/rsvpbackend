package com.gala.celebrations.rsvpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalaEventDTO {
    private int galaEventId;
    private GalaEventDetails galaEventDetails;
}
