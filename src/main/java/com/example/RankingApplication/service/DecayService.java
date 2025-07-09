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
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        Class<? extends Player> playerClass = PlayerClassResolver.resolveFromGameSystemCode(gameSystemCode);
        if (playerClass == null) {
            throw new IllegalArgumentException("Unsupported game system code: " + gameSystemCode);
        }

        List<Player> players = playerRepository.findAll(playerClass).stream().map(Player.class::cast).toList();

        List<Player> filteredPlayers = players.stream()
                .filter(p -> p.getLastMatchDate() != null && p.getLastMatchDate().isBefore(oneMonthAgo))
                .toList();

        filteredPlayers.forEach(player -> {
            player.setRating(player.getRating() * 0.9);
            playerRepository.save(player);
        });

        return filteredPlayers;
    }
}
