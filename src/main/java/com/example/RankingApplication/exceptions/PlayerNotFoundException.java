package com.example.RankingApplication.exceptions;

public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String nombre) {
        super("Jugador '" + nombre + "' no found");
    }
}
