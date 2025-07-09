package com.example.RankingApplication.model.tow;

import com.example.RankingApplication.model.Player;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tow_players")
public class ToWPlayer extends Player {
}
