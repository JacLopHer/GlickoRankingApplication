package com.example.RankingApplication.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "matches")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    private String id;

    private Player playerA;
    private Player playerB;

    private double result;

    private LocalDateTime date;


}