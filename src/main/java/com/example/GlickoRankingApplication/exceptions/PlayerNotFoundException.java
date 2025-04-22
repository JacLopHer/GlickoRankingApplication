package com.example.GlickoRankingApplication.exceptions;

public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String nombre) {
        super("Jugador '" + nombre + "' no encontrado");
    }
}
