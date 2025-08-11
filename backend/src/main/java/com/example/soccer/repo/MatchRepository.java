package com.example.soccer.repo;
import com.example.soccer.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MatchRepository extends JpaRepository<Match, Long> {
    void deleteAllByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);
}