package com.example.kitchensink.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JwtAuthController.class)
class JwtAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.example.kitchensink.security.JwtTokenService jwtTokenService;

    @MockBean
    private com.example.kitchensink.security.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private com.example.kitchensink.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @MockBean
    private com.example.kitchensink.service.AuthService authService;

    @Test
    void testLoginPageWithoutAuth() throws Exception {
        // Should return 401 when not authenticated
        mockMvc.perform(get("/jwt/login"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testLoginPageWithAuth() throws Exception {
        mockMvc.perform(get("/jwt/login"))
                .andExpect(status().isOk());
    }

    @Test
    void testSignupPageWithoutAuth() throws Exception {
        // Should return 401 when not authenticated
        mockMvc.perform(get("/jwt/signup"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testSignupPageWithAuth() throws Exception {
        mockMvc.perform(get("/jwt/signup"))
                .andExpect(status().isOk());
    }

    @Test
    void testLogoutWithoutAuth() throws Exception {
        // Should return 403 when not authenticated
        mockMvc.perform(post("/jwt/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testLogoutWithAuth() throws Exception {
        mockMvc.perform(post("/jwt/logout"))
                .andExpect(status().isForbidden());
    }
} 