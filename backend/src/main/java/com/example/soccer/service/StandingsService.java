package com.example.soccer.service;

import com.example.soccer.dto.StandingRow;
import com.example.soccer.model.Match;
import com.example.soccer.model.Team;
import com.example.soccer.repo.MatchRepository;
import com.example.soccer.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandingsService {
    private final TeamRepository teamRepo;
    private final MatchRepository matchRepo;

    public List<StandingRow> computeStandings() {
        Map<Long, StandingRow> table = new HashMap<>();

        for (Team t : teamRepo.findAll()) {
            table.put(t.getId(), StandingRow.builder()
                    .teamId(t.getId())
                    .teamName(t.getName())
                    .played(0).wins(0).draws(0).losses(0)
                    .goalsFor(0).goalsAgainst(0).goalDiff(0).points(0)
                    .build());
        }

        for (Match m : matchRepo.findAll()) {
            if (m.getHomeTeam() == null || m.getAwayTeam() == null ||
                m.getHomeScore() == null || m.getAwayScore() == null) continue;

            StandingRow home = table.get(m.getHomeTeam().getId());
            StandingRow away = table.get(m.getAwayTeam().getId());

            home.setPlayed(home.getPlayed()+1);
            away.setPlayed(away.getPlayed()+1);

            home.setGoalsFor(home.getGoalsFor()+m.getHomeScore());
            home.setGoalsAgainst(home.getGoalsAgainst()+m.getAwayScore());
            away.setGoalsFor(away.getGoalsFor()+m.getAwayScore());
            away.setGoalsAgainst(away.getGoalsAgainst()+m.getHomeScore());

            if (m.getHomeScore() > m.getAwayScore()) {
                home.setWins(home.getWins()+1);
                away.setLosses(away.getLosses()+1);
                home.setPoints(home.getPoints()+3);
            } else if (m.getHomeScore() < m.getAwayScore()) {
                away.setWins(away.getWins()+1);
                home.setLosses(home.getLosses()+1);
                away.setPoints(away.getPoints()+3);
            } else {
                home.setDraws(home.getDraws()+1);
                away.setDraws(away.getDraws()+1);
                home.setPoints(home.getPoints()+1);
                away.setPoints(away.getPoints()+1);
            }
        }

        table.values().forEach(r -> r.setGoalDiff(r.getGoalsFor()-r.getGoalsAgainst()));
        return table.values().stream()
                .sorted(Comparator.comparingInt(StandingRow::getPoints).reversed()
                        .thenComparingInt(StandingRow::getGoalDiff).reversed()
                        .thenComparingInt(StandingRow::getGoalsFor).reversed()
                        .thenComparing(StandingRow::getTeamName))
                .collect(Collectors.toList());
    }
}
