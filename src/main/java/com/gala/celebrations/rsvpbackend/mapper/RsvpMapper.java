package com.gala.celebrations.rsvpbackend.mapper;


import com.gala.celebrations.rsvpbackend.dto.RsvpDTO;
import com.gala.celebrations.rsvpbackend.entity.Rsvp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RsvpMapper {

    RsvpMapper INSTANCE = Mappers.getMapper(RsvpMapper.class);

    Rsvp mapRsvpDTOToRsvp(RsvpDTO rsvpDetails);
    RsvpDTO mapRsvpToRsvpDTO(Rsvp rsvp);

}