package com.example.RankingApplication.service;

import com.example.RankingApplication.client.BCPClient;
import com.example.RankingApplication.dto.MatchDTO;
import com.example.RankingApplication.dto.bcp.PlayerPairing;
import com.example.RankingApplication.dto.glicko.MatchResult;
import com.example.RankingApplication.enums.Faction;
import com.example.RankingApplication.exceptions.PlayerNotFoundException;
import com.example.RankingApplication.model.FactionPlayed;
import com.example.RankingApplication.model.Match;
import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.repository.FactionPlayedRepository;
import com.example.RankingApplication.repository.MatchRepository;
import com.example.RankingApplication.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MatchService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private GlickoRatingService glickoRatingService;

    @Autowired
    private FactionPlayedRepository factionPlayedRepository;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BCPClient bcpClient;


    public List<MatchDTO> getMatchesByPlayerId(String playerId) {
        List<Match> matches = matchRepository.findByPlayerAIdOrPlayerBId(playerId, playerId);

        return matches.stream()
                .map(match -> toDTO(match, playerId))
                .toList();
    }

    private Faction getFactionFromDisplay(String displayName){
        for(Faction faction: Faction.values()){
            if(faction.getDisplayName().equalsIgnoreCase(displayName)){
                return faction;
            }
        }
        throw new IllegalArgumentException("Faction not valid : " + displayName);
    }

    private Player updateFactionPlayed(Player player, Faction faction, int numberOfRounds) {
        if (player.getFactionsPlayed() == null) {
            player.setFactionsPlayed(new HashMap<>());
        }
        FactionPlayed factionPlayed = player.getFactionsPlayed().get(faction);
        if (factionPlayed == null) {
            factionPlayed = new FactionPlayed(player.getId(), faction, numberOfRounds);
            player.getFactionsPlayed().put(faction, factionPlayed);
        } else {
            factionPlayed.setMatchesAmount(factionPlayed.getMatchesAmount() + numberOfRounds);
        }

        return player;
    }


    @Transactional
    public void bulkMatches(String eventId) {
        List<String> failedMatches = new ArrayList<>();
        log.info("Starting to get matches for event : {}", eventId);
        int numberOfRounds = bcpClient.getNumberOfRounds(eventId);
        int numberOfMatches = 0;

        playerService.createPlayersFromBCP(eventId);

        List<Match> matchesToSave = new ArrayList<>();
        Map<Player, List<MatchResult>> matchResultsMap = new HashMap<>();

        for (int i = 1; i <= numberOfRounds; i++) {
            List<MatchDTO> matchDTOS = bcpClient.getPairings(eventId, i);
            for (MatchDTO matchDTO : matchDTOS) {
                // Buscar jugadores de la base de datos
                Player playerA = playerRepository.findById(matchDTO.getPlayer1().getUser().getId())
                        .orElseThrow(() -> new PlayerNotFoundException(matchDTO.getPlayer1().getUser().getId() + " not found"));
                Player playerB = playerRepository.findById(matchDTO.getPlayer2().getUser().getId())
                        .orElseThrow(() -> new PlayerNotFoundException(matchDTO.getPlayer2().getUser().getId() + " not found"));

                // Crear el objeto Match
                Match match = Match.builder()
                        .playerA(playerA)
                        .playerB(playerB)
                        .result(matchDTO.getPlayer1Game().getResult())
                        .date(LocalDateTime.now())
                        .playerAFaction(matchDTO.getPlayer1().getFaction())
                        .playerBFaction(matchDTO.getPlayer2().getFaction())
                        .build();
                matchesToSave.add(match);

                Faction faction1 = getFactionFromDisplay(matchDTO.getPlayer1().getFaction());
                Faction faction2 = getFactionFromDisplay(matchDTO.getPlayer2().getFaction());

                // Crear MatchResult para cada jugador
                MatchResult resultA = new MatchResult(playerA, matchDTO.getPlayer1Game().getResult(),faction1);
                MatchResult resultB = new MatchResult(playerB, matchDTO.getPlayer2Game().getResult(),faction2);

                // Acumular los resultados para cada jugador
                matchResultsMap.computeIfAbsent(playerA, k -> new ArrayList<>()).add(resultA);
                matchResultsMap.computeIfAbsent(playerB, k -> new ArrayList<>()).add(resultB);

                numberOfMatches++;
            }
        }

        // Guardar todas las partidas al final del proceso
        if (!matchesToSave.isEmpty()) {
            matchRepository.saveAll(matchesToSave);
        }

        // Procesar los jugadores y sus resultados de partidos en batch
        for (Map.Entry<Player, List<MatchResult>> entry : matchResultsMap.entrySet()) {
            Player player = entry.getKey();
            List<MatchResult> matchResults = entry.getValue();
            player.setMatchCount(player.getMatchCount() + matchResults.size());
            player = updateFactionPlayed(player, matchResults.get(0).getFaction(), matchResults.size());
            player = glickoRatingService.updateRatingsBulk(player, matchResults);
            playerRepository.save(player);
        }

        log.info("All matches processed successfully: {} matches and {} rounds", numberOfMatches, numberOfRounds);
    }

    public void deleteAllMatches (){
        log.info("Deleting all matches");
        matchRepository.deleteAll();
        log.info("Deleted all matches");
    }


    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }
    @Transactional
    public void saveMatch(Match match) { matchRepository.save(match); }



    private MatchDTO toDTO(Match match, String playerId) {
        Player playerA = match.getPlayerA();
        Player playerB = match.getPlayerB();

        boolean isPlayerA = playerA.getId().equals(playerId);

        PlayerPairing player1 = toPlayerPairing(isPlayerA ? playerA : playerB);
        PlayerPairing player2 = toPlayerPairing(isPlayerA ? playerB : playerA);

        MatchDTO.Player1Game player1Game = new MatchDTO.Player1Game();
        MatchDTO.Player1Game player2Game = new MatchDTO.Player1Game();

        double result = match.getResult(); // 1.0 (A win), 0.0 (B win), 0.5 (draw)

        if (result == 0.5) {
            player1Game.setResult(0);
            player2Game.setResult(0);
        } else if ((result == 1.0 && isPlayerA) || (result == 0.0 && !isPlayerA)) {
            player1Game.setResult(1); // win
            player2Game.setResult(-1); // loss
        } else {
            player1Game.setResult(-1); // loss
            player2Game.setResult(1); // win
        }

        // Aquí puedes mapear los puntos si los tienes. Por ahora los dejamos en 0.
        player1Game.setPoints(0);
        player2Game.setPoints(0);

        MatchDTO dto = new MatchDTO();
        dto.setPlayer1(player1);
        dto.setPlayer2(player2);
        dto.setPlayer1Game(player1Game);
        dto.setPlayer2Game(player2Game);
        return dto;
    }

    private PlayerPairing toPlayerPairing(Player player) {
        PlayerPairing pairing = new PlayerPairing();
        PlayerPairing.User user = new PlayerPairing.User();

        user.setId(player.getId());
        user.setFirstName(player.getName()); // Asumimos que solo hay un campo de nombre
        user.setLastName(""); // Si tienes un apellido, agrégalo aquí

        pairing.setUser(user);
        pairing.setFaction(
                player.getFactionsPlayed() != null && !player.getFactionsPlayed().isEmpty()
                        ? player.getFactionsPlayed().keySet().iterator().next().getDisplayName()
                        : "Unknown"
        );

        return pairing;
    }
}
