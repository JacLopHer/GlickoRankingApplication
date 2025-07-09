package com.example.RankingApplication.dto;


import com.example.RankingApplication.dto.bcp.PlayerPairing;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}