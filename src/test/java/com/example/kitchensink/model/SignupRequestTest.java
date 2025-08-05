package com.example.kitchensink.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SignupRequest model.
 * Tests validation annotations and Lombok functionality.
 */
class SignupRequestTest {

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(signupRequest);
        assertNull(signupRequest.getName());
        assertNull(signupRequest.getEmail());
        assertNull(signupRequest.getPassword());
        assertNull(signupRequest.getPhoneNumber());
        assertEquals("USER", signupRequest.getRole()); // Default value
    }

    @Test
    void testAllArgsConstructor() {
        SignupRequest request = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "ADMIN");
        assertEquals("John Doe", request.getName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("Password123!", request.getPassword());
        assertEquals("1234567890", request.getPhoneNumber());
        assertEquals("ADMIN", request.getRole());
    }

    @Test
    void testSettersAndGetters() {
        signupRequest.setName("John Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setPhoneNumber("1234567890");
        signupRequest.setRole("ADMIN");

        assertEquals("John Doe", signupRequest.getName());
        assertEquals("john@example.com", signupRequest.getEmail());
        assertEquals("Password123!", signupRequest.getPassword());
        assertEquals("1234567890", signupRequest.getPhoneNumber());
        assertEquals("ADMIN", signupRequest.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        SignupRequest request1 = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "USER");
        SignupRequest request2 = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "USER");
        SignupRequest request3 = new SignupRequest("Jane Doe", "john@example.com", "Password123!", "1234567890", "USER");

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        SignupRequest request = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "USER");
        String toString = request.toString();

        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("Password123!"));
        assertTrue(toString.contains("1234567890"));
        assertTrue(toString.contains("USER"));
        assertTrue(toString.contains("SignupRequest"));
    }

    @Test
    void testEqualsWithNull() {
        SignupRequest request = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "USER");
        assertNotEquals(null, request);
    }

    @Test
    void testEqualsWithDifferentClass() {
        SignupRequest request = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "USER");
        assertNotEquals(request, "string");
    }

    @Test
    void testEqualsWithSameObject() {
        SignupRequest request = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "USER");
        assertEquals(request, request);
    }

    @Test
    void testDefaultRole() {
        SignupRequest request = new SignupRequest();
        assertEquals("USER", request.getRole());
    }

    @Test
    void testNullValues() {
        SignupRequest request = new SignupRequest(null, null, null, null, null);
        assertNull(request.getName());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getPhoneNumber());
        assertNull(request.getRole());
    }

    @Test
    void testEmptyStringValues() {
        SignupRequest request = new SignupRequest("", "", "", "", "");
        assertEquals("", request.getName());
        assertEquals("", request.getEmail());
        assertEquals("", request.getPassword());
        assertEquals("", request.getPhoneNumber());
        assertEquals("", request.getRole());
    }
} 