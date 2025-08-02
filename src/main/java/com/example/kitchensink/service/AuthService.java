package com.example.kitchensink.service;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.model.AuthResponse;
import com.example.kitchensink.model.SignupRequest;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthResponse registerUser(SignupRequest request) {
        // Check if user already exists
        if (memberRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create new user
        MemberDocument user = new MemberDocument();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // Handle role correctly
        String role = request.getRole();
        if (role == null || role.isEmpty()) {
            role = "USER";
        }
        user.setRole("ROLE_" + role.toUpperCase());

        MemberDocument savedUser = memberRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenService.generateAccessToken(savedUser.getEmail(), savedUser.getRole());
        String refreshToken = jwtTokenService.generateRefreshToken(savedUser.getEmail());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenService.getAccessTokenExpiration(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtTokenService.extractUsername(refreshToken);
        MemberDocument user = memberRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtTokenService.generateAccessToken(user.getEmail(), user.getRole());
        String newRefreshToken = jwtTokenService.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtTokenService.getAccessTokenExpiration(),
                user.getEmail(),
                user.getRole()
        );
    }
} 