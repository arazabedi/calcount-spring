package com.digitalfutures.academy.spring_demo.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtUtil {
    // In production, this should be stored securely (e.g., environment variables, vault)
    // Secret key should be at least 256 bits (32 bytes) for HS256
    @Value("${jwt.secret-key}")
    private String secretKey;

    private final long EXPIRATION_TIME = 60; // 60 minutes

    // Get signing key using older JJWT API syntax for better compatibility
    private Key getSigningKey() {
        byte[] apiKeySecretBytes = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(EXPIRATION_TIME, ChronoUnit.MINUTES);

        Map<String, Object> claims = new HashMap<>();

        // Using older JJWT syntax for compatibility
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        // Using older JJWT syntax for compatibility
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            // If we can't extract claims or encounter another error, consider token invalid
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractAllClaims(token).getExpiration();
            return expirationDate.before(Date.from(Instant.now()));
        } catch (Exception e) {
            // If we can't extract claims or encounter another error, consider token expired
            return true;
        }
    }
}