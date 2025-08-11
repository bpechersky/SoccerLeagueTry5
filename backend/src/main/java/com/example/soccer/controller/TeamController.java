package com.example.soccer.controller;

import com.example.soccer.model.Player;
import com.example.soccer.model.Team;
import com.example.soccer.repo.MatchRepository;
import com.example.soccer.repo.PlayerRepository;
import com.example.soccer.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final MatchRepository matchRepo;

    @GetMapping public List<Team> all() { return teamRepo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Team> byId(@PathVariable Long id) {
        return teamRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping public Team create(@RequestBody Team t) { return teamRepo.save(t); }

    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable Long id, @RequestBody Team t) {
        return teamRepo.findById(id).map(existing -> {
            existing.setName(t.getName());
            existing.setCity(t.getCity());
            return ResponseEntity.ok(teamRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!teamRepo.existsById(id)) return ResponseEntity.notFound().build();

        // 1) Detach players
        for (Player p : playerRepo.findByTeamId(id)) {
            p.setTeam(null);
        }

        // 2) Remove matches referencing this team (home or away)
        matchRepo.deleteAllByHomeTeamIdOrAwayTeamId(id, id);

        // 3) Delete the team
        teamRepo.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
