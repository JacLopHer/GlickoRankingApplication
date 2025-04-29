package com.example.GlickoRankingApplication.dto.bcp;

import lombok.Data;

@Data
public class SimplePlayer {
    private String id;
    private UserJson user;
    private String team;
    private String faction;
}
