package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RestServiceTest {

  @Mock
  private MemberService memberService;

  @InjectMocks
  private RestService restService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // List All Members Scenarios
  @Test
  void testListAllMembers_ReturnsMemberList() {
    List<Member> mockMembers = Arrays.asList(new Member(), new Member());
    when(memberService.getAllMembers()).thenReturn(mockMembers);

    ResponseEntity<List<Member>> response = restService.listAllMembers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size(), "The list should contain 2 members.");
    verify(memberService, times(1)).getAllMembers();
  }

  @Test
  void testListAllMembers_ReturnsEmptyList() {
    when(memberService.getAllMembers()).thenReturn(List.of());

    ResponseEntity<List<Member>> response = restService.listAllMembers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().size(), "The list should be empty.");
    verify(memberService, times(1)).getAllMembers();
  }

  // Lookup Member by ID Scenarios
  @Test
  void testLookupMemberById_Found() {
    Member mockMember = new Member("123", "John Doe", "john.doe@example.com", "1234567890", null,
        "USER");
    when(memberService.findById("123")).thenReturn(mockMember);

    ResponseEntity<Member> response = restService.lookupMemberById("123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(mockMember.getId(), response.getBody().getId());
    assertEquals(mockMember.getEmail(), response.getBody().getEmail());
    verify(memberService, times(1)).findById("123");
  }

  @Test
  void testLookupMemberById_NotFound() {
    when(memberService.findById("123")).thenReturn(null);

    try {
      restService.lookupMemberById("123");
    } catch (ResourceNotFoundException ex) {
      assertEquals("Member with ID 123 not found.", ex.getMessage());
      verify(memberService, times(1)).findById("123");
    }
  }

  // Create Member Scenarios
  @Test
  void testCreateMember_Success() {
    Member mockMember = new Member();
    mockMember.setEmail("newuser@example.com");
    when(memberService.registerMember(any(Member.class))).thenReturn(mockMember);

    ResponseEntity<Member> response = restService.registerMember(mockMember);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(mockMember.getEmail(), response.getBody().getEmail());
    verify(memberService, times(1)).registerMember(mockMember);
  }

  @Test
  void testCreateMember_ThrowsException() {
    doThrow(new RuntimeException("Registration failed")).when(memberService)
        .registerMember(any(Member.class));

    try {
      Member mockMember = new Member();
      mockMember.setEmail("newuser@example.com");
      restService.registerMember(mockMember);
    } catch (RuntimeException ex) {
      assertEquals("Registration failed", ex.getMessage());
      verify(memberService, times(1)).registerMember(any(Member.class));
    }
  }

  // Update Member Scenarios
  @Test
  void testUpdateMember_Success() {
    Member existingMember = new Member("123", "John Doe", "john.doe@example.com", "1234567890",
        null, "USER");
    Member updatedMember = new Member("123", "John Doe Updated", "john.doe@example.com",
        "1234567890", null, "ADMIN");

    when(memberService.findById("123")).thenReturn(existingMember);
    when(memberService.updateMember(eq(existingMember), eq(updatedMember))).thenReturn(
        updatedMember);

    ResponseEntity<Member> response = restService.updateMember(updatedMember, "123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("John Doe Updated", response.getBody().getName());
    assertEquals("ADMIN", response.getBody().getRole());
    verify(memberService, times(1)).findById("123");
    verify(memberService, times(1)).updateMember(existingMember, updatedMember);
  }

  @Test
  void testUpdateMember_NotFound() {
    when(memberService.findById("123")).thenReturn(null);

    ResponseEntity<Member> response = restService.updateMember(new Member(), "123");

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(memberService, times(1)).findById("123");
    verify(memberService, times(0)).updateMember(any(Member.class), any(Member.class));
  }

  @Test
  void testUpdateMember_ThrowsException() {
    Member existingMember = new Member("123", "John Doe", "john.doe@example.com", "1234567890",
        null, "USER");
    Member updatedMember = new Member("123", "John Doe Updated", "john.doe@example.com",
        "1234567890", null, "ADMIN");

    when(memberService.findById("123")).thenReturn(existingMember);
    doThrow(new RuntimeException("Update failed")).when(memberService)
        .updateMember(any(Member.class), any(Member.class));

    ResponseEntity<Member> response = restService.updateMember(updatedMember, "123");

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    verify(memberService, times(1)).findById("123");
    verify(memberService, times(1)).updateMember(existingMember, updatedMember);
  }

  // Delete Member Scenarios
  @Test
  void testDeleteMemberById_Success() {
    Member existingMember = new Member("123", "John Doe", "john.doe@example.com", "1234567890",
        null, "USER");
    when(memberService.findById("123")).thenReturn(existingMember);
    doNothing().when(memberService).deleteById("123");

    ResponseEntity<String> response = restService.deleteMemberById("123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Member deleted successfully", response.getBody());
    verify(memberService, times(1)).findById("123");
    verify(memberService, times(1)).deleteById("123");
  }

  @Test
  void testDeleteMemberById_NotFound() {
    when(memberService.findById("123")).thenReturn(null);

    try {
      restService.deleteMemberById("123");
    } catch (ResourceNotFoundException ex) {
      assertEquals("Member with ID 123 not found.", ex.getMessage());
      verify(memberService, times(1)).findById("123");
      verify(memberService, times(0)).deleteById("123");
    }
  }

  @Test
  void testDeleteMemberById_ThrowsException() {
    Member existingMember = new Member("123", "John Doe", "john.doe@example.com", "1234567890",
        null, "USER");
    when(memberService.findById("123")).thenReturn(existingMember);
    doThrow(new RuntimeException("Delete failed")).when(memberService).deleteById("123");

    try {
      restService.deleteMemberById("123");
    } catch (RuntimeException ex) {
      assertEquals("Delete failed", ex.getMessage());
      verify(memberService, times(1)).findById("123");
      verify(memberService, times(1)).deleteById("123");
    }
  }
}
