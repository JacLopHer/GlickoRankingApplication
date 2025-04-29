package com.example.GlickoRankingApplication.repository;

import com.example.GlickoRankingApplication.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchRepository extends MongoRepository<Match, String> {
}
