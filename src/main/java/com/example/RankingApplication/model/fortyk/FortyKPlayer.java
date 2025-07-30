package com.example.RankingApplication.model.fortyk;

import com.example.RankingApplication.model.Player;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "fortyk_players")
public class FortyKPlayer extends Player {
    public FortyKPlayer() {
        super();
    }
}
