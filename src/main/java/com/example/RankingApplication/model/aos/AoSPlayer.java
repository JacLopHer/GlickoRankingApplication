package com.example.RankingApplication.model.aos;

import com.example.RankingApplication.model.Player;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "aos_players")
public class AoSPlayer extends Player {
    public AoSPlayer() {
        super();
    }
}
