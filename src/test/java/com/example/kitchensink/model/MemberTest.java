package com.example.kitchensink.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Member model.
 * Tests validation annotations and Lombok functionality.
 */
class MemberTest {

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(member);
        assertNull(member.getId());
        assertNull(member.getName());
        assertNull(member.getEmail());
        assertNull(member.getPassword());
        assertNull(member.getPhoneNumber());
        assertEquals("USER", member.getRole()); // Default value
    }

    @Test
    void testAllArgsConstructor() {
        Member testMember = new Member("1", "John Doe", "john@example.com", "password123", "1234567890", "ADMIN");
        assertEquals("1", testMember.getId());
        assertEquals("John Doe", testMember.getName());
        assertEquals("john@example.com", testMember.getEmail());
        assertEquals("password123", testMember.getPassword());
        assertEquals("1234567890", testMember.getPhoneNumber());
        assertEquals("ADMIN", testMember.getRole());
    }

    @Test
    void testSettersAndGetters() {
        member.setId("1");
        member.setName("John Doe");
        member.setEmail("john@example.com");
        member.setPassword("password123");
        member.setPhoneNumber("1234567890");
        member.setRole("ADMIN");

        assertEquals("1", member.getId());
        assertEquals("John Doe", member.getName());
        assertEquals("john@example.com", member.getEmail());
        assertEquals("password123", member.getPassword());
        assertEquals("1234567890", member.getPhoneNumber());
        assertEquals("ADMIN", member.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        Member member1 = new Member("1", "John Doe", "john@example.com", "password123", "1234567890", "USER");
        Member member2 = new Member("1", "John Doe", "john@example.com", "password123", "1234567890", "USER");
        Member member3 = new Member("2", "John Doe", "john@example.com", "password123", "1234567890", "USER");

        assertEquals(member1, member2);
        assertNotEquals(member1, member3);
        assertEquals(member1.hashCode(), member2.hashCode());
        assertNotEquals(member1.hashCode(), member3.hashCode());
    }

    @Test
    void testToString() {
        Member testMember = new Member("1", "John Doe", "john@example.com", "password123", "1234567890", "USER");
        String toString = testMember.toString();

        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("john@example.com"));
        assertTrue(toString.contains("password123"));
        assertTrue(toString.contains("1234567890"));
        assertTrue(toString.contains("USER"));
        assertTrue(toString.contains("Member"));
    }

    @Test
    void testEqualsWithNull() {
        Member testMember = new Member("1", "John Doe", "john@example.com", "password123", "1234567890", "USER");
        assertNotEquals(null, testMember);
    }

    @Test
    void testEqualsWithDifferentClass() {
        Member testMember = new Member("1", "John Doe", "john@example.com", "password123", "1234567890", "USER");
        assertNotEquals(testMember, "string");
    }

    @Test
    void testEqualsWithSameObject() {
        Member testMember = new Member("1", "John Doe", "john@example.com", "password123", "1234567890", "USER");
        assertEquals(testMember, testMember);
    }

    @Test
    void testDefaultRole() {
        Member testMember = new Member();
        assertEquals("USER", testMember.getRole());
    }

    @Test
    void testNullValues() {
        Member testMember = new Member(null, null, null, null, null, null);
        assertNull(testMember.getId());
        assertNull(testMember.getName());
        assertNull(testMember.getEmail());
        assertNull(testMember.getPassword());
        assertNull(testMember.getPhoneNumber());
        assertNull(testMember.getRole());
    }

    @Test
    void testEmptyStringValues() {
        Member testMember = new Member("", "", "", "", "", "");
        assertEquals("", testMember.getId());
        assertEquals("", testMember.getName());
        assertEquals("", testMember.getEmail());
        assertEquals("", testMember.getPassword());
        assertEquals("", testMember.getPhoneNumber());
        assertEquals("", testMember.getRole());
    }
} 