package com.example.kitchensink.controller.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class AdminRegistrationStrategyTest {

  @Mock
  private MemberService memberService;

  @Mock
  private RedirectAttributes redirectAttributes;

  @InjectMocks
  private AdminRegistrationStrategy adminRegistrationStrategy;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testRegister_ExistingMember() {
    // Arrange
    Member member = new Member();
    member.setEmail("existing@example.com");

    // Mock the memberService to return an existing member
    when(memberService.findByEmail(member.getEmail())).thenReturn(Optional.of(new MemberEntity()));

    // Act
    String result = adminRegistrationStrategy.register(member, redirectAttributes);

    // Assert
    assertEquals("redirect:/members", result,
        "Should redirect to /members if member already exists.");

    // Verify that the correct redirect attributes were set
    verify(redirectAttributes).addFlashAttribute("registrationError", true);
    verify(redirectAttributes).addFlashAttribute("errorMessage",
        "Member already registered with this email!");

    // Verify that the memberService.registerMember was not called since the member already exists
    verify(memberService, never()).registerMember(any(Member.class));
  }

  @Test
  void testRegister_NewMember() {
    // Arrange
    Member member = new Member();
    member.setEmail("newuser@example.com");

    // Mock the memberService to return no existing member
    when(memberService.findByEmail(member.getEmail())).thenReturn(Optional.empty());

    // Act
    String result = adminRegistrationStrategy.register(member, redirectAttributes);

    // Assert
    assertEquals("redirect:/members", result,
        "Should redirect to /members after successful registration.");

    // Capture the member object that was registered
    ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
    verify(memberService).registerMember(memberCaptor.capture());
    assertEquals(member.getEmail(), memberCaptor.getValue().getEmail(),
        "The registered member's email should match.");

    // Verify that the correct redirect attributes were set
    verify(redirectAttributes).addFlashAttribute("registrationSuccess", true);
    verify(redirectAttributes).addFlashAttribute("successMessage",
        "Member successfully registered!");
  }

  @Test
  void testRegister_ExceptionDuringRegistration() {
    // Arrange
    Member member = new Member();
    member.setEmail("erroruser@example.com");

    // Mock the memberService to throw an exception during registration
    when(memberService.findByEmail(member.getEmail())).thenThrow(
        new RuntimeException("Database error"));

    // Act
    String result = adminRegistrationStrategy.register(member, redirectAttributes);

    // Assert
    assertEquals("redirect:/members", result,
        "Should redirect to /members even if an exception occurs.");

    // Verify that the correct redirect attributes were set for error handling
    verify(redirectAttributes).addFlashAttribute("registrationError", true);
    verify(redirectAttributes).addFlashAttribute("errorMessage", "Database error");
  }

  @Test
  void testGetRootErrorMessage_WithException() {
    // Arrange
    Exception causeException = new Exception("Root Cause");
    Exception exception = new Exception("Wrapper Exception", causeException);

    // Act
    String errorMessage = adminRegistrationStrategy.getRootErrorMessage(exception);

    // Assert
    assertEquals("Root Cause", errorMessage, "Should return the root cause message.");
  }

  @Test
  void testGetRootErrorMessage_NullException() {
    // Act
    String errorMessage = adminRegistrationStrategy.getRootErrorMessage(null);

    // Assert
    assertEquals("Registration failed. See server log for more information", errorMessage,
        "Should return the default error message when the exception is null.");
  }
}
