package com.example.soccer.controller;

import com.example.soccer.model.Match;
import com.example.soccer.model.Team;
import com.example.soccer.repo.MatchRepository;
import com.example.soccer.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {
    private final MatchRepository repo;
    private final TeamRepository teamRepo;

    @GetMapping public List<Match> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Match> byId(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Match> create(@RequestBody Match m) {
        if (m.getHomeTeam() != null && m.getHomeTeam().getId() != null) {
            m.setHomeTeam(teamRepo.findById(m.getHomeTeam().getId()).orElse(null));
        }
        if (m.getAwayTeam() != null && m.getAwayTeam().getId() != null) {
            m.setAwayTeam(teamRepo.findById(m.getAwayTeam().getId()).orElse(null));
        }
        return ResponseEntity.ok(repo.save(m));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> update(@PathVariable Long id, @RequestBody Match m) {
        return repo.findById(id).map(existing -> {
            if (m.getHomeTeam() != null && m.getHomeTeam().getId() != null) {
                existing.setHomeTeam(teamRepo.findById(m.getHomeTeam().getId()).orElse(null));
            } else {
                existing.setHomeTeam(null);
            }
            if (m.getAwayTeam() != null && m.getAwayTeam().getId() != null) {
                existing.setAwayTeam(teamRepo.findById(m.getAwayTeam().getId()).orElse(null));
            } else {
                existing.setAwayTeam(null);
            }
            existing.setHomeScore(m.getHomeScore());
            existing.setAwayScore(m.getAwayScore());
            existing.setMatchDate(m.getMatchDate());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
