package com.example.GlickoRankingApplication.service;

import com.example.GlickoRankingApplication.dto.CreatePlayerRequest;
import com.example.GlickoRankingApplication.dto.PlayerJson;
import com.example.GlickoRankingApplication.exceptions.PlayerNotFoundException;
import com.example.GlickoRankingApplication.model.Player;
import com.example.GlickoRankingApplication.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> createPlayers(List<CreatePlayerRequest> request) {
        log.info("Starting batch player creation : {}", (long) request.size());
        List<Player> toSave = request.stream()
                .map(p -> new Player(p.name()))
                .toList();
        for (Player p : toSave) {
            log.info("Saving : {}", p.getName());
            playerRepository.save(p);
        }
        return toSave;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayerByName(String name) {
        log.info("Attempting to find : {}", name);
        return playerRepository.findByName(name).orElseThrow(() -> new PlayerNotFoundException(name));
    }

    public void applyDecayToAllPlayers() {
        log.info("Starting to apply decay to all players");
        List<Player> players = playerRepository.findAll();
        for (Player player : players) {
            if (shouldDecay(player)) {
                double oldRating = player.getRating();
                double decayedRating = oldRating * 0.99; // Aplica 1% de decay, por ejemplo
                player.setRating(decayedRating);
            }
        }
        playerRepository.saveAll(players);
    }

    public List<Player> createPlayersFromJson(List<PlayerJson> playersJson) {
        playersJson = playersJson.stream().filter(player -> !playerRepository.existsById(player.getUserId())).toList();

        List<Player> toSave = playersJson.stream()
                .map(p -> {
                    // Extraemos el nombre completo y lo asignamos al nombre del jugador
                    String fullName = p.getUser().getFirstName() + " " + p.getUser().getLastName();
                    Player player = new Player(fullName);
                    player.setId(p.getUserId()); // Usamos userId como id
                    return player;
                })
                .toList();
        log.info("Starting batch player creation from JSON : {}", (long) toSave.size());
        for (Player p : toSave) {

            log.info("Saving : {}", p.getName());
            playerRepository.save(p);
        }
        return toSave;
    }

    private boolean shouldDecay(Player player) {
        return player.getLastMatchDate().isBefore(LocalDateTime.now().minusDays(30));
    }
}