package com.example.GlickoRankingApplication.repository;

import com.example.GlickoRankingApplication.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
