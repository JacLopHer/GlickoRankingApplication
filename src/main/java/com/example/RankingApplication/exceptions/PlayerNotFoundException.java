package com.example.RankingApplication.exceptions;

public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String nombre) {
        super("Player '" + nombre + "' no found");
    }
}
