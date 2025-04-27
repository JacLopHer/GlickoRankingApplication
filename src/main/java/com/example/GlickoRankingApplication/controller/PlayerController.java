package com.example.GlickoRankingApplication.controller;

import com.example.GlickoRankingApplication.dto.PlayerDTO;
import com.example.GlickoRankingApplication.model.Player;
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

    @PostMapping()
    public ResponseEntity<List<Player>> createPlayers(@RequestParam String eventId) {
        // Llamamos al servicio para crear los jugadores
        List<Player> players = playerService.createPlayersFromBCP(eventId);
        return ResponseEntity.ok(players);
    }

    // Endpoint para obtener todos los jugadores
    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // Endpoint para obtener un jugador por nombre
    @GetMapping("/{name}")
    public Player getPlayerByName(@PathVariable String name) {
        return playerService.getPlayerByName(name);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllPlayers(){
        try{
            playerService.removeAllPlayers();
        } catch (Exception e){
            log.info("error");
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok("Removed all players");
    }
}
