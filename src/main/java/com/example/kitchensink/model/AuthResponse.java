package com.example.kitchensink.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private String email;
    private String role;
    
    /**
     * Creates a new AuthResponse with default token type
     */
    public static AuthResponse of(String accessToken, String refreshToken, Long expiresIn, String email, String role) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .email(email)
                .role(role)
                .build();
    }
} 