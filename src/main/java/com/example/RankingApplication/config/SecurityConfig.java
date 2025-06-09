package com.example.RankingApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)  // activa cors
                .csrf(AbstractHttpConfigurer::disable) // desactiva CSRF si es una API
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // ajusta esto según lo que necesites
                );

        return http.build();
    }
}

