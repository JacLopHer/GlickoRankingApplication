package com.example.GlickoRankingApplication.dto;

import lombok.Data;

@Data
public class GameResult {
    private String id;
    private int result; // 2 = victoria, 1 = empate, 0 = derrota
    private int points;
}
