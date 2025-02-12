package com.gala.celebrations.rsvpbackend.mapper;

import com.gala.celebrations.rsvpbackend.dto.GalaEventDTO;
import com.gala.celebrations.rsvpbackend.entity.GalaEvent;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GalaEventMapper {

    GalaEventMapper INSTANCE = Mappers.getMapper(GalaEventMapper.class);

    GalaEvent mapGalaEventDTOToGalaEvent(GalaEventDTO galaEventDetails);
    GalaEventDTO mapGalaEventToGalaEventDTO(GalaEvent galaEvent);

}
