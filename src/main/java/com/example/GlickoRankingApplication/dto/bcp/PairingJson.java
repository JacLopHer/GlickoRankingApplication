package com.example.GlickoRankingApplication.dto.bcp;

import com.example.GlickoRankingApplication.dto.GameResult;
import lombok.Data;

@Data
public class PairingJson {
    private String id;
    private int table;
    private int round;
    private SimplePlayer player1;
    private SimplePlayer player2;
    private GameResult player1Game;
    private GameResult player2Game;
}
