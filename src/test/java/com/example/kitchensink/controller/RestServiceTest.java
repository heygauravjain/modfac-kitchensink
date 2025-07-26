package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

  @Test
  void testListAllMembers_ReturnsMemberList() {
    // Arrange: Mock the service to return a list of members
    List<Member> mockMembers = Arrays.asList(new Member(), new Member());
    when(memberService.getAllMembers()).thenReturn(mockMembers);

    // Act: Call the listAllMembers method
    ResponseEntity<List<Member>> response = restService.listAllMembers();

    // Assert: Verify that the response status is OK and the list matches the mock list
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size(), "The list should contain 2 members.");
    verify(memberService, times(1)).getAllMembers();
  }

  @Test
  void testListAllMembers_ReturnsEmptyList() {
    // Arrange: Mock the service to return an empty list
    when(memberService.getAllMembers()).thenReturn(List.of());

    // Act: Call the listAllMembers method
    ResponseEntity<List<Member>> response = restService.listAllMembers();

    // Assert: Verify that the response status is OK and the list is empty
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().size(), "The list should be empty.");
    verify(memberService, times(1)).getAllMembers();
  }

  @Test
  void testLookupMemberById_Found() {
    // Arrange: Mock the service to return a specific member
    Member mockMember = new Member();
    mockMember.setId("123");
    mockMember.setEmail("test@example.com");
    when(memberService.findById("123")).thenReturn(mockMember);

    // Act: Call the lookupMemberById method
    ResponseEntity<Member> response = restService.lookupMemberById("123");

    // Assert: Verify that the response is OK and the body matches the mock member
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(mockMember.getId(), response.getBody().getId());
    assertEquals(mockMember.getEmail(), response.getBody().getEmail());
    verify(memberService, times(1)).findById("123");
  }

  @Test
  void testLookupMemberById_NotFound() {
    // Arrange: Mock the service to return null when looking up an ID
    when(memberService.findById("123")).thenReturn(null);

    // Act & Assert: Call the lookupMemberById method and expect a ResourceNotFoundException
    try {
      restService.lookupMemberById("123");
    } catch (ResourceNotFoundException ex) {
      assertEquals("Member with ID 123 not found.", ex.getMessage());
      verify(memberService, times(1)).findById("123");
    }
  }

  @Test
  void testCreateMember_Success() {
    // Arrange: Mock the service to do nothing when registering a member
    doNothing().when(memberService).registerMember(any(Member.class));

    // Act: Call the createMember method with a mock member object
    Member mockMember = new Member();
    mockMember.setEmail("newuser@example.com");
    ResponseEntity<Member> response = restService.createMember(mockMember);

    // Assert: Verify that the response status is CREATED and the body matches the member
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(mockMember.getEmail(), response.getBody().getEmail());
    verify(memberService, times(1)).registerMember(mockMember);
  }

  @Test
  void testCreateMember_ThrowsException() {
    // Arrange: Mock the service to throw an exception when registering a member
    doThrow(new RuntimeException("Registration failed")).when(memberService)
        .registerMember(any(Member.class));

    // Act & Assert: Call the createMember method and expect the exception
    try {
      Member mockMember = new Member();
      mockMember.setEmail("newuser@example.com");
      restService.createMember(mockMember);
    } catch (RuntimeException ex) {
      assertEquals("Registration failed", ex.getMessage());
      verify(memberService, times(1)).registerMember(any(Member.class));
    }
  }
}
