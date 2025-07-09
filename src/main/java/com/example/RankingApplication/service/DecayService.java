package com.example.RankingApplication.service;

import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.model.PlayerClassResolver;
import com.example.RankingApplication.repository.PlayerRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DecayService {

    private final PlayerRepositoryCustom<Player> playerRepository;

    public List<Player> applyDecayToInactivePlayers(int gameSystemCode) {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);

        Class<? extends Player> playerClass = PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode);
        if (playerClass == null) {
            throw new IllegalArgumentException("Unsupported game system code: " + gameSystemCode);
        }

        List<Player> players = playerRepository.findAll(playerClass).stream().toList();

        List<Player> filteredPlayers = players.stream()
                .filter(p -> p.getLastMatchDate() != null && p.getLastMatchDate().isBefore(twoMonthsAgo))
                .toList();

        filteredPlayers.forEach(player -> {
            if (player.getRating() >= 1500) {
                double decayedRating = player.getRating() * 0.9;
                player.setRating(Math.max(decayedRating, 1500));
                playerRepository.save(player);
            }
        });

        return filteredPlayers;
    }
}
