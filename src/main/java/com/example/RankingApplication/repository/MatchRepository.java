package com.example.RankingApplication.repository;

import com.example.RankingApplication.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchRepository extends MongoRepository<Match, String> {
}
