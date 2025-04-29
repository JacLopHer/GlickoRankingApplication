package com.example.GlickoRankingApplication.dto;


import com.example.GlickoRankingApplication.dto.bcp.PlayerPairing;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchDTO{
    private PlayerPairing player1;
    private PlayerPairing player2;

    private Player1Game player1Game;
    private Player1Game player2Game;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Player1Game{
        private int result;
        private int points;

    }
}