package com.example.kitchensink.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AuthResponse model.
 * Tests builder pattern, static factory method, and Lombok functionality.
 */
class AuthResponseTest {

    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        authResponse = new AuthResponse();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(authResponse);
        assertNull(authResponse.getAccessToken());
        assertNull(authResponse.getRefreshToken());
        assertNull(authResponse.getTokenType());
        assertNull(authResponse.getExpiresIn());
        assertNull(authResponse.getEmail());
        assertNull(authResponse.getRole());
    }

    @Test
    void testAllArgsConstructor() {
        AuthResponse response = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");
        assertEquals("access123", response.getAccessToken());
        assertEquals("refresh456", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testBuilder() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access123")
                .refreshToken("refresh456")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .email("user@example.com")
                .role("USER")
                .build();

        assertEquals("access123", response.getAccessToken());
        assertEquals("refresh456", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testStaticFactoryMethod() {
        AuthResponse response = AuthResponse.of("access123", "refresh456", 3600L, "user@example.com", "USER");

        assertEquals("access123", response.getAccessToken());
        assertEquals("refresh456", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType()); // Default value
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testSettersAndGetters() {
        authResponse.setAccessToken("access123");
        authResponse.setRefreshToken("refresh456");
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(3600L);
        authResponse.setEmail("user@example.com");
        authResponse.setRole("USER");

        assertEquals("access123", authResponse.getAccessToken());
        assertEquals("refresh456", authResponse.getRefreshToken());
        assertEquals("Bearer", authResponse.getTokenType());
        assertEquals(3600L, authResponse.getExpiresIn());
        assertEquals("user@example.com", authResponse.getEmail());
        assertEquals("USER", authResponse.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthResponse response1 = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");
        AuthResponse response2 = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");
        AuthResponse response3 = new AuthResponse("different", "refresh456", "Bearer", 3600L, "user@example.com", "USER");

        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        AuthResponse response = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");
        String toString = response.toString();

        assertTrue(toString.contains("access123"));
        assertTrue(toString.contains("refresh456"));
        assertTrue(toString.contains("Bearer"));
        assertTrue(toString.contains("user@example.com"));
        assertTrue(toString.contains("USER"));
        assertTrue(toString.contains("AuthResponse"));
    }

    @Test
    void testEqualsWithNull() {
        AuthResponse response = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");
        assertNotEquals(null, response);
    }

    @Test
    void testEqualsWithDifferentClass() {
        AuthResponse response = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");
        assertNotEquals(response, "string");
    }

    @Test
    void testEqualsWithSameObject() {
        AuthResponse response = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");
        assertEquals(response, response);
    }

    @Test
    void testBuilderWithPartialValues() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access123")
                .email("user@example.com")
                .build();

        assertEquals("access123", response.getAccessToken());
        assertEquals("user@example.com", response.getEmail());
        assertNull(response.getRefreshToken());
        assertNull(response.getTokenType());
        assertNull(response.getExpiresIn());
        assertNull(response.getRole());
    }

    @Test
    void testBuilderWithNullValues() {
        AuthResponse response = AuthResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .tokenType(null)
                .expiresIn(null)
                .email(null)
                .role(null)
                .build();

        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getTokenType());
        assertNull(response.getExpiresIn());
        assertNull(response.getEmail());
        assertNull(response.getRole());
    }

    @Test
    void testBuilderWithEmptyValues() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("")
                .refreshToken("")
                .tokenType("")
                .expiresIn(0L)
                .email("")
                .role("")
                .build();

        assertEquals("", response.getAccessToken());
        assertEquals("", response.getRefreshToken());
        assertEquals("", response.getTokenType());
        assertEquals(0L, response.getExpiresIn());
        assertEquals("", response.getEmail());
        assertEquals("", response.getRole());
    }

    @Test
    void testStaticFactoryMethodWithNullValues() {
        AuthResponse response = AuthResponse.of(null, null, null, null, null);

        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType()); // Default value
        assertNull(response.getExpiresIn());
        assertNull(response.getEmail());
        assertNull(response.getRole());
    }

    @Test
    void testStaticFactoryMethodWithEmptyValues() {
        AuthResponse response = AuthResponse.of("", "", 0L, "", "");

        assertEquals("", response.getAccessToken());
        assertEquals("", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType()); // Default value
        assertEquals(0L, response.getExpiresIn());
        assertEquals("", response.getEmail());
        assertEquals("", response.getRole());
    }

    @Test
    void testEqualsWithNullFields() {
        AuthResponse response1 = new AuthResponse(null, null, null, null, null, null);
        AuthResponse response2 = new AuthResponse(null, null, null, null, null, null);

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testEqualsWithDifferentNullFields() {
        AuthResponse response1 = new AuthResponse("access123", null, "Bearer", 3600L, "user@example.com", "USER");
        AuthResponse response2 = new AuthResponse("access123", "refresh456", null, 3600L, "user@example.com", "USER");

        assertNotEquals(response1, response2);
    }

    @Test
    void testHashCodeConsistency() {
        AuthResponse response = new AuthResponse("access123", "refresh456", "Bearer", 3600L, "user@example.com", "USER");

        assertEquals(response.hashCode(), response.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        AuthResponse response = new AuthResponse(null, null, null, null, null, null);

        assertNotNull(response.hashCode());
    }

    @Test
    void testToStringWithNullValues() {
        AuthResponse response = new AuthResponse(null, null, null, null, null, null);
        String toString = response.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("AuthResponse"));
    }

    @Test
    void testToStringWithEmptyValues() {
        AuthResponse response = new AuthResponse("", "", "", 0L, "", "");
        String toString = response.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("AuthResponse"));
    }

    @Test
    void testBuilderChaining() {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access123")
                .refreshToken("refresh456")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .email("user@example.com")
                .role("USER")
                .build();

        assertEquals("access123", response.getAccessToken());
        assertEquals("refresh456", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("user@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void testAllArgsConstructorWithNullValues() {
        AuthResponse response = new AuthResponse(null, null, null, null, null, null);

        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
        assertNull(response.getTokenType());
        assertNull(response.getExpiresIn());
        assertNull(response.getEmail());
        assertNull(response.getRole());
    }

    @Test
    void testAllArgsConstructorWithEmptyValues() {
        AuthResponse response = new AuthResponse("", "", "", 0L, "", "");

        assertEquals("", response.getAccessToken());
        assertEquals("", response.getRefreshToken());
        assertEquals("", response.getTokenType());
        assertEquals(0L, response.getExpiresIn());
        assertEquals("", response.getEmail());
        assertEquals("", response.getRole());
    }
} 