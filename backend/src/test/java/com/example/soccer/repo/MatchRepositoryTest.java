package com.example.soccer.repo;

import com.example.soccer.model.Match;
import com.example.soccer.model.Team;
import com.example.soccer.test.AbstractSpringTestNG;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.testng.Assert.*;

public class MatchRepositoryTest extends AbstractSpringTestNG {

    @Autowired private TeamRepository teamRepo;
    @Autowired private MatchRepository matchRepo;

    @Test
    @Transactional
    public void searchReturnsMatchesWithTeamsLoaded() {
        Team arsenal = teamRepo.save(Team.builder().name("Arsenal").city("London").build());
        Team liverpool = teamRepo.save(Team.builder().name("Liverpool").city("Liverpool").build());

        matchRepo.save(Match.builder()
                .homeTeam(arsenal).awayTeam(liverpool)
                .homeScore(2).awayScore(1)
                .matchDate(LocalDate.of(2025, 8, 1))
                .build());

        List<Match> list = matchRepo.search(null, null, null, null, null);

        assertTrue(list.stream().anyMatch(m ->
                m.getHomeTeam().getId().equals(arsenal.getId()) &&
                        m.getAwayTeam().getId().equals(liverpool.getId())
        ));
    }


}
