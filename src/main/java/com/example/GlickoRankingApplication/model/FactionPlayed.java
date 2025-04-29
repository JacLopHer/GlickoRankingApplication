package com.example.GlickoRankingApplication.model;

import com.example.GlickoRankingApplication.enums.Faction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "playerFactions")
public class FactionPlayed {
    @Id
    private String playerId;
    private Faction faction;
    private int matchesAmount;
}
