package com.example.kitchensink.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for RefreshTokenRequest model.
 * Tests validation annotations and Lombok functionality.
 */
class RefreshTokenRequestTest {

    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        refreshTokenRequest = new RefreshTokenRequest();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(refreshTokenRequest);
        assertNull(refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testAllArgsConstructor() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token_123");
        assertEquals("refresh_token_123", request.getRefreshToken());
    }

    @Test
    void testSettersAndGetters() {
        refreshTokenRequest.setRefreshToken("refresh_token_456");
        assertEquals("refresh_token_456", refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testEqualsAndHashCode() {
        RefreshTokenRequest request1 = new RefreshTokenRequest("refresh_token_123");
        RefreshTokenRequest request2 = new RefreshTokenRequest("refresh_token_123");
        RefreshTokenRequest request3 = new RefreshTokenRequest("different_token");

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token_123");
        String toString = request.toString();

        assertTrue(toString.contains("refresh_token_123"));
        assertTrue(toString.contains("RefreshTokenRequest"));
    }

    @Test
    void testEqualsWithNull() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token_123");
        assertNotEquals(null, request);
    }

    @Test
    void testEqualsWithDifferentClass() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token_123");
        assertNotEquals(request, "string");
    }

    @Test
    void testEqualsWithSameObject() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token_123");
        assertEquals(request, request);
    }

    @Test
    void testNullValue() {
        RefreshTokenRequest request = new RefreshTokenRequest(null);
        assertNull(request.getRefreshToken());
    }

    @Test
    void testEmptyStringValue() {
        RefreshTokenRequest request = new RefreshTokenRequest("");
        assertEquals("", request.getRefreshToken());
    }

    @Test
    void testLongTokenValue() {
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        RefreshTokenRequest request = new RefreshTokenRequest(longToken);
        assertEquals(longToken, request.getRefreshToken());
    }
} 