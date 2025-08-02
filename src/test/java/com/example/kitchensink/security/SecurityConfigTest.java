package com.example.kitchensink.security;

import com.example.kitchensink.config.TestSecurityConfig;
import com.example.kitchensink.config.TestApplicationConfig;
import com.example.kitchensink.service.AuthService;
import com.example.kitchensink.security.JwtAuthenticationFilter;
import com.example.kitchensink.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestApplicationConfig.class})
class SecurityConfigTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AuthService authService;

    @Test
    void publicEndpoints_ShouldBeAccessible() throws Exception {
        // Test public endpoints
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.getForEntity("/jwt-login", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.getForEntity("/jwt-login", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = restTemplate.getForEntity("/jwt-signup", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void contextLoads() {
        // Simple test to verify the application context loads
        assertTrue(true);
    }
} 