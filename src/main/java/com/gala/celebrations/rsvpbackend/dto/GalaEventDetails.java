package com.gala.celebrations.rsvpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GalaEventDetails {
    private String name;
    private String date;
    private String location;
    private String image;
    private String description;
}
