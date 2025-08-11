package com.example.soccer.controller;

import com.example.soccer.model.Match;
import com.example.soccer.repo.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchRepository matchRepo;

    // GET /api/matches?teamId=1  (returns matches where team 1 is home OR away)
    // Optional filters: homeTeamId, awayTeamId, from, to
    @GetMapping
    public List<Match> all(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long homeTeamId,
            @RequestParam(required = false) Long awayTeamId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return matchRepo.search(teamId, homeTeamId, awayTeamId, from, to);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> byId(@PathVariable Long id) {
        return matchRepo.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Match create(@RequestBody Match m) { return matchRepo.save(m); }

    @PutMapping("/{id}")
    public ResponseEntity<Match> update(@PathVariable Long id, @RequestBody Match m) {
        return matchRepo.findById(id).map(ex -> {
            ex.setMatchDate(m.getMatchDate());
            ex.setHomeTeam(m.getHomeTeam());
            ex.setAwayTeam(m.getAwayTeam());
            ex.setHomeScore(m.getHomeScore());
            ex.setAwayScore(m.getAwayScore());
            return ResponseEntity.ok(matchRepo.save(ex));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!matchRepo.existsById(id)) return ResponseEntity.notFound().build();
        matchRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
