package com.example.soccer.controller;

import com.example.soccer.dto.PlayerRequest;
import com.example.soccer.model.Player;
import com.example.soccer.model.Team;
import com.example.soccer.repo.PlayerRepository;
import com.example.soccer.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerRepository repo;
    private final TeamRepository teamRepo;

    @GetMapping
    public List<Player> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> byId(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/team/{teamId}")
    public List<Player> byTeam(@PathVariable Long teamId) {
        return repo.findByTeamId(teamId);
    }

    @PostMapping
    public ResponseEntity<Player> create(@RequestBody Player p) {
        if (p.getTeam() != null && p.getTeam().getId() != null) {
            p.setTeam(teamRepo.findById(p.getTeam().getId()).orElse(null));
        } else {
            p.setTeam(null);
        }
        return ResponseEntity.ok(repo.save(p));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PlayerRequest req) {
        return repo.findById(id).map(existing -> {
            existing.setName(req.name());
            existing.setPosition(req.position());

            Team team = null;
            if (req.teamId() != null) {
                team = teamRepo.findById(req.teamId()).orElse(null);
                if (team == null) {
                    return ResponseEntity.badRequest().body("Unknown teamId: " + req.teamId());
                }
            }
            existing.setTeam(team);

            Player saved = repo.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
