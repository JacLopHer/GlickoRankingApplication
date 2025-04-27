package com.example.GlickoRankingApplication.dto;

public record MatchDTO(
        String playerAId,
        String playerBId,
        double result,// 1.0 = gana A, 0.0 = gana B, 0.5 = empate
        String playerAFaction,
        String playerBFaction
) {}
