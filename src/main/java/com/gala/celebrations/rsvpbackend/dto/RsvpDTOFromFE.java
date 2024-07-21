package com.gala.celebrations.rsvpbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RsvpDTOFromFE {


    // Info of Gala
    private String description ;
    private String id ;
    private String name;
    private String date ;
    private String image;
    private String location;

    //info of User
    private String rsvp ;
    private int adults ;
    private int children ;


}
