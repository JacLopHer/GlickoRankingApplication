package com.example.GlickoRankingApplication.dto;


import lombok.Data;

@Data
public class PlayerJson {
    private String id;

    private String userId;

    private User user;

    @Data
    public static class User {
        private String firstName;
        private String lastName;
    }
}
