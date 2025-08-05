package com.example.kitchensink.controller;

import com.example.kitchensink.model.AuthRequest;
import com.example.kitchensink.model.AuthResponse;
import com.example.kitchensink.model.SignupRequest;
import com.example.kitchensink.security.JwtTokenService;
import com.example.kitchensink.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class JwtAuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private AuthService authService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @InjectMocks
    private JwtAuthController jwtAuthController;

    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        userDetails = new User("test@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void showJwtLoginPage_WithLogoutMessage_ShouldAddSuccessAttribute() {
        // Given
        String message = "logout_success";

        // When
        String viewName = jwtAuthController.showJwtLoginPage(message, model);

        // Then
        assertEquals("jwt-login", viewName);
        verify(model).addAttribute("success", "Successfully logged out!");
    }

    @Test
    void showJwtLoginPage_WithoutMessage_ShouldNotAddSuccessAttribute() {
        // Given
        String message = null;

        // When
        String viewName = jwtAuthController.showJwtLoginPage(message, model);

        // Then
        assertEquals("jwt-login", viewName);
        verify(model, never()).addAttribute(anyString(), anyString());
    }

    @Test
    void showJwtLoginPage_WithOtherMessage_ShouldNotAddSuccessAttribute() {
        // Given
        String message = "other_message";

        // When
        String viewName = jwtAuthController.showJwtLoginPage(message, model);

        // Then
        assertEquals("jwt-login", viewName);
        verify(model, never()).addAttribute(anyString(), anyString());
    }

    @Test
    void showJwtSignupPage_ShouldReturnSignupPage() {
        // When
        String viewName = jwtAuthController.showJwtSignupPage();

        // Then
        assertEquals("jwt-signup", viewName);
    }

    @Test
    void jwtLogin_Success_WithUserRole_ShouldRedirectToUserProfile() {
        // Given
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenService.generateAccessToken(anyString(), anyString()))
                .thenReturn("access_token_123");
        when(jwtTokenService.generateRefreshToken(anyString()))
                .thenReturn("refresh_token_456");

        // When
        String viewName = jwtAuthController.jwtLogin(request, redirectAttributes, session);

        // Then
        assertEquals("redirect:/user-profile", viewName);
        verify(session).setAttribute("accessToken", "access_token_123");
        verify(session).setAttribute("refreshToken", "refresh_token_456");
        verify(session).setAttribute("userEmail", "test@example.com");
        verify(session).setAttribute("userRole", "ROLE_USER");
        verify(redirectAttributes).addFlashAttribute("accessToken", "access_token_123");
        verify(redirectAttributes).addFlashAttribute("refreshToken", "refresh_token_456");
        verify(redirectAttributes).addFlashAttribute("userEmail", "test@example.com");
        verify(redirectAttributes).addFlashAttribute("userRole", "ROLE_USER");
        verify(session).setAttribute(eq("loginTime"), any(Long.class));
    }

    @Test
    void jwtLogin_Success_WithAdminRole_ShouldRedirectToAdminHome() {
        // Given
        AuthRequest request = new AuthRequest("admin@example.com", "password123");
        UserDetails adminUserDetails = new User("admin@example.com", "password", 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(adminUserDetails, null, adminUserDetails.getAuthorities());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(adminAuth);
        when(jwtTokenService.generateAccessToken(anyString(), anyString()))
                .thenReturn("admin_access_token");
        when(jwtTokenService.generateRefreshToken(anyString()))
                .thenReturn("admin_refresh_token");

        // When
        String viewName = jwtAuthController.jwtLogin(request, redirectAttributes, session);

        // Then
        assertEquals("redirect:/admin/home", viewName);
        verify(session).setAttribute("userRole", "ROLE_ADMIN");
        verify(redirectAttributes).addFlashAttribute("userRole", "ROLE_ADMIN");
    }

    @Test
    void jwtLogin_Success_WithNoAuthorities_ShouldUseDefaultRole() {
        // Given
        AuthRequest request = new AuthRequest("user@example.com", "password123");
        UserDetails userWithoutAuthorities = new User("user@example.com", "password", Collections.emptyList());
        Authentication authWithoutAuthorities = new UsernamePasswordAuthenticationToken(userWithoutAuthorities, null, Collections.emptyList());
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authWithoutAuthorities);
        when(jwtTokenService.generateAccessToken(anyString(), anyString()))
                .thenReturn("access_token_123");
        when(jwtTokenService.generateRefreshToken(anyString()))
                .thenReturn("refresh_token_456");

        // When
        String viewName = jwtAuthController.jwtLogin(request, redirectAttributes, session);

        // Then
        assertEquals("redirect:/user-profile", viewName);
        verify(session).setAttribute("userRole", "ROLE_USER");
        verify(redirectAttributes).addFlashAttribute("userRole", "ROLE_USER");
    }

    @Test
    void jwtLogin_AuthenticationFailure_ShouldRedirectToLoginWithError() {
        // Given
        AuthRequest request = new AuthRequest("test@example.com", "wrongpassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When
        String viewName = jwtAuthController.jwtLogin(request, redirectAttributes, session);

        // Then
        assertEquals("redirect:/jwt-login", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "Invalid email or password");
        verify(session, never()).setAttribute(anyString(), anyString());
    }

    @Test
    void jwtSignup_Success_ShouldRedirectToLoginWithSuccess() {
        // Given
        SignupRequest request = new SignupRequest("John Doe", "test@example.com", "Password123!", "1234567890", "USER");
        AuthResponse authResponse = AuthResponse.of("access_token_123", "refresh_token_456", 3600L, "test@example.com", "USER");
        
        when(authService.registerUser(request)).thenReturn(authResponse);

        // When
        String viewName = jwtAuthController.jwtSignup(request, redirectAttributes);

        // Then
        assertEquals("redirect:/jwt-login", viewName);
        verify(redirectAttributes).addFlashAttribute("accessToken", "access_token_123");
        verify(redirectAttributes).addFlashAttribute("refreshToken", "refresh_token_456");
        verify(redirectAttributes).addFlashAttribute("userEmail", "test@example.com");
        verify(redirectAttributes).addFlashAttribute("userRole", "USER");
        verify(redirectAttributes).addFlashAttribute("success", "Registration successful! Please login with your credentials.");
    }

    @Test
    void jwtSignup_WithException_ShouldRedirectToSignupWithError() {
        // Given
        SignupRequest request = new SignupRequest("John Doe", "test@example.com", "Password123!", "1234567890", "USER");
        when(authService.registerUser(request)).thenThrow(new RuntimeException("User already exists"));

        // When
        String viewName = jwtAuthController.jwtSignup(request, redirectAttributes);

        // Then
        assertEquals("redirect:/jwt-signup", viewName);
        verify(redirectAttributes).addFlashAttribute("error", "User already exists");
        verify(redirectAttributes, never()).addFlashAttribute(eq("success"), anyString());
    }

    @Test
    void logout_ShouldRedirectToJwtLogout() {
        // When
        String viewName = jwtAuthController.logout();

        // Then
        assertEquals("redirect:/jwt-logout", viewName);
    }

    @Test
    void jwtLogoutGet_ShouldSetCacheHeadersAndRedirect() {
        // When
        String viewName = jwtAuthController.jwtLogoutGet(response);

        // Then
        assertEquals("redirect:/jwt-login", viewName);
        verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        verify(response).setHeader("Pragma", "no-cache");
        verify(response).setHeader("Expires", "0");
    }

    @Test
    void jwtLogoutPost_ShouldSetCacheHeadersAndRedirect() {
        // When
        String viewName = jwtAuthController.jwtLogout(response);

        // Then
        assertEquals("redirect:/jwt-login", viewName);
        verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        verify(response).setHeader("Pragma", "no-cache");
        verify(response).setHeader("Expires", "0");
    }
} 