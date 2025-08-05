package com.example.kitchensink.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberDocumentTest {

  private MemberDocument memberDocument;

  @BeforeEach
  void setUp() {
    memberDocument = new MemberDocument();
  }

  @Test
  void testNoArgsConstructor() {
    // When
    MemberDocument document = new MemberDocument();

    // Then
    assertNull(document.getId());
    assertNull(document.getName());
    assertNull(document.getEmail());
    assertNull(document.getPhoneNumber());
    assertNull(document.getPassword());
    assertNull(document.getRole());
  }

  @Test
  void testAllArgsConstructor() {
    // When
    MemberDocument document = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password123", "USER");

    // Then
    assertEquals("1", document.getId());
    assertEquals("John Doe", document.getName());
    assertEquals("john@example.com", document.getEmail());
    assertEquals("1234567890", document.getPhoneNumber());
    assertEquals("password123", document.getPassword());
    assertEquals("USER", document.getRole());
  }

  @Test
  void testSettersAndGetters() {
    // When
    memberDocument.setId("2");
    memberDocument.setName("Jane Doe");
    memberDocument.setEmail("jane@example.com");
    memberDocument.setPhoneNumber("9876543210");
    memberDocument.setPassword("newpassword");
    memberDocument.setRole("ADMIN");

    // Then
    assertEquals("2", memberDocument.getId());
    assertEquals("Jane Doe", memberDocument.getName());
    assertEquals("jane@example.com", memberDocument.getEmail());
    assertEquals("9876543210", memberDocument.getPhoneNumber());
    assertEquals("newpassword", memberDocument.getPassword());
    assertEquals("ADMIN", memberDocument.getRole());
  }

  @Test
  void testEquals_WithSameObject() {
    // Given
    MemberDocument document1 = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");
    MemberDocument document2 = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");

    // When & Then
    assertEquals(document1, document2);
    assertEquals(document1.hashCode(), document2.hashCode());
  }

  @Test
  void testEquals_WithDifferentObjects() {
    // Given
    MemberDocument document1 = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");
    MemberDocument document2 = new MemberDocument("2", "Jane Doe", "jane@example.com", "1234567890", "password", "USER");

    // When & Then
    assertNotEquals(document1, document2);
  }

  @Test
  void testEquals_WithNull() {
    // Given
    MemberDocument document = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");

    // When & Then
    assertNotEquals(null, document);
  }

  @Test
  void testEquals_WithDifferentClass() {
    // Given
    MemberDocument document = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");

    // When & Then
    assertNotEquals(document, "string");
  }

  @Test
  void testEquals_WithSelf() {
    // Given
    MemberDocument document = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");

    // When & Then
    assertEquals(document, document);
  }

  @Test
  void testEquals_WithNullValues() {
    // Given
    MemberDocument document1 = new MemberDocument(null, null, null, null, null, null);
    MemberDocument document2 = new MemberDocument(null, null, null, null, null, null);

    // When & Then
    assertEquals(document1, document2);
    assertEquals(document1.hashCode(), document2.hashCode());
  }

  @Test
  void testEquals_WithPartialNullValues() {
    // Given
    MemberDocument document1 = new MemberDocument("1", "John Doe", null, "1234567890", "password", "USER");
    MemberDocument document2 = new MemberDocument("1", "John Doe", null, "1234567890", "password", "USER");

    // When & Then
    assertEquals(document1, document2);
    assertEquals(document1.hashCode(), document2.hashCode());
  }

  @Test
  void testEquals_WithDifferentNullValues() {
    // Given
    MemberDocument document1 = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");
    MemberDocument document2 = new MemberDocument("1", "John Doe", null, "1234567890", "password", "USER");

    // When & Then
    assertNotEquals(document1, document2);
  }

  @Test
  void testToString() {
    // Given
    MemberDocument document = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");

    // When
    String toString = document.toString();

    // Then
    assertNotNull(toString);
    assertTrue(toString.contains("John Doe"));
    assertTrue(toString.contains("john@example.com"));
    assertTrue(toString.contains("1234567890"));
    assertTrue(toString.contains("USER"));
  }

  @Test
  void testToString_WithNullValues() {
    // Given
    MemberDocument document = new MemberDocument(null, null, null, null, null, null);

    // When
    String toString = document.toString();

    // Then
    assertNotNull(toString);
  }

  @Test
  void testHashCode_Consistency() {
    // Given
    MemberDocument document = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");

    // When
    int hashCode1 = document.hashCode();
    int hashCode2 = document.hashCode();

    // Then
    assertEquals(hashCode1, hashCode2);
  }

  @Test
  void testHashCode_WithNullValues() {
    // Given
    MemberDocument document = new MemberDocument(null, null, null, null, null, null);

    // When
    int hashCode = document.hashCode();

    // Then
    assertNotNull(hashCode);
  }

  @Test
  void testSerializable() {
    // Given
    MemberDocument document = new MemberDocument("1", "John Doe", "john@example.com", "1234567890", "password", "USER");

    // When & Then
    assertTrue(document instanceof java.io.Serializable);
  }
} 