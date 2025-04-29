package com.example.RankingApplication.dto.glicko;


import com.example.RankingApplication.enums.Faction;
import com.example.RankingApplication.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchResult {
    private Player opponent;
    private int score;
    private Faction faction;
}
