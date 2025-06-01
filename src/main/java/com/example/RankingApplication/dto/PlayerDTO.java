package com.example.RankingApplication.dto;

public record PlayerDTO (
    String id,
    String name,
    double rating,
    String mainFaction,
    int matches,
    int matchesWon
) {}
