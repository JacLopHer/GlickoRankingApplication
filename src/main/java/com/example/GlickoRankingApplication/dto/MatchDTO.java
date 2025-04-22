package com.example.GlickoRankingApplication.dto;

public record MatchDTO(
        Long playerAId,
        Long playerBId,
        double result // 1.0 = gana A, 0.0 = gana B, 0.5 = empate
) {}
