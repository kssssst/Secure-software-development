package com.example.market.security;

import com.example.market.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long accessTokenValidity = 1000 * 60 * 15; // 15 минут
    private final long refreshTokenValidity = 1000 * 60 * 60 * 24; // 24 часа

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(key)
                .compact();
    }

    public User validateAccessToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String email = claims.getBody().getSubject();
            String role = claims.getBody().get("role", String.class);

            User user = new User();
            user.setEmail(email);
            user.setRole(role);
            return user;
        } catch (JwtException e) {
            throw new RuntimeException("Invalid access token");
        }
    }

    public User validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String email = claims.getBody().getSubject();
            User user = new User();
            user.setEmail(email);
            return user;
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
