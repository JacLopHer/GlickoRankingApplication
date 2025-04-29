package com.example.RankingApplication.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppToken {
    private String accessToken;
}
