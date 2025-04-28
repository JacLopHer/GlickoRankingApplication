package com.example.GlickoRankingApplication.service;

import com.example.GlickoRankingApplication.clients.BCPClient;
import com.example.GlickoRankingApplication.dto.MatchDTO;
import com.example.GlickoRankingApplication.enums.Faction;
import com.example.GlickoRankingApplication.exceptions.PlayerNotFoundException;
import com.example.GlickoRankingApplication.model.FactionPlayed;
import com.example.GlickoRankingApplication.model.Match;
import com.example.GlickoRankingApplication.model.Player;
import com.example.GlickoRankingApplication.repository.FactionPlayedRepository;
import com.example.GlickoRankingApplication.repository.MatchRepository;
import com.example.GlickoRankingApplication.repository.PlayerRepository;
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

    @Autowired
    private PlayerService playerService;

    @Autowired
    private BCPClient bcpClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void recordMatch(MatchDTO matchDTO) {
        log.info("Start recording match between : {} vs {}", matchDTO.getPlayer1().getUser().getId(), matchDTO.getPlayer2().getUser().getId());

        Player playerA = playerRepository.findById(matchDTO.getPlayer1().getUser().getId())
                .orElseThrow(() -> new PlayerNotFoundException(matchDTO.getPlayer1().getUser().getId() + " not found"));
        Player playerB = playerRepository.findById(matchDTO.getPlayer2().getUser().getId())
                .orElseThrow(() -> new PlayerNotFoundException(matchDTO.getPlayer2().getUser().getId() + " not found"));

        updateFactionPlayed(playerA, getFactionFromDisplay(matchDTO.getPlayer1().getFaction()));
        updateFactionPlayed(playerB, getFactionFromDisplay(matchDTO.getPlayer2().getFaction()));

        Match match = Match.builder()
                .playerA(playerA)
                .playerB(playerB)
                .result(matchDTO.getPlayer1Game().getResult())
                .date(LocalDateTime.now())
                .build();

        log.info("Saving match {} vs {}", match.getPlayerA().getName(), match.getPlayerB().getName());
        matchRepository.save(match);

        List<Player> updatedPlayers = glickoRatingService.updateRatings(playerA, playerB, matchDTO.getPlayer1Game().getResult());
        log.info("Updating ratings for both players");

        playerRepository.saveAll(updatedPlayers); // Guardamos los jugadores actualizados de golpe
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


    public void bulkMatches(String eventId){
        List<String> failedMatches = new ArrayList<>();
        log.info("Starting to get matches for event : {}", eventId);
        int numberOfRounds = bcpClient.getNumberOfRounds(eventId);
        int numberOfMatches = 0;

        playerService.createPlayersFromBCP(eventId);

        for (int i = 1; i <= numberOfRounds; i++) {
            List<MatchDTO> matchDTOS = bcpClient.getPairings(eventId,i);
            matchDTOS.forEach(this::recordMatch);
            numberOfMatches += matchDTOS.size();
        }

        if (!failedMatches.isEmpty()) {
            // Puedes decidir cómo manejar los fallos: retornar un mensaje, guardar en log, etc.
            log.info("Some matches failed to process: " + String.join(", ", failedMatches));
        } else {
            log.info("All matches processed successfully : {} matches and {} rounds", numberOfMatches, numberOfRounds);
        }
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

}
