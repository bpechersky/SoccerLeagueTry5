package com.example.soccer.controller;

import com.example.soccer.model.Match;
import com.example.soccer.model.Team;
import com.example.soccer.repo.TeamRepository;
import com.example.soccer.test.AbstractSpringTestNG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testng.annotations.Test;

import java.time.LocalDate;

import static org.testng.Assert.*;

public class MatchControllerIT extends AbstractSpringTestNG {

    @Autowired private TestRestTemplate rest;
    @Autowired private TeamRepository teamRepo;

    @Test
    public void createAndFetchMatch_returnsTeams() {
        Team home = teamRepo.save(Team.builder().name("Chelsea").city("London").build());
        Team away = teamRepo.save(Team.builder().name("Man City").city("Manchester").build());

        Match req = Match.builder()
                .homeTeam(Team.builder().id(home.getId()).build())
                .awayTeam(Team.builder().id(away.getId()).build())
                .homeScore(1).awayScore(1)
                .matchDate(LocalDate.now())
                .build();

        ResponseEntity<Match> createResp =
                rest.postForEntity("/api/matches", req, Match.class);

        assertEquals(createResp.getStatusCode(), HttpStatus.OK);
        Match created = createResp.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(created.getHomeTeam().getName(), "Chelsea");
        assertEquals(created.getAwayTeam().getName(), "Man City");

        Match[] all = rest.getForObject("/api/matches", Match[].class);
        assertTrue(all.length >= 1);
    }
}
