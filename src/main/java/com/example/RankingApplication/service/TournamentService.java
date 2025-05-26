package com.example.RankingApplication.service;

import com.example.RankingApplication.model.Tournament;
import com.example.RankingApplication.repository.TournamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class TournamentService {

    @Autowired
    private  TournamentRepository tournamentRepository;

    public boolean saveTournament(String tournamentId){
        if (tournamentId == null || tournamentId.isBlank()) {
            log.error("No tournament ID provided");
            return false;
        }

        if (tournamentRepository.existsById(tournamentId)) {
            log.warn("Tournament already exists, skipping save");
            return false;
        }

        Tournament tournament = new Tournament(tournamentId);

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
