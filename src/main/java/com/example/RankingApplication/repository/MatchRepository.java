package com.example.RankingApplication.repository;

import com.example.RankingApplication.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MatchRepository extends MongoRepository<Match, String> {
    List<Match> findByPlayerAIdOrPlayerBId(String playerAId, String playerBId);
}
