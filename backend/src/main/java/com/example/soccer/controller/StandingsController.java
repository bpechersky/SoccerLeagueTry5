package com.example.soccer.controller;

import com.example.soccer.dto.StandingRow;
import com.example.soccer.service.StandingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/standings")
@RequiredArgsConstructor
public class StandingsController {
    private final StandingsService standingsService;

    @GetMapping
    public List<StandingRow> getStandings() {
        return standingsService.computeStandings();
    }
}
