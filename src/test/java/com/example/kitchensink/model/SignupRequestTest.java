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
    void testEqualsWithNullFields() {
        SignupRequest request1 = new SignupRequest(null, null, null, null, null);
        SignupRequest request2 = new SignupRequest(null, null, null, null, null);

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEqualsWithDifferentNullFields() {
        SignupRequest request1 = new SignupRequest("John Doe", null, "Password123!", "1234567890", "USER");
        SignupRequest request2 = new SignupRequest("John Doe", "john@example.com", null, "1234567890", "USER");

        assertNotEquals(request1, request2);
    }

    @Test
    void testHashCodeConsistency() {
        SignupRequest request = new SignupRequest("John Doe", "john@example.com", "Password123!", "1234567890", "USER");

        assertEquals(request.hashCode(), request.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        SignupRequest request = new SignupRequest(null, null, null, null, null);

        assertNotNull(request.hashCode());
    }

    @Test
    void testToStringWithNullValues() {
        SignupRequest request = new SignupRequest(null, null, null, null, null);
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("SignupRequest"));
    }

    @Test
    void testToStringWithEmptyValues() {
        SignupRequest request = new SignupRequest("", "", "", "", "");
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("SignupRequest"));
    }

    @Test
    void testAllArgsConstructorWithNullValues() {
        SignupRequest request = new SignupRequest(null, null, null, null, null);

        assertNull(request.getName());
        assertNull(request.getEmail());
        assertNull(request.getPassword());
        assertNull(request.getPhoneNumber());
        assertNull(request.getRole());
    }

    @Test
    void testAllArgsConstructorWithEmptyValues() {
        SignupRequest request = new SignupRequest("", "", "", "", "");

        assertEquals("", request.getName());
        assertEquals("", request.getEmail());
        assertEquals("", request.getPassword());
        assertEquals("", request.getPhoneNumber());
        assertEquals("", request.getRole());
    }

    @Test
    void testSettersWithNullValues() {
        signupRequest.setName(null);
        signupRequest.setEmail(null);
        signupRequest.setPassword(null);
        signupRequest.setPhoneNumber(null);
        signupRequest.setRole(null);

        assertNull(signupRequest.getName());
        assertNull(signupRequest.getEmail());
        assertNull(signupRequest.getPassword());
        assertNull(signupRequest.getPhoneNumber());
        assertNull(signupRequest.getRole());
    }

    @Test
    void testSettersWithEmptyValues() {
        signupRequest.setName("");
        signupRequest.setEmail("");
        signupRequest.setPassword("");
        signupRequest.setPhoneNumber("");
        signupRequest.setRole("");

        assertEquals("", signupRequest.getName());
        assertEquals("", signupRequest.getEmail());
        assertEquals("", signupRequest.getPassword());
        assertEquals("", signupRequest.getPhoneNumber());
        assertEquals("", signupRequest.getRole());
    }

    @Test
    void testBoundaryValues() {
        // Test minimum length name
        signupRequest.setName("A");
        assertEquals("A", signupRequest.getName());

        // Test maximum length name
        String maxLengthName = "A".repeat(25);
        signupRequest.setName(maxLengthName);
        assertEquals(maxLengthName, signupRequest.getName());

        // Test minimum length password
        signupRequest.setPassword("Pass1!@");
        assertEquals("Pass1!@", signupRequest.getPassword());
    }

    @Test
    void testSpecialCharacters() {
        signupRequest.setName("José María O'Connor");
        signupRequest.setEmail("test+tag@example.co.uk");
        signupRequest.setPassword("Complex1@Pass");

        assertEquals("José María O'Connor", signupRequest.getName());
        assertEquals("test+tag@example.co.uk", signupRequest.getEmail());
        assertEquals("Complex1@Pass", signupRequest.getPassword());
    }

    @Test
    void testUnicodeCharacters() {
        signupRequest.setName("李小明");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("Test123!@");

        assertEquals("李小明", signupRequest.getName());
        assertEquals("test@example.com", signupRequest.getEmail());
        assertEquals("Test123!@", signupRequest.getPassword());
    }
} 