package com.example.GlickoRankingApplication.service;

import com.example.GlickoRankingApplication.dto.CreatePlayersRequest;
import com.example.GlickoRankingApplication.exceptions.PlayerNotFoundException;
import com.example.GlickoRankingApplication.model.Player;
import com.example.GlickoRankingApplication.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> createPlayers(CreatePlayersRequest request) {
        List<Player> toSave = request.players().stream()
                .map(p -> new Player(p.name()))
                .toList();
        for (Player p : toSave) {
            playerRepository.save(p);
        }
        return toSave;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayerByName(String name) {
        return playerRepository.findByName(name).orElseThrow(() -> new PlayerNotFoundException(name));
    }
}