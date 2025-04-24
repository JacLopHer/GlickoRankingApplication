package com.example.GlickoRankingApplication.service;

import com.example.GlickoRankingApplication.dto.MatchDTO;
import com.example.GlickoRankingApplication.exceptions.MatchNotSavedException;
import com.example.GlickoRankingApplication.exceptions.PlayerNotFoundException;
import com.example.GlickoRankingApplication.model.Match;
import com.example.GlickoRankingApplication.model.Player;
import com.example.GlickoRankingApplication.repository.MatchRepository;
import com.example.GlickoRankingApplication.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public void recordMatch(MatchDTO matchDTO) {
        // Encontrar jugadores en la base de datos
        log.info("Start recording match between : {} vs {}", matchDTO.playerAId(), matchDTO.playerBId());
        Player playerA = playerRepository.findById(matchDTO.playerAId())
                .orElseThrow(() -> new PlayerNotFoundException(matchDTO.playerAId() + "not found"));
        Player playerB = playerRepository.findById(matchDTO.playerBId())
                .orElseThrow(() -> new IllegalArgumentException(matchDTO.playerBId() + "not found"));

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

    public List<Match> getAllMatches(){
        return matchRepository.findAll();
    }
    @Transactional
    public void saveMatch(Match match) { matchRepository.save(match); }
}
