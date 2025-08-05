package com.example.kitchensink.service;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.model.AuthResponse;
import com.example.kitchensink.model.SignupRequest;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private MemberDocument memberDocument;

    @BeforeEach
    void setUp() {
        // Updated to match current SignupRequest model with phoneNumber field
        signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("Password123!"); // Updated to meet new password requirements
        signupRequest.setRole("USER");
        signupRequest.setPhoneNumber("1234567890"); // Optional field
        
        memberDocument = new MemberDocument();
        memberDocument.setId("1");
        memberDocument.setName("Test User");
        memberDocument.setEmail("test@example.com");
        memberDocument.setPassword("encodedPassword");
        memberDocument.setRole("ROLE_USER");
        memberDocument.setPhoneNumber("1234567890");
    }

    @Test
    void registerUser_WithValidData_ShouldReturnAuthResponse() {
        // Given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900000L, response.getExpiresIn());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());

        verify(memberRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("Password123!");
        verify(memberRepository).save(any(MemberDocument.class));
        verify(jwtTokenService).generateAccessToken("test@example.com", "ROLE_USER");
        verify(jwtTokenService).generateRefreshToken("test@example.com");
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Given
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(memberDocument));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.registerUser(signupRequest));
        assertEquals("User with this email already exists", exception.getMessage());

        verify(memberRepository).findByEmail("test@example.com");
        verify(memberRepository, never()).save(any(MemberDocument.class));
    }

    @Test
    void registerUser_WithAdminRole_ShouldSetCorrectRole() {
        // Given
        signupRequest.setRole("ADMIN");
        signupRequest.setEmail("admin@example.com");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        // Create admin member document
        MemberDocument adminMember = new MemberDocument();
        adminMember.setId("2");
        adminMember.setName("Admin User");
        adminMember.setEmail("admin@example.com");
        adminMember.setPassword("encodedPassword");
        adminMember.setRole("ROLE_ADMIN");
        adminMember.setPhoneNumber("1234567890");
        
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(adminMember);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertEquals("ROLE_ADMIN", response.getRole());
        verify(jwtTokenService).generateAccessToken("admin@example.com", "ROLE_ADMIN");
    }

    @Test
    void registerUser_WithNullRole_ShouldSetDefaultUserRole() {
        // Given
        signupRequest.setRole(null);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertEquals("ROLE_USER", response.getRole());
        verify(jwtTokenService).generateAccessToken("test@example.com", "ROLE_USER");
    }

    @Test
    void registerUser_WithEmptyRole_ShouldSetDefaultUserRole() {
        // Given
        signupRequest.setRole("");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertEquals("ROLE_USER", response.getRole());
        verify(jwtTokenService).generateAccessToken("test@example.com", "ROLE_USER");
    }

    @Test
    void registerUser_WithLowerCaseRole_ShouldConvertToUpperCase() {
        // Given
        signupRequest.setRole("user");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertEquals("ROLE_USER", response.getRole());
        verify(jwtTokenService).generateAccessToken("test@example.com", "ROLE_USER");
    }

    @Test
    void registerUser_WithMixedCaseRole_ShouldConvertToUpperCase() {
        // Given
        signupRequest.setRole("AdMiN");
        signupRequest.setEmail("admin@example.com");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        
        MemberDocument adminMember = new MemberDocument();
        adminMember.setId("2");
        adminMember.setName("Admin User");
        adminMember.setEmail("admin@example.com");
        adminMember.setPassword("encodedPassword");
        adminMember.setRole("ROLE_ADMIN");
        adminMember.setPhoneNumber("1234567890");
        
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(adminMember);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertEquals("ROLE_ADMIN", response.getRole());
        verify(jwtTokenService).generateAccessToken("admin@example.com", "ROLE_ADMIN");
    }

    @Test
    void registerUser_WithoutPhoneNumber_ShouldRegisterSuccessfully() {
        // Given
        signupRequest.setPhoneNumber(null);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertNotNull(response);
        assertEquals("ROLE_USER", response.getRole());
        verify(memberRepository).save(any(MemberDocument.class));
    }

    @Test
    void registerUser_WithEmptyPhoneNumber_ShouldRegisterSuccessfully() {
        // Given
        signupRequest.setPhoneNumber("");
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertNotNull(response);
        assertEquals("ROLE_USER", response.getRole());
        verify(memberRepository).save(any(MemberDocument.class));
    }

    @Test
    void registerUser_WithNullName_ShouldRegisterSuccessfully() {
        // Given
        signupRequest.setName(null);
        when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(MemberDocument.class))).thenReturn(memberDocument);
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("accessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.registerUser(signupRequest);

        // Then
        assertNotNull(response);
        assertEquals("ROLE_USER", response.getRole());
        verify(memberRepository).save(any(MemberDocument.class));
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewTokens() {
        // Given
        String refreshToken = "validRefreshToken";
        when(jwtTokenService.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtTokenService.extractUsername(refreshToken)).thenReturn("test@example.com");
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(memberDocument));
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("newAccessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("newRefreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("newAccessToken", response.getAccessToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900000L, response.getExpiresIn());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());

        verify(jwtTokenService).isTokenValid(refreshToken);
        verify(jwtTokenService).extractUsername(refreshToken);
        verify(memberRepository).findByEmail("test@example.com");
        verify(jwtTokenService).generateAccessToken("test@example.com", "ROLE_USER");
        verify(jwtTokenService).generateRefreshToken("test@example.com");
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldThrowException() {
        // Given
        String refreshToken = "invalidRefreshToken";
        when(jwtTokenService.isTokenValid(refreshToken)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("Invalid refresh token", exception.getMessage());

        verify(jwtTokenService).isTokenValid(refreshToken);
        verify(memberRepository, never()).findByEmail(anyString());
    }

    @Test
    void refreshToken_WithValidTokenButUserNotFound_ShouldThrowException() {
        // Given
        String refreshToken = "validRefreshToken";
        when(jwtTokenService.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtTokenService.extractUsername(refreshToken)).thenReturn("test@example.com");
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("User not found", exception.getMessage());

        verify(jwtTokenService).isTokenValid(refreshToken);
        verify(jwtTokenService).extractUsername(refreshToken);
        verify(memberRepository).findByEmail("test@example.com");
    }

    @Test
    void refreshToken_WithNullToken_ShouldThrowException() {
        // Given
        String refreshToken = null;
        when(jwtTokenService.isTokenValid(refreshToken)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("Invalid refresh token", exception.getMessage());

        verify(jwtTokenService).isTokenValid(refreshToken);
        verify(memberRepository, never()).findByEmail(anyString());
    }

    @Test
    void refreshToken_WithEmptyToken_ShouldThrowException() {
        // Given
        String refreshToken = "";
        when(jwtTokenService.isTokenValid(refreshToken)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.refreshToken(refreshToken));
        assertEquals("Invalid refresh token", exception.getMessage());

        verify(jwtTokenService).isTokenValid(refreshToken);
        verify(memberRepository, never()).findByEmail(anyString());
    }

    @Test
    void refreshToken_WithAdminUser_ShouldReturnAdminRole() {
        // Given
        String refreshToken = "validRefreshToken";
        MemberDocument adminMember = new MemberDocument();
        adminMember.setId("2");
        adminMember.setName("Admin User");
        adminMember.setEmail("admin@example.com");
        adminMember.setPassword("encodedPassword");
        adminMember.setRole("ROLE_ADMIN");
        adminMember.setPhoneNumber("1234567890");
        
        when(jwtTokenService.isTokenValid(refreshToken)).thenReturn(true);
        when(jwtTokenService.extractUsername(refreshToken)).thenReturn("admin@example.com");
        when(memberRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminMember));
        when(jwtTokenService.generateAccessToken(anyString(), anyString())).thenReturn("newAccessToken");
        when(jwtTokenService.generateRefreshToken(anyString())).thenReturn("newRefreshToken");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900000L);

        // When
        AuthResponse response = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("ROLE_ADMIN", response.getRole());
        verify(jwtTokenService).generateAccessToken("admin@example.com", "ROLE_ADMIN");
    }
} 