package com.example.RankingApplication.exceptions;

public class PlayerInstantiationException extends RuntimeException {

    public PlayerInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerInstantiationException(String message) {
        super(message);
    }
}

