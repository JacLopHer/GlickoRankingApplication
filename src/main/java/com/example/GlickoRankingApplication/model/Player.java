package com.example.GlickoRankingApplication.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "players")
public class Player {
    @Id
    private String id;
    private String name;

    private double rating = 1500;       // μ
    private double rd = 350;           // φ
    private double volatility = 0.06;   // σ

    private LocalDateTime lastMatchDate;


    public Player(String name){
        if(name == null || name.trim().isBlank()){
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        this.name = name;
    }
}