package com.example.GlickoRankingApplication.controller;

import com.example.GlickoRankingApplication.dto.CreatePlayersRequest;
import com.example.GlickoRankingApplication.model.Player;
import com.example.GlickoRankingApplication.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/players")
@CrossOrigin(origins = "http://localhost:5173")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<List<Player>> createPlayers(@RequestBody CreatePlayersRequest request) {
        List<Player> created = playerService.createPlayers(request);
        return ResponseEntity.ok(created);
    }

    // Endpoint para obtener todos los jugadores
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // Endpoint para obtener un jugador por nombre
    @GetMapping("/{name}")
    public Player getPlayerByName(@PathVariable String name) {
        return playerService.getPlayerByName(name);
    }
}
