package com.example.GlickoRankingApplication.dto;

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
