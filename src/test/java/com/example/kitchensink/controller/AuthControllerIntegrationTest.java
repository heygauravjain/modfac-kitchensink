package com.example.kitchensink.controller;

import com.example.kitchensink.config.TestSecurityConfig;
import com.example.kitchensink.config.TestApplicationConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestApplicationConfig.class})
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private com.example.kitchensink.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.example.kitchensink.security.CustomUserDetailsService customUserDetailsService;

    @MockBean
    private com.example.kitchensink.service.AuthService authService;

    @Test
    void contextLoads() {
        // Simple test to verify the application context loads
        assertTrue(true);
    }

    @Test
    void authEndpoints_ShouldBeAccessible() throws Exception {
        // Test that auth endpoints are accessible (they should be permitted in security config)
        ResponseEntity<String> response = restTemplate.getForEntity("/api/auth/login", String.class);
        // Should be accessible (might return 400 for missing body, but not 403/401)
        assertTrue(response.getStatusCode() != HttpStatus.FORBIDDEN && 
                  response.getStatusCode() != HttpStatus.UNAUTHORIZED);

        response = restTemplate.getForEntity("/api/auth/signup", String.class);
        assertTrue(response.getStatusCode() != HttpStatus.FORBIDDEN && 
                  response.getStatusCode() != HttpStatus.UNAUTHORIZED);
    }
} 