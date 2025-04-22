package com.example.GlickoRankingApplication.controller;

import com.example.GlickoRankingApplication.dto.MatchDTO;
import com.example.GlickoRankingApplication.model.Match;
import com.example.GlickoRankingApplication.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    public ResponseEntity<?> recordMatch(@RequestBody MatchDTO matchDTO) {
        if(matchDTO.playerAId() == null || matchDTO.playerBId() == null){
            return ResponseEntity.badRequest().body("Player IDs cannot be null");
        }
        matchService.recordMatch(matchDTO);
        return ResponseEntity.ok().build();
    }
    @GetMapping
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    @PostMapping("/bulk")
    public ResponseEntity<String> processMatches(@RequestBody List<MatchDTO> matchDTOS){
        if(matchDTOS.isEmpty()){
            return  ResponseEntity.badRequest().body("Cannot process empty list");
        }
        matchService.bulkMatches(matchDTOS);
        return ResponseEntity.ok("Matches processed successfully");
    }
}