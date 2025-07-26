package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.controller.strategy.RegistrationContext;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    // Given
    when(memberService.getAllMembers()).thenReturn(new ArrayList<>());

    // When
    String viewName = memberController.showRegistrationForm(model, authentication);

    // Then
    assertEquals("index", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
    verify(model).addAttribute(eq("members"), anyList());
  }

  @Test
  void registerMember_WhenSourceIsIndex_ShouldUseAdminStrategy() {
    // Given
    Member member = new Member();
    when(registrationContext.register(eq(member), eq(redirectAttributes))).thenReturn("index");

    // When
    String viewName = memberController.registerMember(member, "index", redirectAttributes);

    // Then
    assertEquals("index", viewName);
    verify(registrationContext).setStrategy("index");
    verify(registrationContext).register(eq(member), eq(redirectAttributes));
  }

  @Test
  void registerMember_WhenSourceIsRegister_ShouldUseUserStrategy() {
    // Given
    Member member = new Member();
    when(registrationContext.register(eq(member), eq(redirectAttributes))).thenReturn("register");

    // When
    String viewName = memberController.registerMember(member, "register", redirectAttributes);

    // Then
    assertEquals("register", viewName);
    verify(registrationContext).setStrategy("register");
    verify(registrationContext).register(eq(member), eq(redirectAttributes));
  }

  @Test
  void showLoginPage_ShouldReturnLoginView() {
    // When
    String viewName = memberController.showLoginPage();

    // Then
    assertEquals("login", viewName);
  }

  @Test
  public void showMembersPage_ShouldReturnMembersView() {

  }
}
