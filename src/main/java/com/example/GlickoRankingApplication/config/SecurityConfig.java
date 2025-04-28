package com.example.GlickoRankingApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // para APIs normalmente se desactiva CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/matches/**", "/api/players/**").authenticated()
                        .requestMatchers("/api/players").permitAll()
                        .anyRequest().permitAll() //
                )
                .httpBasic(withDefaults()); // o podrías usar bearer token, pero basic para ti puede bastar

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password123")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}
