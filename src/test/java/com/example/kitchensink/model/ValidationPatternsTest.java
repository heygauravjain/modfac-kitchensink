package com.example.kitchensink.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ValidationPatterns utility class.
 * Tests all validation patterns and constants.
 */
class ValidationPatternsTest {

    @Test
    void testPhonePattern_ValidPhoneNumbers() {
        // Test valid phone numbers
        assertTrue("1234567890".matches(ValidationPatterns.PHONE_PATTERN));
        assertTrue("12345678901".matches(ValidationPatterns.PHONE_PATTERN));
        assertTrue("123456789012".matches(ValidationPatterns.PHONE_PATTERN));
    }

    @Test
    void testPhonePattern_InvalidPhoneNumbers() {
        // Test invalid phone numbers
        assertFalse("123456789".matches(ValidationPatterns.PHONE_PATTERN)); // Too short
        assertFalse("1234567890123".matches(ValidationPatterns.PHONE_PATTERN)); // Too long
        assertFalse("123456789a".matches(ValidationPatterns.PHONE_PATTERN)); // Contains letters
        assertFalse("123-456-7890".matches(ValidationPatterns.PHONE_PATTERN)); // Contains hyphens
    }

    @Test
    void testPhonePattern_EmptyString() {
        // Test empty string (should be valid)
        assertTrue("".matches(ValidationPatterns.PHONE_PATTERN));
    }

    @Test
    void testNamePattern_ValidNames() {
        // Test valid names (no numbers)
        assertTrue("John".matches(ValidationPatterns.NAME_PATTERN));
        assertTrue("Mary Jane".matches(ValidationPatterns.NAME_PATTERN));
        assertTrue("O'Connor".matches(ValidationPatterns.NAME_PATTERN));
        assertTrue("José".matches(ValidationPatterns.NAME_PATTERN));
    }

    @Test
    void testNamePattern_InvalidNames() {
        // Test invalid names (contain numbers)
        assertFalse("John123".matches(ValidationPatterns.NAME_PATTERN));
        assertFalse("123John".matches(ValidationPatterns.NAME_PATTERN));
        assertFalse("John1Doe".matches(ValidationPatterns.NAME_PATTERN));
    }

    @Test
    void testPasswordPattern_ValidPasswords() {
        // Test valid complex passwords that match the pattern:
        // At least one uppercase, one lowercase, one number, one special character (@$!%*?&), minimum 8 chars
        assertTrue("Password123!".matches(ValidationPatterns.PASSWORD_PATTERN));
        assertTrue("MyPass@word1".matches(ValidationPatterns.PASSWORD_PATTERN));
        assertTrue("Test$Pass3".matches(ValidationPatterns.PASSWORD_PATTERN));
        assertTrue("Complex1@Pass".matches(ValidationPatterns.PASSWORD_PATTERN));
        assertTrue("Valid8!Word".matches(ValidationPatterns.PASSWORD_PATTERN));
        assertTrue("Secure%Pass2".matches(ValidationPatterns.PASSWORD_PATTERN));
    }

    @Test
    void testPasswordPattern_InvalidPasswords() {
        // Test invalid passwords
        assertFalse("password123!".matches(ValidationPatterns.PASSWORD_PATTERN)); // No uppercase
        assertFalse("PASSWORD123!".matches(ValidationPatterns.PASSWORD_PATTERN)); // No lowercase
        assertFalse("Password!".matches(ValidationPatterns.PASSWORD_PATTERN)); // No number
        assertFalse("Password123".matches(ValidationPatterns.PASSWORD_PATTERN)); // No special char
        assertFalse("Pass1!".matches(ValidationPatterns.PASSWORD_PATTERN)); // Too short
        assertFalse("Password#123".matches(ValidationPatterns.PASSWORD_PATTERN)); // Invalid special char (#)
        assertFalse("Password^123".matches(ValidationPatterns.PASSWORD_PATTERN)); // Invalid special char (^)
    }

    @Test
    void testConstants() {
        // Test that constants are properly defined
        assertEquals("Phone number must be 10-12 digits or empty", ValidationPatterns.PHONE_MESSAGE);
        assertEquals("Name must not contain numbers", ValidationPatterns.NAME_MESSAGE);
        assertEquals("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character", 
                    ValidationPatterns.PASSWORD_MESSAGE);
        assertEquals("Password must be at least 8 characters long", ValidationPatterns.SIMPLE_PASSWORD_MESSAGE);
        
        assertEquals(1, ValidationPatterns.NAME_MIN_LENGTH);
        assertEquals(25, ValidationPatterns.NAME_MAX_LENGTH);
        assertEquals(8, ValidationPatterns.PASSWORD_MIN_LENGTH);
    }

    @Test
    void testConstructorIsPrivate() {
        // Test that constructor is private (utility class)
        assertThrows(Exception.class, () -> {
            ValidationPatterns.class.getDeclaredConstructor().newInstance();
        });
    }
} 