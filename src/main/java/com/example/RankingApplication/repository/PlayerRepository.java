package com.example.RankingApplication.repository;


import com.example.RankingApplication.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlayerRepository extends MongoRepository<Player, String> {

    Optional<Player> findByName(String name);


}
