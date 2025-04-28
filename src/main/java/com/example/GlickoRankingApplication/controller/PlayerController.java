package com.example.GlickoRankingApplication.controller;

import com.example.GlickoRankingApplication.dto.PlayerDTO;
import com.example.GlickoRankingApplication.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/players")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Create players from an event
     * @param eventId
     * @return List<Player> list of players
     */
    @PostMapping()
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
    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers();
    }


    /**
     * Deletes all players from the database.
     * @return ResponseEntity
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAllPlayers(){
        try{
            playerService.removeAllPlayers();
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
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable String id){
        try{
            playerService.removePlayerById(id);
        } catch (Exception e){
            log.info("error");
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok("Removed all players");
    }
}
