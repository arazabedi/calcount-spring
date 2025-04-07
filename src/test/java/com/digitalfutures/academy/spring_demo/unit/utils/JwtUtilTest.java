package com.digitalfutures.academy.spring_demo.unit.utils;

import com.digitalfutures.academy.spring_demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secretKey;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        secretKey = Base64.getEncoder().encodeToString("this-is-a-32-byte-secret-key-123456".getBytes());
        Field secretKeyField = JwtUtil.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(jwtUtil, secretKey);
    }

    private Claims parseToken(String token) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        Key signingKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(token)
                .getBody();
    }

    @Nested
    class GenerateTokenTest {

        @Test
        void generateToken_ValidUsername_ReturnsNonNullToken() {
            // Arrange
            String username = "testUser";

            // Act
            String token = jwtUtil.generateToken(username);

            // Assert
            assertNotNull(token, "Token should not be null");
        }

        @Test
        void generateToken_ValidUsername_TokenContainsUsernameAsSubject() {
            // Arrange
            String username = "testUser";

            // Act
            String token = jwtUtil.generateToken(username);
            Claims claims = parseToken(token);

            // Assert
            assertEquals(username, claims.getSubject(), "Token subject should match username");
        }

        @Test
        void generateToken_ValidUsername_TokenExpiresIn60Minutes() {
            // Arrange
            String username = "testUser";

            // Act
            String token = jwtUtil.generateToken(username);
            Claims claims = parseToken(token);
            Date issuedAt = claims.getIssuedAt();
            Date expiration = claims.getExpiration();

            // Assert
            long expectedDurationMs = TimeUnit.MINUTES.toMillis(60);
            long actualDurationMs = expiration.getTime() - issuedAt.getTime();
            assertEquals(expectedDurationMs, actualDurationMs, 1000, "Token should expire in 60 minutes (±1 second)");
        }
    }

    @Nested
    class ExtractUsernameTest {

        @Test
        void extractUsername_ValidToken_ReturnsUsername() {
            // Arrange
            String username = "testUser";
            String token = jwtUtil.generateToken(username);

            // Act
            String extractedUsername = jwtUtil.extractUsername(token);

            // Assert
            assertEquals(username, extractedUsername, "Extracted username should match");
        }

        @Test
        void extractUsername_InvalidToken_ThrowsException() {
            // Arrange
            String invalidToken = "invalid.token.here";

            // Act & Assert
            assertThrows(Exception.class, () -> jwtUtil.extractUsername(invalidToken),
                    "Invalid token should throw exception");
        }
    }

    @Nested
    class ValidateTokenTest {

        @Test
        void validateToken_ValidTokenAndUsername_ReturnsTrue() {
            // Arrange
            String username = "testUser";
            String token = jwtUtil.generateToken(username);

            // Act
            boolean isValid = jwtUtil.validateToken(token, username);

            // Assert
            assertTrue(isValid, "Valid token with correct username should return true");
        }

        @Test
        void validateToken_ValidTokenButWrongUsername_ReturnsFalse() {
            // Arrange
            String username = "testUser";
            String token = jwtUtil.generateToken(username);

            // Act
            boolean isValid = jwtUtil.validateToken(token, "wrongUser");

            // Assert
            assertFalse(isValid, "Valid token with incorrect username should return false");
        }

        @Test
        void validateToken_ExpiredToken_ReturnsFalse() throws Exception {
            // Arrange
            String username = "testUser";
            Instant now = Instant.now();
            Instant expiration = now.minus(1, ChronoUnit.MINUTES);
            Claims claims = Jwts.claims().setSubject(username);
            claims.setIssuedAt(Date.from(now));
            claims.setExpiration(Date.from(expiration));
            Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS256.getJcaName());
            String expiredToken = Jwts.builder()
                    .setClaims(claims)
                    .signWith(signingKey)
                    .compact();

            // Act
            boolean isValid = jwtUtil.validateToken(expiredToken, username);

            // Assert
            assertFalse(isValid, "Expired token should return false");
        }

        @Test
        void validateToken_InvalidToken_ReturnsFalse() {
            // Arrange
            String invalidToken = "invalid.token.here";

            // Act
            boolean isValid = jwtUtil.validateToken(invalidToken, "testUser");

            // Assert
            assertFalse(isValid, "Invalid token should return false");
        }
    }

    @Nested
    class IsTokenExpiredTest {

        @Test
        void isTokenExpired_ValidToken_ReturnsFalse() throws Exception {
            // Arrange
            String token = jwtUtil.generateToken("testUser");
            Method isTokenExpiredMethod = JwtUtil.class.getDeclaredMethod("isTokenExpired", String.class);
            isTokenExpiredMethod.setAccessible(true);

            // Act
            boolean isExpired = (boolean) isTokenExpiredMethod.invoke(jwtUtil, token);

            // Assert
            assertFalse(isExpired, "Valid token should not be expired");
        }

        @Test
        void isTokenExpired_ExpiredToken_ReturnsTrue() throws Exception {
            // Arrange
            String username = "testUser";
            Instant now = Instant.now();
            Instant expiration = now.minus(1, ChronoUnit.MINUTES);
            Claims claims = Jwts.claims().setSubject(username);
            claims.setIssuedAt(Date.from(now));
            claims.setExpiration(Date.from(expiration));
            Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(secretKey), SignatureAlgorithm.HS256.getJcaName());
            String expiredToken = Jwts.builder()
                    .setClaims(claims)
                    .signWith(signingKey)
                    .compact();
            Method isTokenExpiredMethod = JwtUtil.class.getDeclaredMethod("isTokenExpired", String.class);
            isTokenExpiredMethod.setAccessible(true);

            // Act
            boolean isExpired = (boolean) isTokenExpiredMethod.invoke(jwtUtil, expiredToken);

            // Assert
            assertTrue(isExpired, "Expired token should return true");
        }
    }
}