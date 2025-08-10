package com.example.soccer.repo;
import com.example.soccer.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TeamRepository extends JpaRepository<Team, Long> { }