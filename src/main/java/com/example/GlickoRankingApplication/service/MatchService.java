package com.example.GlickoRankingApplication.service;

import com.example.GlickoRankingApplication.dto.MatchDTO;
import com.example.GlickoRankingApplication.enums.Faction;
import com.example.GlickoRankingApplication.exceptions.MatchNotSavedException;
import com.example.GlickoRankingApplication.exceptions.PlayerNotFoundException;
import com.example.GlickoRankingApplication.model.FactionPlayed;
import com.example.GlickoRankingApplication.model.Match;
import com.example.GlickoRankingApplication.model.Player;
import com.example.GlickoRankingApplication.repository.FactionPlayedRepository;
import com.example.GlickoRankingApplication.repository.MatchRepository;
import com.example.GlickoRankingApplication.repository.PlayerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void recordMatch(MatchDTO matchDTO) {
        // Encontrar jugadores en la base de datos
        log.info("Start recording match between : {} vs {}", matchDTO.playerAId(), matchDTO.playerBId());
        Player playerA = playerRepository.findById(matchDTO.playerAId())
                .orElseThrow(() -> new PlayerNotFoundException(matchDTO.playerAId() + "not found"));
        Player playerB = playerRepository.findById(matchDTO.playerBId())
                .orElseThrow(() -> new IllegalArgumentException(matchDTO.playerBId() + "not found"));


        // Actualizar las listas de facciones jugadas para ambos jugadores
        updateFactionPlayed(playerA, getFactionFromDisplay(matchDTO.playerAFaction()));
        updateFactionPlayed(playerB, getFactionFromDisplay(matchDTO.playerBFaction()));

        // Crear el match
        Match match = Match.builder()
                .playerA(playerA)
                .playerB(playerB)
                .result(matchDTO.result())
                .date(LocalDateTime.now())// 1 = A gana, 0 = B gana, 0.5 = empate
                .build();
        // Guardar el match en la base de datos
        log.info("Saving match {} vs {}:", match.getPlayerA().getName(), match.getPlayerB().getName() );
        matchRepository.save(match);

        // Actualizar ratings de los jugadores
        glickoRatingService.updateRatings(playerA, playerB, matchDTO.result());
        log.info("Updating ratings for both players");
        playerRepository.save(playerA);
        playerRepository.save(playerB);
        log.info("Match successfully recorded");
    }

    private Faction getFactionFromDisplay(String displayName){
        for(Faction faction: Faction.values()){
            if(faction.getDisplayName().equalsIgnoreCase(displayName)){
                return faction;
            }
        }
        throw new IllegalArgumentException("Faction not valid : " + displayName);
    }

    private void updateFactionPlayed(Player player, Faction faction) {
        if (player.getFactionsPlayed() == null) {
            player.setFactionsPlayed(new HashMap<>());
        }

        FactionPlayed factionPlayed = player.getFactionsPlayed().get(faction);

        if (factionPlayed == null) {
            factionPlayed = new FactionPlayed(player.getId(), faction, 1);
        } else {
            factionPlayed.setMatchesAmount(factionPlayed.getMatchesAmount() + 1);
        }
        player.setMatchCount(player.getMatchCount() + 1);
        player.getFactionsPlayed().put(faction, factionPlayed);
    }


    public void bulkMatches(List<MatchDTO> matchDTOS){
        List<String> failedMatches = new ArrayList<>();
        log.info("Starting to bulk matches : {}", (long) matchDTOS.size());
        matchDTOS.forEach(match -> {
            try {
                recordMatch(match);
            } catch (MatchNotSavedException e) {
                // Puedes registrar el fallo o guardar el nombre de la partida fallida
                failedMatches.add(match.playerAId() + " vs " + match.playerBId());
            }
        });

        if (!failedMatches.isEmpty()) {
            // Puedes decidir cómo manejar los fallos: retornar un mensaje, guardar en log, etc.
            log.info("Some matches failed to process: " + String.join(", ", failedMatches));
        } else {
            log.info("All matches processed successfully.");
        }
    }

    public void deleteAllMatches (){
        log.info("Deleting all matches");
        matchRepository.deleteAll();
        log.info("Deleted all matches");
    }

    public void importPairingsJson(String pairingsJson) {
        try {
            JsonNode root = objectMapper.readTree(pairingsJson);
            JsonNode activePairings = root.get("active");
            List<MatchDTO> matches = new ArrayList<>();

            if (activePairings.isArray()) {
                for (JsonNode pairing : activePairings) {
                    String playerAId = pairing.get("player1").get("user").get("id").asText();
                    String playerBId = pairing.get("player2").get("user").get("id").asText();
                    int playerAScore = pairing.get("player1Game").get("points").asInt();
                    int playerBScore = pairing.get("player2Game").get("points").asInt();
                    String playerAFaction = pairing.get("player1").get("faction").asText();
                    String playerBFaction = pairing.get("player2").get("faction").asText();

                    double result;
                    if (playerAScore > playerBScore) {
                        result = 1.0;
                    } else if (playerAScore < playerBScore) {
                        result = 0.0;
                    } else {
                        result = 0.5;
                    }

                    matches.add(new MatchDTO(playerAId, playerBId, result, playerAFaction, playerBFaction));
                }
            }

            bulkMatches(matches);
        } catch (Exception e) {
            log.error("Error importing pairings JSON", e);
            throw new RuntimeException("Failed to import pairings", e);
        }
    }

    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }
    @Transactional
    public void saveMatch(Match match) { matchRepository.save(match); }
}
