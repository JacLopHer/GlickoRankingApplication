package com.example.GlickoRankingApplication.service;

import com.example.GlickoRankingApplication.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public void updateRatings(Player player, Player opponent, double score) {
        log.info("Starting to update ratings for game between: {} vs {}", player.getName(), opponent.getName());

        // Verificar si el jugador tiene una volatilidad. Si no la tiene, se usa el valor predeterminado.
        if (player.getVolatility() == 0) {
            player.setVolatility(DEFAULT_VOLATILITY);
        }

        // Verificar si el oponente tiene una volatilidad. Si no la tiene, se usa el valor predeterminado.
        if (opponent.getVolatility() == 0) {
            opponent.setVolatility(DEFAULT_VOLATILITY);
        }

        // Conversión inicial
        double mu = (player.getRating() - DEFAULT_RATING) / SCALE;
        double phi = player.getRd() / SCALE;
        double sigma = player.getVolatility(); // Usamos la volatilidad del jugador

        double mu_j = (opponent.getRating() - DEFAULT_RATING) / SCALE;
        double phi_j = opponent.getRd() / SCALE;

        // Calcular g y E
        double g = g(phi_j);
        double E = E(mu, mu_j, phi_j);

        log.info("g = {}, E = {}", g, E);

        // Calcular v y delta
        double v = 1.0 / (g * g * E * (1 - E));
        double delta = v * g * (score - E);

        log.info("v = {}, delta = {}", v, delta);

        // Resolución para a, A, B, sigmaPrime, phiStar, etc.
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

        // Actualización final para player
        player.setRating(muPrime * SCALE + DEFAULT_RATING);
        player.setRd(phiPrime * SCALE);
        player.setVolatility(sigmaPrime);

        // Ahora actualizar ratings del oponente (playerB)
        double muB = (opponent.getRating() - DEFAULT_RATING) / SCALE;
        double phiB = opponent.getRd() / SCALE;
        double sigmaB = opponent.getVolatility();

        double mu_jB = (player.getRating() - DEFAULT_RATING) / SCALE;
        double phi_jB = player.getRd() / SCALE;

        g = g(phi_jB);
        E = E(muB, mu_jB, phi_jB);
        v = 1.0 / (g * g * E * (1 - E));
        delta = v * g * (1 - score - E); // El score invertido para el oponente

        a = Math.log(sigmaB * sigmaB);
        A = a;
        B = Math.log(Math.pow(delta, 2) - phiB * phiB - v);

        fA = f(A, delta, phiB, v, a);
        fB = f(B, delta, phiB, v, a);

        while (Math.abs(B - A) > 0.000001) {
            double C = A + (A - B) * fA / (fB - fA);
            double fC = f(C, delta, phiB, v, a);

            if (fC * fB < 0) {
                A = B;
                fA = fB;
            } else {
                fA /= 2;
            }

            B = C;
            fB = fC;
        }

        log.info("Player {}: v = {}, delta = {}", player.getName(), v, delta);

        sigmaPrime = Math.exp(A / 2);
        phiStar = Math.sqrt(phiB * phiB + sigmaPrime * sigmaPrime);
        phiPrime = 1.0 / Math.sqrt(1.0 / (phiStar * phiStar) + 1.0 / v);
        muPrime = muB + phiPrime * phiPrime * g * (1 - score - E);

        // Actualización final para opponent (playerB)
        opponent.setRating(muPrime * SCALE + DEFAULT_RATING);
        opponent.setRd(phiPrime * SCALE);
        opponent.setVolatility(sigmaPrime);

        player.setLastMatchDate(LocalDateTime.now());
        opponent.setLastMatchDate(LocalDateTime.now());

        log.info("Ratings updated");
    }

    private double f(double x, double delta, double phi, double v, double a) {
        double ex = Math.exp(x);
        double num = ex * (delta * delta - phi * phi - v - ex);
        double den = 2.0 * Math.pow(phi * phi + v + ex, 2);
        return num / den - (x - a) / (TAU * TAU);
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