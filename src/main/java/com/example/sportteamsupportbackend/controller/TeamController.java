package com.example.sportteamsupportbackend.controller;

import com.example.sportteamsupportbackend.api.TeamsApi;
import com.example.sportteamsupportbackend.mappers.TeamMapper;
import com.example.sportteamsupportbackend.model.Team;
import com.example.sportteamsupportbackend.model.TeamApiModel;
import com.example.sportteamsupportbackend.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.aspectj.runtime.internal.Conversions.longValue;

@RestController
@RequestMapping("/api/sport-team-support-backend/")
public class TeamController implements TeamsApi {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    public TeamController(TeamRepository teamRepository, TeamMapper teamMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    @Override
    public ResponseEntity<List<TeamApiModel>> teamsGet() {
        List<Team> teams = teamRepository.findAll();
        List<TeamApiModel> teamsApi = teamMapper.toApiModelList(teams);
        return new ResponseEntity<>(teamsApi, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TeamApiModel> getTeamById(@Parameter(name = "id",description = "the id of the team to retrieve",required = true,in = ParameterIn.PATH)
                                                    @PathVariable("id") Integer id) {
        Optional<Team> teamById = teamRepository.findById(Long.valueOf(id));
        TeamApiModel teamApi = teamMapper.toApiModel(teamById.get());
        return new ResponseEntity<>(teamApi, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> teamsPost(@Parameter(name = "TeamApiModel",description = "add team",required = true)
                                          @RequestBody @Valid TeamApiModel teamApiModel) {
        var model = teamMapper.toEntity(teamApiModel);
        teamRepository.save(model);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<TeamApiModel> teamsIdPut(@Parameter(name = "id", description = "id of the team to update", required = true, in = ParameterIn.PATH)
                           @PathVariable("id") Integer id,
                           @Parameter(name = "TeamApiModel", description = "", required = true)
                           @Valid @RequestBody TeamApiModel teamApiModel) {
        Long teamId = longValue(id);
        Team updatedTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Team not exist with id:" + id));
        TeamMapper.INSTANCE.update(updatedTeam, teamApiModel);
        Team savedTeam = teamRepository.save(updatedTeam);
        TeamApiModel savedApiModel = teamMapper.toApiModel(savedTeam);
        return new ResponseEntity<>(savedApiModel, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> teamsIdDelete(@Parameter(name = "id",description = "id of team to delete",required = true,in = ParameterIn.PATH)
                            @PathVariable("id") Integer id,
                            @Parameter(name = "TeamApiModel",description = "",required = true)
                            @RequestBody @Valid TeamApiModel teamApiModel) {
        teamRepository.deleteById(longValue(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
