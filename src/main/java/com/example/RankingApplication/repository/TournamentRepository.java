package com.example.RankingApplication.repository;

import com.example.RankingApplication.model.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentRepository  extends MongoRepository<Tournament, String> {
}
