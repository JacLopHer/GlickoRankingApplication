package com.example.GlickoRankingApplication.repository;


import com.example.GlickoRankingApplication.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByName(String name);
}
