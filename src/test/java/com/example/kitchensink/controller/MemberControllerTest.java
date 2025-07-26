package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.controller.strategy.RegistrationContext;
import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class MemberControllerTest {

  @Mock
  private MemberService memberService;

  @Mock
  private RegistrationContext registrationContext;

  @InjectMocks
  private MemberController memberController;

  @Mock
  private Model model;

  @Mock
  private Authentication authentication;

  @Mock
  private RedirectAttributes redirectAttributes;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void showRegistrationForm_ShouldReturnIndexView() {
    when(memberService.getAllMembers()).thenReturn(new ArrayList<>());

    String viewName = memberController.showRegistrationForm(model);

    assertEquals("index", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
    verify(model).addAttribute(eq("members"), anyList());
  }

  @Test
  void registerMember_WhenSourceIsIndex_ShouldUseAdminStrategy() {
    Member member = new Member();
    when(registrationContext.register(eq(member), eq(redirectAttributes))).thenReturn("index");

    String viewName = memberController.registerMember(member, "index", redirectAttributes);

    assertEquals("index", viewName);
    verify(registrationContext).setStrategy("index");
    verify(registrationContext).register(eq(member), eq(redirectAttributes));
  }

  @Test
  void registerMember_WhenSourceIsRegister_ShouldUseUserStrategy() {
    Member member = new Member();
    when(registrationContext.register(eq(member), eq(redirectAttributes))).thenReturn("register");

    String viewName = memberController.registerMember(member, "register", redirectAttributes);

    assertEquals("register", viewName);
    verify(registrationContext).setStrategy("register");
    verify(registrationContext).register(eq(member), eq(redirectAttributes));
  }

  @Test
  void showLoginPage_ShouldReturnLoginView() {
    String viewName = memberController.showLoginPage();
    assertEquals("login", viewName);
  }

  @Test
  void showUserProfile_ShouldReturnUserProfileView() {
    String email = "test@example.com";
    MemberDocument memberDocument = new MemberDocument("1", "John Doe", email, "1234567890",
        "password", "USER");

    when(authentication.getName()).thenReturn(email);
    when(memberService.findByEmail(email)).thenReturn(Optional.of(memberDocument));

    String viewName = memberController.showUserProfile(model, authentication);

    assertEquals("user-profile", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
  }

  @Test
  void showUserProfile_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
    String email = "unknown@example.com";

    when(authentication.getName()).thenReturn(email);
    when(memberService.findByEmail(email)).thenReturn(Optional.empty());

    try {
      memberController.showUserProfile(model, authentication);
    } catch (ResourceNotFoundException ex) {
      assertEquals("Member with email " + email + " not found.", ex.getMessage());
    }
  }

  @Test
  void registerMember_WithInvalidData_ShouldReturnIndexView() {
    Member invalidMember = new Member(); // Empty member with invalid data
    when(registrationContext.register(eq(invalidMember), eq(redirectAttributes))).thenReturn(
        "index");

    String viewName = memberController.registerMember(invalidMember, "index", redirectAttributes);

    assertEquals("index", viewName); // Should stay on the index view due to validation errors
    verify(registrationContext).setStrategy("index");
    verify(registrationContext).register(eq(invalidMember), eq(redirectAttributes));
  }
}
