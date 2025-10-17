package com.example.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final Key key;

    private final int expirationTime;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") int expirationTime
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationTime = expirationTime;
    }

    public String generateToken(Long userId, Role role) {
        return Jwts.builder()
                .claim("role", role.name())
                .setSubject(String.valueOf(userId)) 
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String extractSubject(String token) {
        return parseToken(token).getBody().getSubject();
    }

    public Role extractRole(String token) {
        String roleStr = parseToken(token).getBody().get("role", String.class);
        return Role.valueOf(roleStr);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getBody().getExpiration();
        return expiration.before(new Date());
    }

    public boolean validateToken(String token) {
        try {
            String subject = extractSubject(token);
            return (subject != null && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
