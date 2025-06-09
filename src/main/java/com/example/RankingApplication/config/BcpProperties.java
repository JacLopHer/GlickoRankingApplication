package com.example.RankingApplication.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bcp")
@Data
public class BcpProperties {
    private String username;
    private String password;
}

