package com.example.GlickoRankingApplication.dto.glicko;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class to represent just a snapshot of an opponent player for glicko
 */
@Data
@AllArgsConstructor
public class PlayerSnapshot {
    private double rating;
    private double rd;
    private double volatility;
}
