package com.example.GlickoRankingApplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerJson {
    private User user;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String id;
        private String firstName;
        private String lastName;
    }
}
