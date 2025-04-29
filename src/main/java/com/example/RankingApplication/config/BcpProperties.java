package com.example.RankingApplication.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bcp")
@Data
public class BcpProperties {
    @Value("${bcp.username}")
    private String username;
    @Value("${bcp.username}")
    private String password;
}
