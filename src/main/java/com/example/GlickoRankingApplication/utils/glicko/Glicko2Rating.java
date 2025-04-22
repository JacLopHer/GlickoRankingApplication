package com.example.GlickoRankingApplication.utils.glicko;

import java.util.List;

public class Glicko2Rating {
    private double rating; // μ
    private double rd;     // φ
    private double volatility; // σ

    public Glicko2Rating(double rating, double rd, double volatility) {
        this.rating = rating;
        this.rd = rd;
        this.volatility = volatility;
    }

    // Getters and setters

    public double getRating() {
        return rating;
    }

    public double getRd() {
        return rd;
    }

    public double getVolatility() {
        return volatility;
    }

    public void update(List<Glicko2Match> matches) {
        // Lógica de Glicko-2:
        // - convertir rating/rd a escala interna
        // - aplicar fórmula
        // - actualizar rating, rd, volatility

        // Aquí vendría el algoritmo matemático completo (lo podemos añadir en detalle si quieres)
    }
}
