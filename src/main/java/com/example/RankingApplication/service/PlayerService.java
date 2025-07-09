package com.example.RankingApplication.service;

import com.example.RankingApplication.client.BCPClient;
import com.example.RankingApplication.dto.PlayerDTO;
import com.example.RankingApplication.dto.bcp.PlayerPlayer;
import com.example.RankingApplication.enums.Faction;
import com.example.RankingApplication.exceptions.PlayerInstantiationException;
import com.example.RankingApplication.model.FactionPlayed;
import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.model.PlayerClassResolver;
import com.example.RankingApplication.repository.PlayerRepositoryCustom;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class PlayerService {

    private PlayerRepositoryCustom<Player> playerRepository; // debe soportar operaciones genéricas con clase

    private BCPClient bcpClient;

    public List<PlayerDTO> getAllPlayers(int gameSystemCode) {
        Class<? extends Player> clazz = PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode);
        if (clazz == null) {
            throw new IllegalArgumentException("Unsupported game system code: " + gameSystemCode);
        }

        List<Player> players = safeCastPlayers(playerRepository.findAll(clazz));
        // mapear a DTO igual que antes
        return players.stream()
                .map(player -> new PlayerDTO(
                        player.getId(),
                        player.getName(),
                        player.getRating(),
                        player.getFactionsPlayed() != null ? getMostPlayedFaction(player.getFactionsPlayed()).getDisplayName() : null,
                        player.getMatchCount(),
                        player.getMatchesWon()))
                .toList();
    }

    public Faction getMostPlayedFaction(Map<Faction, FactionPlayed> factionsPlayed) {
        Faction mostPlayedFaction = null;
        int maxMatches = 0;

        for (Map.Entry<Faction, FactionPlayed> entry : factionsPlayed.entrySet()) {
            Faction faction = entry.getKey();
            int matchesAmount = entry.getValue().getMatchesAmount();  // Asegúrate de tener un getter para matchesAmount en FactionPlayed

            if (matchesAmount > maxMatches) {
                maxMatches = matchesAmount;
                mostPlayedFaction = faction;
            }
        }

        return mostPlayedFaction;
    }

    public void applyDecayToAllPlayers(int gameSystemCode) {
        log.info("Starting to apply decay to all players");
        List<Player> players = safeCastPlayers(playerRepository.findAll(PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode)));
        for (Player player : players) {
            if (shouldDecay(player)) {
                double oldRating = player.getRating();
                double decayedRating = oldRating * 0.99; // Aplica 1% de decay, por ejemplo
                player.setRating(decayedRating);
            }
        }
        playerRepository.saveAll(players);
    }

    public void createPlayersFromBCP(String eventId) {
        var event = bcpClient.getEvent(eventId);
        int gameSystemCode = event.gameSystem().code();

        Class<? extends Player> clazz = PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode);
        if (clazz == null) {
            throw new IllegalArgumentException("Unsupported game system code: " + gameSystemCode);
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException("Cannot instantiate abstract player class: " + clazz.getName());
        }

        log.info("Creando jugadores para el sistema de juego: {}, clase: {}", gameSystemCode, clazz.getName());

        List<PlayerPlayer> playersJson = bcpClient.getPlayers(eventId);

        List<PlayerPlayer> newPlayers = playersJson.stream()
                .filter(p -> !playerRepository.existsById(p.getUser().getId(), clazz))
                .toList();

        List<Player> toSave = newPlayers.stream()
                .map(p -> {
                    try {
                        Player player = createEmptyPlayerInstance(clazz);
                        String fullName = p.getUser().getFirstName() + " " + p.getUser().getLastName();
                        player.setId(p.getUser().getId());
                        player.setName(fullName);
                        return player;
                    } catch (Exception e) {
                        log.error("Error creando jugador para " + p.getUser().getFirstName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        playerRepository.saveAll(toSave);
    }

    private Player createEmptyPlayerInstance(Class<? extends Player> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new PlayerInstantiationException("Failed to instantiate player class: " + clazz.getName(), e);
        }
    }

    // Otros métodos adaptados para usar el código también:

    public void removeAllPlayers(int gameSystemCode) {
        Class<? extends Player> clazz = PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode);
        if (clazz == null) {
            throw new IllegalArgumentException("Unsupported game system code: " + gameSystemCode);
        }
        playerRepository.deleteAll(clazz);
    }

    public void removePlayerById(String playerId, int gameSystemCode) {
        Class<? extends Player> clazz = PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode);
        if (clazz == null) {
            throw new IllegalArgumentException("Unsupported game system code: " + gameSystemCode);
        }
        playerRepository.deleteById(playerId, clazz);
    }

    // Aplica decay a jugadores de un sistema específico
    public void applyDecayToPlayers(int gameSystemCode) {
        Class<? extends Player> clazz = PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode);
        if (clazz == null) {
            throw new IllegalArgumentException("Unsupported game system code: " + gameSystemCode);
        }

        List<Player> players = safeCastPlayers(playerRepository.findAll(clazz));
        for (Player player : players) {
            if (shouldDecay(player)) {
                double oldRating = player.getRating();
                double decayedRating = oldRating * 0.99;
                player.setRating(decayedRating);
            }
        }
        playerRepository.saveAll(players);
    }

    private boolean shouldDecay(Player player) {
        return player.getLastMatchDate().isBefore(LocalDateTime.now().minusDays(30));
    }

    private List<Player> safeCastPlayers(List<? extends Player> players){
        return players.stream().filter(Objects::nonNull)
                .map(Player.class::cast)
                .toList();
    }
}