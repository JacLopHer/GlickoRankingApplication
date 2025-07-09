package com.example.RankingApplication.model;

import com.example.RankingApplication.enums.Faction;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "players")
public abstract class Player {

    @Id
    private String id;

    private String name;

    private EnumMap<Faction, FactionPlayed> factionsPlayed = new EnumMap<>(Faction.class);

    private double rating = 1500.0;

    private double rd = 350.0;

    private double volatility = 0.06;

    private LocalDateTime lastMatchDate;

    private int matchCount = 0;

    private int matchesWon = 0;

    /**
     * Protected constructor to enforce validation when creating instances manually.
     */
    protected Player(String name) {
        setName(name);
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be blank");
        }
        this.name = name.trim();
    }

    /**
     * Convenience method to add or update a faction played.
     */
    public void addOrUpdateFactionPlayed(Faction faction, FactionPlayed factionPlayed) {
        if (faction == null || factionPlayed == null) {
            throw new IllegalArgumentException("Faction and FactionPlayed must not be null");
        }
        factionsPlayed.put(faction, factionPlayed);
    }

    /**
     * Get total matches played across all factions.
     */
    public int getTotalMatchesPlayed() {
        return factionsPlayed.values().stream()
                .mapToInt(FactionPlayed::getMatchesAmount)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
