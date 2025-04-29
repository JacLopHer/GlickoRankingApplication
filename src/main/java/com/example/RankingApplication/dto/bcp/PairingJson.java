package com.example.RankingApplication.dto.bcp;

import com.example.RankingApplication.dto.GameResult;
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
