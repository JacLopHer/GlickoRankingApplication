package com.example.GlickoRankingApplication.controller;

import com.example.GlickoRankingApplication.dto.PlayersRequest;
import com.example.GlickoRankingApplication.model.Player;
import com.example.GlickoRankingApplication.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/players")
@CrossOrigin(origins = "http://localhost:5173")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/create")
    public ResponseEntity<List<Player>> createPlayers(@RequestBody PlayersRequest request) {
        // Llamamos al servicio para crear los jugadores
        List<Player> createdPlayers = new ArrayList<>();
        if(request != null ) {
            createdPlayers = playerService.createPlayersFromJson(request.getActive());
        }
        // Si la lista de jugadores creados no está vacía, devolvemos un 201
        if (!createdPlayers.isEmpty()) {
            return new ResponseEntity<>(createdPlayers, HttpStatus.CREATED);
        }

        // Si no se han creado jugadores nuevos, devolvemos un 200
        return ResponseEntity.ok().build();
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
