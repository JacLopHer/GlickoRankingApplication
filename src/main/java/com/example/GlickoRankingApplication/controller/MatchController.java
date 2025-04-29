package com.example.GlickoRankingApplication.controller;

import com.example.GlickoRankingApplication.model.Match;
import com.example.GlickoRankingApplication.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/private/matches")
@CrossOrigin(origins = "http://localhost:5173")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<String> processMatches(@PathVariable String eventId){
        if(eventId.isEmpty()){
            return  ResponseEntity.badRequest().body("Cannot process empty eventId");
        }
        matchService.bulkMatches(eventId);
        return ResponseEntity.ok("Matches processed successfully");
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteAllMatches(){
        matchService.deleteAllMatches();
        return ResponseEntity.ok("Removed all matches");
    }
}