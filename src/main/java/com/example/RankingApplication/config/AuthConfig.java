package com.example.RankingApplication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthConfig {
    private String secret;
    private String userName;
    private String password;
}

