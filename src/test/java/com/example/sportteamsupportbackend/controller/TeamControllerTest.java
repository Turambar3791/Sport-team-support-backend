package com.example.sportteamsupportbackend.controller;

import com.example.sportteamsupportbackend.mappers.TeamMapper;
import com.example.sportteamsupportbackend.model.Team;
import com.example.sportteamsupportbackend.model.TeamApiModel;
import com.example.sportteamsupportbackend.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TeamControllerTest {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamController teamController;
    @Autowired
    private TeamMapper teamMapper;

    @BeforeEach
    void setUp() {
        teamRepository.deleteAll();
        teamRepository.flush();

        Team team1 = new Team(
                "Drużyna A",
                "Słoneczna",
                "120",
                "45-897",
                "Warszawa",
                123456798,
                12346578912L,
                "faktura"
        );

        Team team2 = new Team(
                "Drużyna B",
                "Kwiatowa",
                "78",
                "56-951",
                "Poznań",
                987654321,
                98765432198L,
                "faktura2"
        );

        List<Team> teamList = Arrays.asList(team1, team2);
        teamRepository.saveAll(teamList);
        teamRepository.flush();
    }

    @Test
    void canGetAllTeams() {
        ResponseEntity<List<TeamApiModel>> response = teamController.teamsGet();
        List<TeamApiModel> teamApiModelList = response.getBody();

        List<Team> teamList = teamRepository.findAll();
        List<TeamApiModel> expectedTeamApiModel = teamMapper.toApiModelList(teamList);

        assertEquals(expectedTeamApiModel, teamApiModelList);
    }

    @Test
    void getTeamById() {
        Long teamId = 1L;
        Optional<Team> team = teamRepository.findById(teamId);

        assertNotNull(team.orElse(null));

        ResponseEntity<TeamApiModel> response = teamController.getTeamById(teamId.intValue());
        TeamApiModel teamApiModel = response.getBody();
        TeamApiModel expectedTeamApiModel = teamMapper.toApiModel(team.get());

        assertEquals(expectedTeamApiModel, teamApiModel);
    }

    @Test
    public void teamsPost() {
        Team team = new Team(
                3L,
                "Drużyna C",
                "Leśna",
                "78",
                "79-596",
                "Lizbona",
                120056798,
                12344578912L,
                "faktura"
        );

        TeamApiModel teamApiModel = teamMapper.toApiModel(team);

        ResponseEntity<Void> response = teamController.teamsPost(teamApiModel);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Team> savedTeam = teamRepository.findById(team.getId());
        assertNotNull(savedTeam.orElse(null));
        assertEquals(savedTeam.get().getName(), team.getName());
    }

    @Test
    void teamsIdPut() {
        Long teamId = 1L;
        Team team = teamRepository.findById(teamId).orElse(null);
        assertNotNull(team);
        Team updatedTeam = new Team(
                "Drużyna A updated",
                "Słoneczna updated",
                "121",
                "45-898",
                "Paryż",
                123456799,
                12346578913L,
                "faktura updated"
        );
        updatedTeam.setId(teamId);
        TeamApiModel updatedTeamApiModel = teamMapper.toApiModel(updatedTeam);

        ResponseEntity<TeamApiModel> response = teamController.teamsIdPut(teamId.intValue(), updatedTeamApiModel);
        TeamApiModel result = response.getBody();

        assertEquals(result, updatedTeamApiModel);

        Team savedTeam = teamRepository.findById(teamId).orElse(null);
        assertNotNull(savedTeam);
        assertEquals(savedTeam.getName(), "Drużyna A updated");
    }

    @Test
    void teamsIdDelete() {
        Long teamId = 1L;
        Team team = teamRepository.findById(teamId).orElse(null);
        assertNotNull(team);

        ResponseEntity<Void> response = teamController.teamsIdDelete(teamId.intValue(), teamMapper.toApiModel(team));

        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        assertEquals(teamRepository.findById(teamId), Optional.empty());
    }
}