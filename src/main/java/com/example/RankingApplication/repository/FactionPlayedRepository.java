package com.example.RankingApplication.repository;

import com.example.RankingApplication.model.FactionPlayed;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FactionPlayedRepository extends MongoRepository<FactionPlayed, String> {
}
