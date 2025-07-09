package com.example.RankingApplication.service;

import com.example.RankingApplication.model.Player;
import com.example.RankingApplication.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DecayService {

    private final PlayerRepository playerRepository;

    public List<Player> applyDecayToInactivePlayers() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        List<Player> allPlayers = playerRepository.findAll();

        // Aplica 10% de decay

        return allPlayers.stream()
                .filter(p -> p.getLastMatchDate() != null && p.getLastMatchDate().isBefore(oneMonthAgo))
                .map(player -> {
                    double newRating = player.getRating() * 0.9; // Aplica 10% de decay
                    player.setRating(newRating);
                    return playerRepository.save(player);
                })
                .toList();
    }
}
