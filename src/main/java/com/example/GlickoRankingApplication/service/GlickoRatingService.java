package com.example.GlickoRankingApplication.service;

import com.example.GlickoRankingApplication.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class GlickoRatingService {
    // Constantes del sistema Glicko-2
    private static final double TAU = 0.5;
    private static final double DEFAULT_RATING = 1500.0;
    private static final double DEFAULT_RD = 350.0;
    private static final double DEFAULT_VOLATILITY = 0.06;
    private static final double SCALE = 173.7178;

    // Conversiones según Glicko-2 (escala interna)
    private double g(double phi) {
        return 1.0 / Math.sqrt(1.0 + 3.0 * Math.pow(phi, 2) / Math.pow(Math.PI, 2));
    }

    private double E(double mu, double mu_j, double phi_j) {
        return 1.0 / (1.0 + Math.exp(-g(phi_j) * (mu - mu_j)));
    }

    public List<Player> updateRatings(Player playerA, Player playerB, int result) {
        PlayerSnapshot snapshotA = new PlayerSnapshot(playerA);
        PlayerSnapshot snapshotB = new PlayerSnapshot(playerB);

        double scoreA = mapResultToScore(result);
        double scoreB = 1.0 - scoreA;

        PlayerRatingUpdate updatedA = calculateNewRating(snapshotA, snapshotB, scoreA);
        PlayerRatingUpdate updatedB = calculateNewRating(snapshotB, snapshotA, scoreB);

        applyRatingUpdate(playerA, updatedA);
        applyRatingUpdate(playerB, updatedB);

        return List.of(playerA, playerB); // <- devolvemos los dos players actualizados
    }

    private static class PlayerSnapshot {
        double rating;
        double rd;
        double volatility;

        public PlayerSnapshot(Player player) {
            this.rating = player.getRating();
            this.rd = player.getRd();
            this.volatility = player.getVolatility();
        }
    }

    private double mapResultToScore(int result) {
        return switch (result) {
            case 2 -> 1.0; // victoria
            case 1 -> 0.5; // empate
            case 0 -> 0.0; // derrota
            default -> throw new IllegalArgumentException("Invalid result: " + result);
        };
    }

    private static class PlayerRatingUpdate {
        double newRating;
        double newRd;
        double newVolatility;

        public PlayerRatingUpdate(double newRating, double newRd, double newVolatility) {
            this.newRating = newRating;
            this.newRd = newRd;
            this.newVolatility = newVolatility;
        }
    }

    private double f(double x, double delta, double phi, double v, double a) {
        double ex = Math.exp(x);
        double num = ex * (delta * delta - phi * phi - v - ex);
        double den = 2.0 * Math.pow(phi * phi + v + ex, 2);
        return num / den - (x - a) / (TAU * TAU);
    }

    private PlayerRatingUpdate calculateNewRating(PlayerSnapshot player, PlayerSnapshot opponent, double score) {
        double mu = (player.rating - DEFAULT_RATING) / SCALE;
        double phi = player.rd / SCALE;
        double sigma = player.volatility;

        double mu_j = (opponent.rating - DEFAULT_RATING) / SCALE;
        double phi_j = opponent.rd / SCALE;

        double g = g(phi_j);
        double E = E(mu, mu_j, phi_j);

        double v = 1.0 / (g * g * E * (1 - E));
        double delta = v * g * (score - E);

        double a = Math.log(sigma * sigma);
        double A = a;
        double B;

        if (Math.pow(delta, 2) > phi * phi + v) {
            B = Math.log(Math.pow(delta, 2) - phi * phi - v);
        } else {
            double k = 1;
            while (f(a - k * TAU, delta, phi, v, a) < 0) {
                k++;
            }
            B = a - k * TAU;
        }

        double fA = f(A, delta, phi, v, a);
        double fB = f(B, delta, phi, v, a);

        while (Math.abs(B - A) > 0.000001) {
            double C = A + (A - B) * fA / (fB - fA);
            double fC = f(C, delta, phi, v, a);

            if (fC * fB < 0) {
                A = B;
                fA = fB;
            } else {
                fA /= 2;
            }
            B = C;
            fB = fC;
        }

        double sigmaPrime = Math.exp(A / 2);
        double phiStar = Math.sqrt(phi * phi + sigmaPrime * sigmaPrime);
        double phiPrime = 1.0 / Math.sqrt(1.0 / (phiStar * phiStar) + 1.0 / v);
        double muPrime = mu + phiPrime * phiPrime * g * (score - E);

        double newRating = muPrime * SCALE + DEFAULT_RATING;
        double newRd = phiPrime * SCALE;

        return new PlayerRatingUpdate(newRating, newRd, sigmaPrime);
    }


    private void applyRatingUpdate(Player player, PlayerRatingUpdate update) {
        player.setRating(update.newRating);
        player.setRd(update.newRd);
        player.setVolatility(update.newVolatility);
    }


    public void applyRatingDecay(Player player) {
        if(player.getLastMatchDate() == null) {
            player.setLastMatchDate(LocalDateTime.now());
        }

        long weeksInactive = ChronoUnit.WEEKS.between(player.getLastMatchDate(), LocalDateTime.now());
        if (weeksInactive > 0) {
            // RD se incrementa con la inactividad, límite máximo = DEFAULT_RD
            double phi = player.getRd() / SCALE;
            double newPhi = Math.sqrt(phi * phi + player.getVolatility() * player.getVolatility() * weeksInactive);
            double newRD = Math.min(newPhi * SCALE, DEFAULT_RD); // No más de RD inicial

            player.setRd(newRD);
        }
    }
}