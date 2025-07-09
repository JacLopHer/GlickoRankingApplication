package com.example.RankingApplication.model;

import com.example.RankingApplication.model.aos.AoSPlayer;
import com.example.RankingApplication.model.fortyk.FortyKPlayer;
import com.example.RankingApplication.model.tow.ToWPlayer;

public class PlayerClassResolver {

    private PlayerClassResolver(){}

    public static Class<? extends Player> resolveFromGameSystemCode(int code) {
        return switch (code) {
            case 1 -> FortyKPlayer.class;
            case 4 -> AoSPlayer.class;
            case 89 -> ToWPlayer.class;
            default -> throw new IllegalArgumentException("Unsupported game system code: " + code);
        };
    }

    public static <T extends Player> Class<T> resolveFromGameSystemCodeToClass(int code) {
        return switch (code) {
            case 1 -> castOrThrow(FortyKPlayer.class);
            case 4 -> castOrThrow(AoSPlayer.class);
            case 89 -> castOrThrow(ToWPlayer.class);
            default -> throw new IllegalArgumentException("Unsupported game system code: " + code);
        };
    }

    @SuppressWarnings("unchecked")
    private static <T extends Player> Class<T> castOrThrow(Class<? extends Player> clazz) {
        if (Player.class.isAssignableFrom(clazz)) {
            return (Class<T>) clazz;
        } else {
            throw new IllegalArgumentException("Cannot cast " + clazz.getName() + " to Player");
        }
    }
}
