package com.example.RankingApplication.controller;

import com.example.RankingApplication.config.AuthConfig;
import com.example.RankingApplication.dto.security.AppToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Key;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthConfig authConfig;


    @PostMapping("/login")
    public AppToken login(@RequestParam String username, @RequestParam String password) {

        return authConfig.getUserName().equals(username)
                && authConfig.getPassword().equals(password) ?
                generateToken() : new AppToken("Incorrect login");
    }

    private AppToken generateToken() {
        Key key = Keys.hmacShaKeyFor(authConfig.getSecret().getBytes());
        String accessToken = Jwts.builder()
                .subject(authConfig.getUserName())
                .signWith(key)
                .compact();
        return new AppToken(accessToken);
    }
}
