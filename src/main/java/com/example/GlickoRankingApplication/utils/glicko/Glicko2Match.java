package com.example.GlickoRankingApplication.utils.glicko;

public class Glicko2Match {
    private final double opponentRating;
    private final double opponentRD;
    private final double result; // 1 = win, 0.5 = draw, 0 = loss

    public Glicko2Match(double opponentRating, double opponentRD, double result) {
        this.opponentRating = opponentRating;
        this.opponentRD = opponentRD;
        this.result = result;
    }

    // Getters
}

