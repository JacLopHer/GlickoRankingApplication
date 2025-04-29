package com.example.RankingApplication.service;

import com.example.RankingApplication.dto.glicko.MatchResult;
import com.example.RankingApplication.model.Player;
import lombok.Data;
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

    public Player updateRatingsBulk(Player player, List<MatchResult> matchResults) {
        PlayerSnapshot snapshot = new PlayerSnapshot(player);

        double mu = (snapshot.rating - DEFAULT_RATING) / SCALE;
        double phi = snapshot.rd / SCALE;
        double sigma = snapshot.volatility;

        double sumG2Et1Et = 0.0;
        double sumGScoreMinusE = 0.0;

        for (MatchResult match : matchResults) {
            PlayerSnapshot opponent = new PlayerSnapshot(match.getOpponent());
            double mu_j = (opponent.rating - DEFAULT_RATING) / SCALE;
            double phi_j = opponent.rd / SCALE;
            double score = mapScore(match.getScore()); // 1.0 = win, 0.5 = draw, 0.0 = loss

            double g = g(phi_j);
            double E = E(mu, mu_j, phi_j);

            sumG2Et1Et += g * g * E * (1 - E);
            sumGScoreMinusE += g * (score - E);
        }

        if (matchResults.isEmpty()) {
            applyRatingDecay(player);
            return player;
        }

        double v = 1.0 / sumG2Et1Et;
        double delta = v * sumGScoreMinusE;

        double a = Math.log(sigma * sigma);
        double A = a;
        double B;
        if (delta * delta > phi * phi + v) {
            B = Math.log(delta * delta - phi * phi - v);
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
        double muPrime = mu + phiPrime * phiPrime * sumGScoreMinusE;

        double newRating = muPrime * SCALE + DEFAULT_RATING;
        double newRd = phiPrime * SCALE;

        player.setRating(newRating);
        player.setRd(capRatingDeviation(newRd));
        player.setVolatility(sigmaPrime);

        return player;

    }

    // Método auxiliar para aplicar la decadencia en el rating (RD) cuando un jugador ha estado inactivo
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

    // Método para limitar el valor de RD dentro de un rango
    private double capRatingDeviation(double rd) {
        double minRD = 30.0;
        double maxRD = 350.0;
        if (rd < minRD) return minRD;
        if (rd > maxRD) return maxRD;
        return rd;
    }

    private double f(double x, double delta, double phi, double v, double a) {
        double ex = Math.exp(x);
        double num = ex * (delta * delta - phi * phi - v - ex);
        double den = 2.0 * Math.pow(phi * phi + v + ex, 2);
        return num / den - (x - a) / (TAU * TAU);
    }

    @Data
    private static class PlayerRatingUpdate {
        private double newRating;
        private double newRd;
        private double newVolatility;

        public PlayerRatingUpdate(double newRating, double newRd, double newVolatility) {
            this.newRating = newRating;
            this.newRd = newRd;
            this.newVolatility = newVolatility;
        }
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

    private double mapScore(int score) {
        return switch (score) {  // suponiendo que MatchResult tiene un tipo de resultado
            case 2 -> 1.0;
            case 1 -> 0.5;
            case 0 -> 0.0;
            default -> throw new IllegalArgumentException("Tipo de resultado desconocido: " + score);
        };
    }
}