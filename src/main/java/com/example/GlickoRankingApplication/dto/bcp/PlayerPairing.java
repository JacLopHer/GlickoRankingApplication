package com.example.GlickoRankingApplication.dto.bcp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerPairing {
    private User user;

    private String faction;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String id;
        private String firstName;
        private String lastName;
    }
}
