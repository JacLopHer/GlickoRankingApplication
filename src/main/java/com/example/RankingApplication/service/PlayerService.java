package com.example.RankingApplication.service;

import com.example.RankingApplication.client.BCPClient;
import com.example.RankingApplication.dto.PlayerDTO;
import com.example.RankingApplication.dto.bcp.PlayerPlayer;
import com.example.RankingApplication.enums.Faction;
import com.example.RankingApplication.model.FactionPlayed;
import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PlayerService {
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private BCPClient bcpClient;


    public List<PlayerDTO> getAllPlayers() {
        List<Player> players = playerRepository.findAll();
        List<PlayerDTO> playerDTOS = players.stream()
                .map(player -> new PlayerDTO(player.getId(),player.getName(), player.getRating(), player.getFactionsPlayed() != null ? getMostPlayedFaction(player.getFactionsPlayed()).getDisplayName() : null, player.getMatchCount())).toList();
        return playerDTOS;
    }

    public Faction getMostPlayedFaction(HashMap<Faction, FactionPlayed> factionsPlayed) {
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

    public void createPlayersFromBCP(String eventId) {
        // Recuperar los jugadores desde BCPClient
        List<PlayerPlayer> playersJson = bcpClient.getPlayers(eventId);

        // Filtrar jugadores que ya existen en la base de datos
        playersJson = playersJson.stream()
                .filter(player -> !playerRepository.existsById(player.getUser().getId()))
                .toList();

        List<Player> toSave = playersJson.stream()
                .map(p -> {
                    // Extraer el nombre completo y asignarlo al jugador
                    String fullName = p.getUser().getFirstName() + " " + p.getUser().getLastName();
                    Player player = new Player(fullName);
                    player.setId(p.getUser().getId()); // Usamos userId como id
                    return player;
                })
                .toList();

        log.info("Starting batch player creation from BCP : {}", toSave.size());

        // Guardar los jugadores en la base de datos
        for (Player p : toSave) {
            log.info("Saving : {}", p.getName());
            playerRepository.save(p);
        }
    }

    private boolean shouldDecay(Player player) {
        return player.getLastMatchDate().isBefore(LocalDateTime.now().minusDays(30));
    }

    public void removeAllPlayers(){
        playerRepository.deleteAll();
    }

    public void removePlayerById(String playerId) {playerRepository.deleteById(playerId);}
}