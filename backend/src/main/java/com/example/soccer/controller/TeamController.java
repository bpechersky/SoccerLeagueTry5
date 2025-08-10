package com.example.soccer.controller;

import com.example.soccer.model.Team;
import com.example.soccer.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamRepository repo;

    @GetMapping public List<Team> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Team> byId(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping public Team create(@RequestBody Team t) { return repo.save(t); }

    @PutMapping("/{id}")
    public ResponseEntity<Team> update(@PathVariable Long id, @RequestBody Team t) {
        return repo.findById(id).map(existing -> {
            existing.setName(t.getName());
            existing.setCity(t.getCity());
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
