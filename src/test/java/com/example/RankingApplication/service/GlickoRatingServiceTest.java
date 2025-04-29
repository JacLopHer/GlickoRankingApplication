package com.example.RankingApplication.service;

import com.example.RankingApplication.dto.glicko.MatchResult;
import com.example.RankingApplication.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlickoRatingServiceTest {

    private GlickoRatingService glickoRatingService;

    @BeforeEach
    void setUp() {
        glickoRatingService = new GlickoRatingService();
    }

    @Test
    void testUpdateRatingsBulk_WithWin_ShouldIncreaseRating() {
        Player player = new Player();
        player.setRating(1500);
        player.setRd(200);
        player.setVolatility(0.06);

        Player opponent = new Player();
        opponent.setRating(1500);
        opponent.setRd(200);
        opponent.setVolatility(0.06);

        MatchResult result = new MatchResult(opponent, 2,null);

        Player updated = glickoRatingService.updateRatingsBulk(player, List.of(result));

        assertNotNull(updated);
        assertTrue(updated.getRating() > 1500, "Expected rating to increase after a win");
        assertTrue(updated.getRd() < 200, "Expected RD to decrease after a match");
    }

    @Test
    void testUpdateRatingsBulk_WithDraw_ShouldNotChangeRatingMuch() {
        Player player = new Player();
        player.setRating(1500);
        player.setRd(100);
        player.setVolatility(0.06);

        Player opponent = new Player();
        opponent.setRating(1500);
        opponent.setRd(100);
        opponent.setVolatility(0.06);

        MatchResult result = new MatchResult(opponent,1,null);
        result.setOpponent(opponent);
        result.setScore(1); // Draw

        Player updated = glickoRatingService.updateRatingsBulk(player, List.of(result));

        assertNotNull(updated);
        assertTrue(Math.abs(updated.getRating() - 1500) < 50, "Draw should not change rating much");
    }

    @Test
    void testUpdateRatingsBulk_WithNoMatches_ShouldApplyDecay() {
        Player player = new Player();
        player.setRating(1500);
        player.setRd(100);
        player.setVolatility(0.06);
        player.setLastMatchDate(LocalDateTime.now().minusWeeks(5));

        Player updated = glickoRatingService.updateRatingsBulk(player, List.of());

        assertNotNull(updated);
        assertTrue(updated.getRd() > 100, "RD should increase after inactivity");
    }

    @Test
    void testApplyRatingDecay_ShouldIncreaseRD() {
        Player player = new Player();
        player.setRd(80);
        player.setVolatility(0.06);
        player.setLastMatchDate(LocalDateTime.now().minusWeeks(10));

        glickoRatingService.applyRatingDecay(player);

        assertTrue(player.getRd() > 80, "RD should increase due to inactivity");
        assertTrue(player.getRd() <= 350, "RD should not exceed maximum allowed");
    }

    @Test
    void testApplyRatingDecay_WithNoPreviousMatch_ShouldSetLastMatchDate() {
        Player player = new Player();
        player.setRd(100);
        player.setVolatility(0.06);
        player.setLastMatchDate(null);

        glickoRatingService.applyRatingDecay(player);

        assertNotNull(player.getLastMatchDate(), "Last match date should be initialized");
    }
}
