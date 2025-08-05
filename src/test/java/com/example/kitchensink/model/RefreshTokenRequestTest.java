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

    @Test
    void testEqualsWithNullFields() {
        RefreshTokenRequest request1 = new RefreshTokenRequest(null);
        RefreshTokenRequest request2 = new RefreshTokenRequest(null);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEqualsWithDifferentNullFields() {
        RefreshTokenRequest request1 = new RefreshTokenRequest("token123");
        RefreshTokenRequest request2 = new RefreshTokenRequest(null);

        assertNotEquals(request1, request2);
    }

    @Test
    void testHashCodeConsistency() {
        RefreshTokenRequest request = new RefreshTokenRequest("refresh_token_123");

        assertEquals(request.hashCode(), request.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        assertNotNull(request.hashCode());
    }

    @Test
    void testToStringWithNullValues() {
        RefreshTokenRequest request = new RefreshTokenRequest(null);
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("RefreshTokenRequest"));
    }

    @Test
    void testToStringWithEmptyValues() {
        RefreshTokenRequest request = new RefreshTokenRequest("");
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("RefreshTokenRequest"));
    }

    @Test
    void testAllArgsConstructorWithNullValues() {
        RefreshTokenRequest request = new RefreshTokenRequest(null);

        assertNull(request.getRefreshToken());
    }

    @Test
    void testAllArgsConstructorWithEmptyValues() {
        RefreshTokenRequest request = new RefreshTokenRequest("");

        assertEquals("", request.getRefreshToken());
    }

    @Test
    void testSettersWithNullValues() {
        refreshTokenRequest.setRefreshToken(null);

        assertNull(refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testSettersWithEmptyValues() {
        refreshTokenRequest.setRefreshToken("");

        assertEquals("", refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testSpecialCharactersInToken() {
        String specialToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        refreshTokenRequest.setRefreshToken(specialToken);

        assertEquals(specialToken, refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testUnicodeCharactersInToken() {
        String unicodeToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiLkuK3mlociLCJuYW1lIjoi5LiA5Liq5Lq6IiwiaWF0IjoxNTE2MjM5MDIyfQ.test";
        refreshTokenRequest.setRefreshToken(unicodeToken);

        assertEquals(unicodeToken, refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testVeryLongToken() {
        String veryLongToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + 
                              "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyNDI2MjJ9." +
                              "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c_very_long_token_with_many_characters";
        refreshTokenRequest.setRefreshToken(veryLongToken);

        assertEquals(veryLongToken, refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testTokenWithSpecialCharacters() {
        String specialCharToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c!@#$%^&*()";
        refreshTokenRequest.setRefreshToken(specialCharToken);

        assertEquals(specialCharToken, refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testTokenWithSpaces() {
        String tokenWithSpaces = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c ";
        refreshTokenRequest.setRefreshToken(tokenWithSpaces);

        assertEquals(tokenWithSpaces, refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testTokenWithNewlines() {
        String tokenWithNewlines = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.\neyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.\nSflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        refreshTokenRequest.setRefreshToken(tokenWithNewlines);

        assertEquals(tokenWithNewlines, refreshTokenRequest.getRefreshToken());
    }

    @Test
    void testTokenWithTabs() {
        String tokenWithTabs = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.\teyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.\tSflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        refreshTokenRequest.setRefreshToken(tokenWithTabs);

        assertEquals(tokenWithTabs, refreshTokenRequest.getRefreshToken());
    }
} 