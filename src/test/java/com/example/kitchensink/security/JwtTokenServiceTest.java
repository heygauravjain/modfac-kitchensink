package com.example.kitchensink.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

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
} 