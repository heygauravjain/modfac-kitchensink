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
} 