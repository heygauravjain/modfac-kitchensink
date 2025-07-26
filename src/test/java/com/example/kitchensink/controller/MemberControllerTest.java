package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.kitchensink.controller.strategy.RegistrationContext;
import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.exception.ResourceNotFoundException;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import java.security.Principal;
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
  private Principal principal;

  @Mock
  private Authentication authentication;

  @Mock
  private RedirectAttributes redirectAttributes;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  // Scenario: Show registration form with empty members list
  @Test
  void showRegistrationForm_ShouldReturnIndexView_WithNoMembers() {
    when(memberService.getAllMembers()).thenReturn(new ArrayList<>()); // Empty list

    String viewName = memberController.showAdminHome(model, principal);

    assertEquals("index", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
    verify(model).addAttribute(eq("members"), anyList());
  }

  // Scenario: Show registration form with multiple members
  @Test
  void showRegistrationForm_ShouldReturnIndexView_WithMembers() {
    ArrayList<Member> members = new ArrayList<>();
    members.add(new Member("1", "John Doe", "john.doe@example.com", "1234567890", null, "ADMIN"));
    members.add(new Member("2", "Jane Doe", "jane.doe@example.com", "0987654321", null, "USER"));

    when(memberService.getAllMembers()).thenReturn(members);

    String viewName = memberController.showAdminHome(model, principal);

    assertEquals("index", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
    verify(model).addAttribute(eq("members"), eq(members));
  }

  // Scenario: Register member using admin strategy
  @Test
  void registerMember_WhenSourceIsIndex_ShouldUseAdminStrategy() {
    Member member = new Member();
    when(registrationContext.register(eq(member), eq(redirectAttributes))).thenReturn("index");

    String viewName = memberController.registerMember(member, "index", redirectAttributes);

    assertEquals("index", viewName);
    verify(registrationContext).setStrategy("index");
    verify(registrationContext).register(eq(member), eq(redirectAttributes));
  }

  // Scenario: Register member using user strategy
  @Test
  void registerMember_WhenSourceIsRegister_ShouldUseUserStrategy() {
    Member member = new Member();
    when(registrationContext.register(eq(member), eq(redirectAttributes))).thenReturn("register");

    String viewName = memberController.registerMember(member, "register", redirectAttributes);

    assertEquals("register", viewName);
    verify(registrationContext).setStrategy("register");
    verify(registrationContext).register(eq(member), eq(redirectAttributes));
  }

  // Scenario: Show login page
  @Test
  void showLoginPage_ShouldReturnLoginView() {
    String viewName = memberController.showLoginPage();
    assertEquals("login", viewName);
  }

  // Scenario: Show user profile page
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

  // Scenario: Register member with invalid data - should return index view
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

  // Scenario: Error 403 page
  @Test
  void error403_ShouldReturn403View() {
    String viewName = memberController.error403();
    assertEquals("/error/403", viewName);
  }

  // Scenario: Error 401 page
  @Test
  void error401_ShouldReturn401View() {
    String viewName = memberController.error401();
    assertEquals("/error/401", viewName);
  }
}
