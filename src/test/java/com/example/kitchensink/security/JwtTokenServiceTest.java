package com.example.kitchensink.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService();
        ReflectionTestUtils.setField(jwtTokenService, "secretKey", "Srgl71VAmMhSVI+8Bb5eQB6HFr3HdUbidBb/xoTWZAM=");
        ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpiration", 900000L);
        ReflectionTestUtils.setField(jwtTokenService, "refreshTokenExpiration", 604800000L);
    }

    @Test
    void generateAccessToken_ShouldCreateValidToken() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";

        // When
        String token = jwtTokenService.generateAccessToken(username, role);

        // Then
        assertNotNull(token);
        assertTrue(jwtTokenService.isTokenValid(token));
        assertEquals(username, jwtTokenService.extractUsername(token));
        assertEquals(role, jwtTokenService.extractRole(token));
    }

    @Test
    void generateRefreshToken_ShouldCreateValidToken() {
        // Given
        String username = "test@example.com";

        // When
        String token = jwtTokenService.generateRefreshToken(username);

        // Then
        assertNotNull(token);
        assertTrue(jwtTokenService.isTokenValid(token));
        assertEquals(username, jwtTokenService.extractUsername(token));
        assertNull(jwtTokenService.extractRole(token)); // Refresh tokens don't have roles
    }

    @Test
    void generateToken_WithNullRole_ShouldCreateTokenWithoutRole() {
        // Given
        String username = "test@example.com";
        String role = null;

        // When
        String token = jwtTokenService.generateAccessToken(username, role);

        // Then
        assertNotNull(token);
        assertTrue(jwtTokenService.isTokenValid(token));
        assertEquals(username, jwtTokenService.extractUsername(token));
        assertNull(jwtTokenService.extractRole(token));
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";
        String token = jwtTokenService.generateAccessToken(username, role);
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role)))
                .build();

        // When
        boolean isValid = jwtTokenService.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";
        String token = jwtTokenService.generateAccessToken(username, role);
        UserDetails userDetails = User.builder()
                .username("different@example.com")
                .password("password")
                .authorities(List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role)))
                .build();

        // When
        boolean isValid = jwtTokenService.validateToken(token, userDetails);

        // Then
        assertFalse(isValid);
    }



    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";
        String token = jwtTokenService.generateAccessToken(username, role);

        // When
        String extractedUsername = jwtTokenService.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractRole_WithValidToken_ShouldReturnRole() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_ADMIN";
        String token = jwtTokenService.generateAccessToken(username, role);

        // When
        String extractedRole = jwtTokenService.extractRole(token);

        // Then
        assertEquals(role, extractedRole);
    }

    @Test
    void extractRole_WithTokenWithoutRole_ShouldReturnNull() {
        // Given
        String username = "test@example.com";
        String token = jwtTokenService.generateRefreshToken(username); // Refresh tokens don't have roles

        // When
        String extractedRole = jwtTokenService.extractRole(token);

        // Then
        assertNull(extractedRole);
    }

    @Test
    void extractExpiration_WithValidToken_ShouldReturnExpirationDate() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";
        String token = jwtTokenService.generateAccessToken(username, role);

        // When
        Date expiration = jwtTokenService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_WithValidToken_ShouldReturnFalse() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";
        String token = jwtTokenService.generateAccessToken(username, role);

        // When
        boolean isExpired = jwtTokenService.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }



    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";
        String token = jwtTokenService.generateAccessToken(username, role);

        // When
        boolean isValid = jwtTokenService.isTokenValid(token);

        // Then
        assertTrue(isValid);
    }



    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenService.isTokenValid(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithNullToken_ShouldReturnFalse() {
        // Given
        String nullToken = null;

        // When
        boolean isValid = jwtTokenService.isTokenValid(nullToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithEmptyToken_ShouldReturnFalse() {
        // Given
        String emptyToken = "";

        // When
        boolean isValid = jwtTokenService.isTokenValid(emptyToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithMalformedToken_ShouldReturnFalse() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean isValid = jwtTokenService.isTokenValid(malformedToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractClaim_WithValidToken_ShouldReturnClaim() {
        // Given
        String username = "test@example.com";
        String role = "ROLE_USER";
        String token = jwtTokenService.generateAccessToken(username, role);

        // When
        String extractedUsername = jwtTokenService.extractClaim(token, claims -> claims.getSubject());

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void getAccessTokenExpiration_ShouldReturnConfiguredValue() {
        // When
        Long expiration = jwtTokenService.getAccessTokenExpiration();

        // Then
        assertEquals(900000L, expiration);
    }
} 