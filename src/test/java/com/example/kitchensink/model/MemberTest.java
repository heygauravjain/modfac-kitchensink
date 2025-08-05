package com.example.kitchensink.model;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTest {

  private Validator validator;
  private Member member;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
    
    member = new Member();
    member.setId("1");
    member.setName("John Doe");
    member.setEmail("john@example.com");
    member.setPassword("Password123!");
    member.setPhoneNumber("1234567890");
    member.setRole("USER");
  }

  @Test
  void testValidMember_ShouldPassValidation() {
    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Valid member should have no validation violations");
  }

  @Test
  void testMemberWithNullName_ShouldFailValidation() {
    // Given
    member.setName(null);

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with null name should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void testMemberWithEmptyName_ShouldFailValidation() {
    // Given
    member.setName("");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with empty name should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void testMemberWithShortName_ShouldFailValidation() {
    // Given
    member.setName("A");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with short name should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void testMemberWithLongName_ShouldFailValidation() {
    // Given
    member.setName("A".repeat(26));

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with long name should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void testMemberWithInvalidNamePattern_ShouldFailValidation() {
    // Given
    member.setName("John123");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with invalid name pattern should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void testMemberWithNullEmail_ShouldFailValidation() {
    // Given
    member.setEmail(null);

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with null email should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void testMemberWithEmptyEmail_ShouldFailValidation() {
    // Given
    member.setEmail("");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with empty email should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void testMemberWithInvalidEmail_ShouldFailValidation() {
    // Given
    member.setEmail("invalid-email");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with invalid email should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
  }

  @Test
  void testMemberWithShortPassword_ShouldFailValidation() {
    // Given
    member.setPassword("123");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with short password should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
  }

  @Test
  void testMemberWithNullPassword_ShouldPassValidation() {
    // Given
    member.setPassword(null);

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with null password should pass validation");
  }

  @Test
  void testMemberWithEmptyPassword_ShouldPassValidation() {
    // Given
    member.setPassword("");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with empty password should pass validation");
  }

  @Test
  void testMemberWithInvalidPhoneNumber_ShouldFailValidation() {
    // Given
    member.setPhoneNumber("invalid-phone");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertFalse(violations.isEmpty(), "Member with invalid phone number should have validation violations");
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
  }

  @Test
  void testMemberWithNullPhoneNumber_ShouldPassValidation() {
    // Given
    member.setPhoneNumber(null);

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with null phone number should pass validation");
  }

  @Test
  void testMemberWithEmptyPhoneNumber_ShouldPassValidation() {
    // Given
    member.setPhoneNumber("");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with empty phone number should pass validation");
  }

  @Test
  void testMemberWithValidPhoneNumber_ShouldPassValidation() {
    // Given
    member.setPhoneNumber("1234567890");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with valid phone number should pass validation");
  }

  @Test
  void testMemberWithValidPhoneNumberWithDashes_ShouldPassValidation() {
    // Given
    member.setPhoneNumber("123-456-7890");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with valid phone number with dashes should pass validation");
  }

  @Test
  void testMemberWithValidPhoneNumberWithParentheses_ShouldPassValidation() {
    // Given
    member.setPhoneNumber("(123) 456-7890");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with valid phone number with parentheses should pass validation");
  }

  @Test
  void testMemberConstructor_WithAllArgsConstructor() {
    // When
    Member member = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");

    // Then
    assertEquals("1", member.getId());
    assertEquals("John Doe", member.getName());
    assertEquals("john@example.com", member.getEmail());
    assertEquals("password", member.getPassword());
    assertEquals("1234567890", member.getPhoneNumber());
    assertEquals("USER", member.getRole());
  }

  @Test
  void testMemberConstructor_WithNoArgsConstructor() {
    // When
    Member member = new Member();

    // Then
    assertNull(member.getId());
    assertNull(member.getName());
    assertNull(member.getEmail());
    assertNull(member.getPassword());
    assertNull(member.getPhoneNumber());
    assertEquals("USER", member.getRole()); // Default value
  }

  @Test
  void testMemberSettersAndGetters() {
    // Given
    Member member = new Member();

    // When
    member.setId("2");
    member.setName("Jane Doe");
    member.setEmail("jane@example.com");
    member.setPassword("newpassword");
    member.setPhoneNumber("9876543210");
    member.setRole("ADMIN");

    // Then
    assertEquals("2", member.getId());
    assertEquals("Jane Doe", member.getName());
    assertEquals("jane@example.com", member.getEmail());
    assertEquals("newpassword", member.getPassword());
    assertEquals("9876543210", member.getPhoneNumber());
    assertEquals("ADMIN", member.getRole());
  }

  @Test
  void testMemberEquals_WithSameObject() {
    // Given
    Member member1 = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");
    Member member2 = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");

    // When & Then
    assertEquals(member1, member2);
    assertEquals(member1.hashCode(), member2.hashCode());
  }

  @Test
  void testMemberEquals_WithDifferentObjects() {
    // Given
    Member member1 = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");
    Member member2 = new Member("2", "Jane Doe", "jane@example.com", "password", "1234567890", "USER");

    // When & Then
    assertNotEquals(member1, member2);
  }

  @Test
  void testMemberToString() {
    // Given
    Member member = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");

    // When
    String toString = member.toString();

    // Then
    assertNotNull(toString);
    assertTrue(toString.contains("John Doe"));
    assertTrue(toString.contains("john@example.com"));
  }

  @Test
  void testMemberEquals_WithNull() {
    // Given
    Member member = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");

    // When & Then
    assertNotEquals(null, member);
  }

  @Test
  void testMemberEquals_WithDifferentClass() {
    // Given
    Member member = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");
    String differentObject = "not a member";

    // When & Then
    assertNotEquals(member, differentObject);
  }



  @Test
  void testMemberEquals_WithNullFields() {
    // Given
    Member member1 = new Member(null, null, null, null, null, null);
    Member member2 = new Member(null, null, null, null, null, null);

    // When & Then
    assertEquals(member1, member2);
    assertEquals(member1.hashCode(), member2.hashCode());
  }

  @Test
  void testMemberEquals_WithDifferentNullFields() {
    // Given
    Member member1 = new Member("1", null, "john@example.com", "password", "1234567890", "USER");
    Member member2 = new Member("1", "John Doe", null, "password", "1234567890", "USER");

    // When & Then
    assertNotEquals(member1, member2);
  }

  @Test
  void testMemberHashCode_Consistency() {
    // Given
    Member member = new Member("1", "John Doe", "john@example.com", "password", "1234567890", "USER");

    // When & Then
    assertEquals(member.hashCode(), member.hashCode());
  }

  @Test
  void testMemberHashCode_WithNullFields() {
    // Given
    Member member = new Member(null, null, null, null, null, null);

    // When & Then
    assertNotNull(member.hashCode());
  }

  @Test
  void testMemberValidation_WithNullId() {
    // Given
    member.setId(null);

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with null id should pass validation");
  }

  @Test
  void testMemberValidation_WithEmptyId() {
    // Given
    member.setId("");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with empty id should pass validation");
  }

  @Test
  void testMemberValidation_WithNullRole() {
    // Given
    member.setRole(null);

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with null role should pass validation");
  }

  @Test
  void testMemberValidation_WithEmptyRole() {
    // Given
    member.setRole("");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with empty role should pass validation");
  }

  @Test
  void testMemberValidation_WithBoundaryNameLength() {
    // Given - Test minimum length
    member.setName("A");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with minimum length name should pass validation");

    // Given - Test maximum length
    member.setName("A".repeat(25));

    // When
    violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with maximum length name should pass validation");
  }

  @Test
  void testMemberValidation_WithBoundaryPasswordLength() {
    // Given - Test minimum length
    member.setPassword("Pass1!@");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with minimum length password should pass validation");
  }

  @Test
  void testMemberValidation_WithSpecialCharactersInName() {
    // Given
    member.setName("José María O'Connor");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with special characters in name should pass validation");
  }

  @Test
  void testMemberValidation_WithUnicodeCharactersInName() {
    // Given
    member.setName("李小明");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with unicode characters in name should pass validation");
  }

  @Test
  void testMemberValidation_WithComplexEmail() {
    // Given
    member.setEmail("test+tag@example.co.uk");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with complex email should pass validation");
  }

  @Test
  void testMemberValidation_WithInternationalPhoneNumber() {
    // Given
    member.setPhoneNumber("123456789012");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with international phone number should pass validation");
  }

  @Test
  void testMemberValidation_WithBoundaryPhoneNumberLength() {
    // Given - Test minimum length
    member.setPhoneNumber("1234567890");

    // When
    Set<ConstraintViolation<Member>> violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with minimum length phone number should pass validation");

    // Given - Test maximum length
    member.setPhoneNumber("123456789012");

    // When
    violations = validator.validate(member);

    // Then
    assertTrue(violations.isEmpty(), "Member with maximum length phone number should pass validation");
  }
} 