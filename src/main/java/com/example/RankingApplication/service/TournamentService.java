package com.example.RankingApplication.service;

import com.example.RankingApplication.dto.bcp.EventDTO;
import com.example.RankingApplication.model.Tournament;
import com.example.RankingApplication.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public boolean saveTournament(EventDTO eventDTO, String tournamentId){
        if (eventDTO == null) {
            log.error("No tournament ID provided");
            return false;
        }

        if (tournamentRepository.existsById(tournamentId)) {
            log.warn("Tournament already exists, skipping save");
            return false;
        }

        Tournament tournament = new Tournament(tournamentId,
                eventDTO.name(),
                eventDTO.gameSystem().name(),
                eventDTO.gameSystem().code(),
                eventDTO.status().numberOfRounds());

        try {
            tournamentRepository.save(tournament);
            log.info("Tournament saved successfully: {}", tournamentId);
            return true;
        } catch (Exception e) {
            log.error("Failed to save tournament: {}", tournamentId, e);
            return false;
        }
    }


}
