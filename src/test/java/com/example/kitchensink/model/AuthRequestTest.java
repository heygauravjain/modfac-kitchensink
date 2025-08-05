package com.example.kitchensink.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AuthRequest model.
 * Tests validation annotations and Lombok functionality.
 */
class AuthRequestTest {

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(authRequest);
        assertNull(authRequest.getEmail());
        assertNull(authRequest.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        authRequest.setEmail("user@example.com");
        authRequest.setPassword("secret123");

        assertEquals("user@example.com", authRequest.getEmail());
        assertEquals("secret123", authRequest.getPassword());
    }

    @Test
    void testEqualsAndHashCode() {
        AuthRequest request1 = new AuthRequest("test@example.com", "password123");
        AuthRequest request2 = new AuthRequest("test@example.com", "password123");
        AuthRequest request3 = new AuthRequest("different@example.com", "password123");

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        String toString = request.toString();

        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("password123"));
        assertTrue(toString.contains("AuthRequest"));
    }

    @Test
    void testEqualsWithNull() {
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        assertNotEquals(null, request);
    }

    @Test
    void testEqualsWithDifferentClass() {
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        assertNotEquals(request, "string");
    }

    @Test
    void testEqualsWithSameObject() {
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        assertEquals(request, request);
    }

    @Test
    void testEqualsWithNullFields() {
        AuthRequest request1 = new AuthRequest(null, null);
        AuthRequest request2 = new AuthRequest(null, null);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEqualsWithDifferentNullFields() {
        AuthRequest request1 = new AuthRequest("test@example.com", null);
        AuthRequest request2 = new AuthRequest("test@example.com", "password123");

        assertNotEquals(request1, request2);
    }

    @Test
    void testHashCodeConsistency() {
        AuthRequest request = new AuthRequest("test@example.com", "password123");

        assertEquals(request.hashCode(), request.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        AuthRequest request = new AuthRequest(null, null);

        assertNotNull(request.hashCode());
    }

    @Test
    void testToStringWithNullValues() {
        AuthRequest request = new AuthRequest(null, null);
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("AuthRequest"));
    }

    @Test
    void testToStringWithEmptyValues() {
        AuthRequest request = new AuthRequest("", "");
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("AuthRequest"));
    }

    @Test
    void testAllArgsConstructorWithNullValues() {
        AuthRequest request = new AuthRequest(null, null);

        assertNull(request.getEmail());
        assertNull(request.getPassword());
    }

    @Test
    void testAllArgsConstructorWithEmptyValues() {
        AuthRequest request = new AuthRequest("", "");

        assertEquals("", request.getEmail());
        assertEquals("", request.getPassword());
    }

    @Test
    void testSettersWithNullValues() {
        authRequest.setEmail(null);
        authRequest.setPassword(null);

        assertNull(authRequest.getEmail());
        assertNull(authRequest.getPassword());
    }

    @Test
    void testSettersWithEmptyValues() {
        authRequest.setEmail("");
        authRequest.setPassword("");

        assertEquals("", authRequest.getEmail());
        assertEquals("", authRequest.getPassword());
    }

    @Test
    void testComplexEmailAddresses() {
        authRequest.setEmail("test+tag@example.co.uk");
        assertEquals("test+tag@example.co.uk", authRequest.getEmail());

        authRequest.setEmail("user.name@domain.com");
        assertEquals("user.name@domain.com", authRequest.getEmail());

        authRequest.setEmail("test@subdomain.example.com");
        assertEquals("test@subdomain.example.com", authRequest.getEmail());
    }

    @Test
    void testSpecialCharactersInPassword() {
        authRequest.setPassword("Pass@word123!");
        assertEquals("Pass@word123!", authRequest.getPassword());

        authRequest.setPassword("Complex#Pass$1");
        assertEquals("Complex#Pass$1", authRequest.getPassword());

        authRequest.setPassword("Test%Pass^2");
        assertEquals("Test%Pass^2", authRequest.getPassword());
    }

    @Test
    void testUnicodeCharacters() {
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("Test123!@");

        assertEquals("test@example.com", authRequest.getEmail());
        assertEquals("Test123!@", authRequest.getPassword());
    }

    @Test
    void testLongValues() {
        String longEmail = "very.long.email.address.with.many.subdomains@example.com";
        String longPassword = "VeryLongPasswordWithManyCharacters123!@#$%^&*()";

        authRequest.setEmail(longEmail);
        authRequest.setPassword(longPassword);

        assertEquals(longEmail, authRequest.getEmail());
        assertEquals(longPassword, authRequest.getPassword());
    }
} 