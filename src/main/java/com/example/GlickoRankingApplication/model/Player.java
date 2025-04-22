package com.example.GlickoRankingApplication.model;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double rating = 1500;       // μ
    private double rd = 350;           // φ
    private double volatility = 0.06;   // σ

    private LocalDateTime lastMatchDate;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastMatchDate = LocalDateTime.now();
    }

    public Player(String name){
        if(name == null || name.trim().isBlank()){
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        this.name = name;
    }
}