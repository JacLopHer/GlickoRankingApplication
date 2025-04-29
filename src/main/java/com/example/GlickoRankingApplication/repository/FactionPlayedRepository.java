package com.example.GlickoRankingApplication.repository;

import com.example.GlickoRankingApplication.model.FactionPlayed;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FactionPlayedRepository extends MongoRepository<FactionPlayed, String> {
}
