package com.example.kitchensink.controller.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.repository.MemberRepository;
import com.example.kitchensink.service.MemberService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class UserRegistrationStrategyTest {

  @Mock
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @Mock
  private RedirectAttributes redirectAttributes;

  @InjectMocks
  private UserRegistrationStrategy userRegistrationStrategy;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testRegister_ExistingMemberWithNullPassword() {
    // Arrange
    Member member = new Member();
    member.setEmail("test@example.com");
    member.setPassword("newPassword");

    MemberEntity existingMember = new MemberEntity();
    existingMember.setEmail("test@example.com");
    existingMember.setPassword(null);

    when(memberService.findByEmail(member.getEmail())).thenReturn(Optional.of(existingMember));
    when(passwordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");

    // Act
    String result = userRegistrationStrategy.register(member, redirectAttributes);

    // Assert
    assertEquals("redirect:/login", result);
    assertEquals("encodedPassword", existingMember.getPassword(),
        "Password should be updated with encoded password.");

    // Verify the existing member was saved with the updated password
    verify(memberRepository).save(existingMember);

    // Verify that the redirect attributes were set correctly
    verify(redirectAttributes).addFlashAttribute("registrationSuccess", true);
    verify(redirectAttributes).addFlashAttribute("successMessage",
        "Password updated successfully!");
  }

  @Test
  void testRegister_ExistingMemberWithPasswordAlreadySet() {
    // Arrange
    Member member = new Member();
    member.setEmail("test@example.com");

    MemberEntity existingMember = new MemberEntity();
    existingMember.setEmail("test@example.com");
    existingMember.setPassword("existingPassword");

    when(memberService.findByEmail(member.getEmail())).thenReturn(Optional.of(existingMember));

    // Act
    String result = userRegistrationStrategy.register(member, redirectAttributes);

    // Assert
    assertEquals("redirect:/login", result);

    // Verify that no updates were made to the existing member
    verify(memberRepository, never()).save(any(MemberEntity.class));

    // Verify that the redirect attributes were set correctly
    verify(redirectAttributes).addFlashAttribute("registrationError", true);
    verify(redirectAttributes).addFlashAttribute("errorMessage",
        "Account already exists with this email. Please log in.");
  }

  @Test
  void testRegister_NewMember() {
    // Arrange
    Member member = new Member();
    member.setEmail("newuser@example.com");
    member.setPassword("newPassword");

    when(memberService.findByEmail(member.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(member.getPassword())).thenReturn("encodedPassword");

    // Act
    String result = userRegistrationStrategy.register(member, redirectAttributes);

    // Assert
    assertEquals("redirect:/login", result);

    // Verify that a new member was created and registered
    ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
    verify(memberService).registerMember(memberCaptor.capture());
    assertEquals("USER", memberCaptor.getValue().getRole(),
        "New member role should be set to USER.");
    assertEquals("encodedPassword", memberCaptor.getValue().getPassword(),
        "Password should be encoded.");

    // Verify that the redirect attributes were set correctly
    verify(redirectAttributes).addFlashAttribute("registrationSuccess", true);
    verify(redirectAttributes).addFlashAttribute("successMessage", "User successfully registered!");
  }

  @Test
  void testRegister_ExistingMemberWithNullEmail() {
    // Arrange
    Member member = new Member();
    member.setEmail("nonexisting@example.com");
    member.setPassword("newPassword");

    when(memberService.findByEmail(member.getEmail())).thenReturn(Optional.empty());

    // Act
    String result = userRegistrationStrategy.register(member, redirectAttributes);

    // Assert
    assertEquals("redirect:/login", result);

    // Verify that a new member was registered
    verify(memberService, times(1)).registerMember(any(Member.class));
    verify(redirectAttributes).addFlashAttribute("registrationSuccess", true);
    verify(redirectAttributes).addFlashAttribute("successMessage", "User successfully registered!");
  }
}
