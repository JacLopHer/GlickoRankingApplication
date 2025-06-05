package com.example.RankingApplication.controller;

import com.example.RankingApplication.dto.MatchDTO;
import com.example.RankingApplication.model.Match;
import com.example.RankingApplication.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "https://daafgo.github.io/Kanarias-open/"})
@Slf4j
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    @GetMapping("/private/matches/{eventId}")
    public ResponseEntity<String> processMatches(@PathVariable String eventId){
        if(eventId.isEmpty()){
            return  ResponseEntity.badRequest().body("Cannot process empty eventId");
        }
        boolean bulkedMatches = matchService.bulkMatches(eventId);
        return bulkedMatches ?
                ResponseEntity.ok("Matches processed successfully") :
                ResponseEntity.badRequest().body("Tournament already processed");
    }

    @DeleteMapping("/private/matches")
    public ResponseEntity<String> deleteAllMatches(){
        matchService.deleteAllMatches();
        return ResponseEntity.ok("Removed all matches");
    }

    /**
     * Retrieves the matches from a player
     * @return ResponseEntity<String>
     */
    @GetMapping("/public/matches/players/{playerId}")
    public ResponseEntity<List<MatchDTO>> getPlayerMatches(@PathVariable String playerId){
        List<MatchDTO> matchDTOS = new ArrayList<>();
        try{
           matchDTOS = matchService.getMatchesByPlayerId(playerId);
        }catch (Exception e){
            log.info("error retrieve player matches");
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().body(matchDTOS);
    }
}