package com.example.soccer.repo;

import com.example.soccer.model.Match;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Override
    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    List<Match> findAll();

    @EntityGraph(attributePaths = {"homeTeam", "awayTeam"})
    @Query("""
    select m from Match m
    where (:teamId is null or m.homeTeam.id = :teamId or m.awayTeam.id = :teamId)
      and (:homeTeamId is null or m.homeTeam.id = :homeTeamId)
      and (:awayTeamId is null or m.awayTeam.id = :awayTeamId)
      and (:fromDate is null or m.matchDate >= :fromDate)
      and (:toDate   is null or m.matchDate <= :toDate)
    order by m.matchDate desc
  """)
    List<Match> search(
            @Param("teamId") Long teamId,
            @Param("homeTeamId") Long homeTeamId,
            @Param("awayTeamId") Long awayTeamId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    // Use nested property traversal with underscores
    void deleteAllByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);
}
