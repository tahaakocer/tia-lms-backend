package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.TeamDto;
import com.tia.lms_backend.model.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    Team dtoToEntity(TeamDto teamDto);

    TeamDto entityToDto(Team savedTeam);
}