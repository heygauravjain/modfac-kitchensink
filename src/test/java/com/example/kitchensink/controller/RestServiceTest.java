package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class RestServiceTest {

  @Mock
  private MemberService memberService;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private RestService restService;

  private Member testMember;

  @BeforeEach
  void setUp() {
    testMember = new Member();
    testMember.setId("1");
    testMember.setName("John Doe");
    testMember.setEmail("john@example.com");
    testMember.setRole("USER");
    testMember.setPhoneNumber("1234567890");
  }

  @Test
  void listAllMembers_ShouldReturnAllMembers() {
    // Given
    List<Member> members = List.of(testMember);
    when(memberService.getAllMembers()).thenReturn(members);

    // When
    ResponseEntity<List<Member>> response = restService.listAllMembers();

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(members, response.getBody());
    verify(memberService).getAllMembers();
  }

  @Test
  void lookupMemberById_WithValidId_ShouldReturnMember() {
    // Given
    when(memberService.findById("1")).thenReturn(testMember);

    // When
    ResponseEntity<Member> response = restService.lookupMemberById("1");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testMember, response.getBody());
    verify(memberService).findById("1");
  }

  @Test
  void lookupMemberById_WithInvalidId_ShouldThrowException() {
    // Given
    when(memberService.findById("999")).thenReturn(null);

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
      restService.lookupMemberById("999");
    });
    verify(memberService).findById("999");
  }

  @Test
  void lookupMemberByEmail_WithValidEmail_ShouldReturnMember() {
    // Given
    when(memberService.findMemberByEmail("john@example.com")).thenReturn(testMember);

    // When
    ResponseEntity<Member> response = restService.lookupMemberByEmail("john@example.com");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(testMember, response.getBody());
    verify(memberService).findMemberByEmail("john@example.com");
  }

  @Test
  void lookupMemberByEmail_WithInvalidEmail_ShouldThrowException() {
    // Given
    when(memberService.findMemberByEmail("invalid@example.com")).thenReturn(null);

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
      restService.lookupMemberByEmail("invalid@example.com");
    });
    verify(memberService).findMemberByEmail("invalid@example.com");
  }

  @Test
  void registerMember_WithValidMember_ShouldReturnCreatedMember() {
    // Given
    when(memberService.registerMember(any(Member.class))).thenReturn(testMember);

    // When
    ResponseEntity<Member> response = restService.registerMember(testMember);

    // Then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(testMember, response.getBody());
    verify(memberService).registerMember(testMember);
  }

  @Test
  void registerMember_WithException_ShouldThrowException() {
    // Given
    when(memberService.registerMember(any(Member.class))).thenThrow(new RuntimeException("Registration failed"));

    // When & Then
    assertThrows(RuntimeException.class, () -> {
      restService.registerMember(testMember);
    });
    verify(memberService).registerMember(testMember);
  }

  @Test
  void deleteMemberById_WithValidId_ShouldDeleteMember() {
    // Given
    when(memberService.findById("1")).thenReturn(testMember);
    doNothing().when(memberService).deleteById("1");
    setupSecurityContext("other@example.com");

    // When
    ResponseEntity<String> response = restService.deleteMemberById("1");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Member deleted successfully", response.getBody());
    verify(memberService).findById("1");
    verify(memberService).deleteById("1");
  }

  @Test
  void deleteMemberById_WithInvalidId_ShouldThrowException() {
    // Given
    when(memberService.findById("999")).thenReturn(null);

    // When & Then
    assertThrows(ResourceNotFoundException.class, () -> {
      restService.deleteMemberById("999");
    });
    verify(memberService).findById("999");
    verify(memberService, never()).deleteById(anyString());
  }

  @Test
  void deleteMemberById_WithOwnAccount_ShouldReturnForbidden() {
    // Given
    when(memberService.findById("1")).thenReturn(testMember);
    setupSecurityContext("john@example.com");

    // When
    ResponseEntity<String> response = restService.deleteMemberById("1");

    // Then
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals("Cannot delete your own account", response.getBody());
    verify(memberService).findById("1");
    verify(memberService, never()).deleteById(anyString());
  }

  @Test
  void updateMember_WithValidMember_ShouldUpdateMember() {
    // Given
    Member updatedMember = new Member();
    updatedMember.setName("Jane Doe");
    updatedMember.setEmail("jane@example.com");
    
    when(memberService.findById("1")).thenReturn(testMember);
    when(memberService.updateMember(eq(testMember), any(Member.class))).thenReturn(updatedMember);
    setupSecurityContext("other@example.com");

    // When
    ResponseEntity<Member> response = restService.updateMember(updatedMember, "1");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(updatedMember, response.getBody());
    verify(memberService).findById("1");
    verify(memberService).updateMember(eq(testMember), eq(updatedMember));
  }

  @Test
  void updateMember_WithInvalidId_ShouldReturnNotFound() {
    // Given
    when(memberService.findById("999")).thenReturn(null);

    // When
    ResponseEntity<Member> response = restService.updateMember(testMember, "999");

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(memberService).findById("999");
    verify(memberService, never()).updateMember(any(Member.class), any(Member.class));
  }

  @Test
  void updateMember_WithOwnAccount_ShouldReturnForbidden() {
    // Given
    when(memberService.findById("1")).thenReturn(testMember);
    setupSecurityContext("john@example.com");

    // When
    ResponseEntity<Member> response = restService.updateMember(testMember, "1");

    // Then
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    verify(memberService).findById("1");
    verify(memberService, never()).updateMember(any(Member.class), any(Member.class));
  }

  @Test
  void updateMember_WithException_ShouldReturnInternalServerError() {
    // Given
    when(memberService.findById("1")).thenReturn(testMember);
    when(memberService.updateMember(any(Member.class), any(Member.class)))
        .thenThrow(new RuntimeException("Update failed"));
    setupSecurityContext("other@example.com");

    // When
    ResponseEntity<Member> response = restService.updateMember(testMember, "1");

    // Then
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    verify(memberService).findById("1");
    verify(memberService).updateMember(eq(testMember), eq(testMember));
  }

  @Test
  void loginForToken_WithValidCredentials_ShouldReturnSuccess() {
    // When
    ResponseEntity<String> response = restService.loginForToken("test@example.com", "password123");

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Login successful. Use the JWT token from your login session for API calls.", response.getBody());
  }

  @Test
  void loginForToken_WithNullEmail_ShouldReturnUnauthorized() {
    // When
    ResponseEntity<String> response = restService.loginForToken(null, "password123");

    // Then
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid credentials", response.getBody());
  }

  @Test
  void loginForToken_WithNullPassword_ShouldReturnUnauthorized() {
    // When
    ResponseEntity<String> response = restService.loginForToken("test@example.com", null);

    // Then
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid credentials", response.getBody());
  }

  @Test
  void loginForToken_WithEmptyEmail_ShouldReturnUnauthorized() {
    // When
    ResponseEntity<String> response = restService.loginForToken("", "password123");

    // Then
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid credentials", response.getBody());
  }

  @Test
  void loginForToken_WithEmptyPassword_ShouldReturnUnauthorized() {
    // When
    ResponseEntity<String> response = restService.loginForToken("test@example.com", "");

    // Then
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid credentials", response.getBody());
  }

  private void setupSecurityContext(String email) {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn(email);
    SecurityContextHolder.setContext(securityContext);
  }
}
