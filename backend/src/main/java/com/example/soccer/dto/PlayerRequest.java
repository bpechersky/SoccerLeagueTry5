package com.example.soccer.dto;

public record PlayerRequest(
        String name,
        String position,
        Long teamId   // nullable = no team
) { }
