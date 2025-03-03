package com.gala.celebrations.rsvpbackend.mapper;


import com.gala.celebrations.rsvpbackend.dto.AdminsDTO;
import com.gala.celebrations.rsvpbackend.entity.Admins;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminsMapper {

    RsvpMapper INSTANCE = Mappers.getMapper(AdminsMapper.class);

    Rsvp mapAdminsDTOToAdmins(AdminsDTO adminsDto);
    RsvpDTO mapAdminsToAdminsDTO(Admins admins);

}
