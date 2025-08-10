package com.example.soccer.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// When a Team appears as home/away, reuse the same identity instead of expanding deeply
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Match {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    private Integer homeScore;
    private Integer awayScore;
    private LocalDate matchDate;
}
