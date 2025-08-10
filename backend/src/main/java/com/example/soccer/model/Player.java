package com.example.soccer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "player")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference           // prevents infinite Team->Players->Team recursion
    private Team team;
}
