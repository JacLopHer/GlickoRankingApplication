package com.example.RankingApplication.controller;

import com.example.RankingApplication.dto.PlayerDTO;
import com.example.RankingApplication.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
@CrossOrigin(origins = {"http://localhost:5173", "https://daafgo.github.io/Kanarias-open/", "https://jaclopher.github.io/mission-selector-app/"})
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
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers();
    }


    /**
     * Deletes all players from the database.
     * @return ResponseEntity
     */
    @DeleteMapping("/private/players/delete")
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
    @DeleteMapping("/private/{id}")
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
