package com.example.GlickoRankingApplication.security;

import com.example.GlickoRankingApplication.config.AuthConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;

@WebFilter(urlPatterns = "/*")
public class JwtFilter extends OncePerRequestFilter {


    private final AuthConfig authConfig;


    private final AuthenticationManager authenticationManager;

    public JwtFilter(AuthConfig authConfig, AuthenticationManager authenticationManager) {
        this.authConfig = authConfig;
        this.authenticationManager = authenticationManager;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); //

            try {
                Key key = Keys.hmacShaKeyFor(authConfig.getSecret().getBytes());

                Claims claims = Jwts.parser()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String username = claims.getSubject();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, null);

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                logger.error("Invalid JWT token", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
