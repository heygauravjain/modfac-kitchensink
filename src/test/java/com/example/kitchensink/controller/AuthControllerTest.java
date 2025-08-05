package com.example.kitchensink.controller;

import com.example.kitchensink.model.AuthRequest;
import com.example.kitchensink.model.AuthResponse;
import com.example.kitchensink.model.RefreshTokenRequest;
import com.example.kitchensink.model.SignupRequest;
import com.example.kitchensink.security.JwtTokenService;
import com.example.kitchensink.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import com.example.kitchensink.config.TestSecurityConfig;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuthController.
 * Tests all authentication endpoints and error scenarios.
 */
@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {TestSecurityConfig.class})
@TestPropertySource(properties = {
    "spring.security.user.name=test",
    "spring.security.user.password=test"
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userDetails = new User("test@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenService.generateAccessToken(anyString(), anyString()))
                .thenReturn("access_token_123");
        when(jwtTokenService.generateRefreshToken(anyString()))
                .thenReturn("refresh_token_456");
        when(jwtTokenService.getAccessTokenExpiration())
                .thenReturn(3600L);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token_123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token_456"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void testLogin_AuthenticationFailure() throws Exception {
        // Given
        AuthRequest request = new AuthRequest("test@example.com", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSignup_Success() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("John Doe", "test@example.com", "Password123!", "1234567890", "USER");
        AuthResponse response = AuthResponse.of("access_token_123", "refresh_token_456", 3600L, "test@example.com", "USER");
        
        when(authService.registerUser(any(SignupRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("access_token_123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token_456"));
    }

    @Test
    void testSignup_UserAlreadyExists() throws Exception {
        // Given
        SignupRequest request = new SignupRequest("John Doe", "test@example.com", "Password123!", "1234567890", "USER");
        when(authService.registerUser(any(SignupRequest.class)))
                .thenThrow(new RuntimeException("User already exists"));

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token_123");
        AuthResponse response = AuthResponse.of("new_access_token", "new_refresh_token", 3600L, "test@example.com", "USER");
        
        when(authService.refreshToken(anyString()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("new_refresh_token"));
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("invalid_token");
        when(authService.refreshToken(anyString()))
                .thenThrow(new RuntimeException("Invalid refresh token"));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidateToken_ValidToken() throws Exception {
        // Given
        String validToken = "valid_token_123";
        when(jwtTokenService.isTokenValid(validToken))
                .thenReturn(true);
        when(jwtTokenService.extractUsername(validToken))
                .thenReturn("test@example.com");

        // When & Then
        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.username").value("test@example.com"));
    }

    @Test
    void testValidateToken_InvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid_token_123";
        when(jwtTokenService.isTokenValid(invalidToken))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidateToken_NoAuthorizationHeader() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/validate"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidateToken_InvalidAuthorizationHeader() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "InvalidFormat token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testValidateToken_NullAuthorizationHeader() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/validate"))
                .andExpect(status().isUnauthorized());
    }
} 