package com.example.RankingApplication.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bcp")
@Data
public class AuthConfig {
    @Value("${auth.secret}")
    private String secret;
    @Value("${auth.username}")
    private String userName;
    @Value("${auth.password}")
    private String password;
}
