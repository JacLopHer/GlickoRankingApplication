package com.example.RankingApplication.controller;

import com.example.RankingApplication.dto.PlayerDTO;
import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.service.DecayService;
import com.example.RankingApplication.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@Slf4j
public class PlayerController {
    private final PlayerService playerService;
    private final DecayService decayService;

    public PlayerController(PlayerService playerService, DecayService decayService) {
        this.playerService = playerService;
        this.decayService = decayService;
    }

    /**
     * Create players from an event
     * @param eventId event identification
     * @return List<Player> list of players
     */
    @PostMapping("/private/players")
    public ResponseEntity<List<PlayerDTO>> createPlayers(@RequestParam String eventId) {
        if(!eventId.isEmpty()){
            playerService.createPlayersFromBCP(eventId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Retrieves all players
     * @return List<PlayerDTO> List of players
     */
    @GetMapping("public/players")
    public List<PlayerDTO> getAllPlayers(@RequestParam int gameSystemCode) {
        return playerService.getAllPlayers(gameSystemCode);
    }


    /**
     * Deletes all players from the database.
     * @return ResponseEntity
     */
    @DeleteMapping("/private/players/delete")
    public ResponseEntity<String> deleteAllPlayers(@RequestParam int gameSystemCode){
        try{
            playerService.removeAllPlayers(gameSystemCode);
        } catch (Exception e){
            log.info("error");
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok("Removed all players");
    }

    /**
     * Deletes a players from the database.
     * @return ResponseEntity
     */
    @DeleteMapping("/private/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable String id, @RequestParam int gameSystemCode){
        try{
            playerService.removePlayerById(id,gameSystemCode);
        } catch (Exception e){
            log.info("error");
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok("Removed all players");
    }

    @PostMapping("/private/players/decay")
    public ResponseEntity<List<Player>> applyDecay(@RequestParam int gameSystemCode) {
        try{
            List<Player> response = decayService.applyDecayToInactivePlayers(gameSystemCode);
            return ResponseEntity.ok(response);
        } catch (Exception e){
            log.info("error when decaying players");
            return ResponseEntity.internalServerError().build();
        }
    }
}
