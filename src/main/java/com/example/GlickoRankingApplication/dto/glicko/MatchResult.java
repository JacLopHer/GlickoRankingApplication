package com.example.GlickoRankingApplication.dto.glicko;


import com.example.GlickoRankingApplication.enums.Faction;
import com.example.GlickoRankingApplication.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchResult {
    private Player opponent;
    private int score;
    private Faction faction;
}
