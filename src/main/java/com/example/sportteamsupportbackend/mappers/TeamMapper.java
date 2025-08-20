package com.example.sportteamsupportbackend.mappers;

import com.example.sportteamsupportbackend.model.Team;
import com.example.sportteamsupportbackend.model.TeamApiModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamMapper INSTANCE = Mappers.getMapper(TeamMapper.class);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget Team team, TeamApiModel teamApiModel);
    Team toEntity(TeamApiModel teamApiModel);
    List<TeamApiModel> toApiModelList(List<Team> team);
    TeamApiModel toApiModel(Team team);
}