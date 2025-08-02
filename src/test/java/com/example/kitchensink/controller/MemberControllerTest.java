package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.kitchensink.controller.strategy.RegistrationContext;
import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

  @Mock
  private HttpServletResponse response;

  @Mock
  private HttpSession session;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void showAdminHome_ShouldReturnIndexView_WithNoMembers() {
    when(memberService.getAllMembers()).thenReturn(new ArrayList<>());
    when(session.getAttribute("userEmail")).thenReturn(null);
    when(session.getAttribute("accessToken")).thenReturn(null);
    when(session.getAttribute("refreshToken")).thenReturn(null);
    when(session.getAttribute("userRole")).thenReturn(null);

    String viewName = memberController.showAdminHome(model, null, response, null, session);

    assertEquals("index", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
    verify(model).addAttribute(eq("members"), anyList());
    verify(model).addAttribute(eq("loggedInUser"), eq("Admin"));
  }

  @Test
  void showAdminHome_ShouldReturnIndexView_WithMembers() {
    ArrayList<Member> members = new ArrayList<>();
    members.add(new Member("1", "John Doe", "john.doe@example.com", "1234567890", null, "ADMIN"));
    members.add(new Member("2", "Jane Doe", "jane.doe@example.com", "0987654321", null, "USER"));

    when(memberService.getAllMembers()).thenReturn(members);
    when(principal.getName()).thenReturn("admin@admin.com");

    String viewName = memberController.showAdminHome(model, principal, response, null, session);

    assertEquals("index", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
    verify(model).addAttribute(eq("members"), eq(members));
    verify(model).addAttribute(eq("loggedInUser"), eq("admin@admin.com"));
  }

  @Test
  void showAdminHome_ShouldReturnIndexView_WithSessionUser() {
    when(memberService.getAllMembers()).thenReturn(new ArrayList<>());
    when(session.getAttribute("userEmail")).thenReturn("test@test.com");

    String viewName = memberController.showAdminHome(model, null, response, null, session);

    assertEquals("index", viewName);
    verify(model).addAttribute(eq("loggedInUser"), eq("test@test.com"));
  }

  @Test
  void showAdminHome_ShouldClearSession_WhenClearSessionIsTrue() {
    String viewName = memberController.showAdminHome(model, principal, response, "true", session);

    assertEquals("redirect:/admin/home", viewName);
    verify(session).removeAttribute("accessToken");
    verify(session).removeAttribute("refreshToken");
    verify(session).removeAttribute("userEmail");
    verify(session).removeAttribute("userRole");
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
  void redirectToJwtLogin_ShouldReturnRedirectToJwtLogin() {
    String viewName = memberController.redirectToJwtLogin();
    assertEquals("redirect:/jwt-login", viewName);
  }

  @Test
  void showUserProfile_ShouldReturnUserProfileView_WithAuthentication() {
    String email = "test@example.com";
    MemberDocument memberDocument = new MemberDocument("1", "John Doe", email, "1234567890",
        "password", "USER");

    when(authentication.getName()).thenReturn(email);
    when(memberService.findByEmail(email)).thenReturn(Optional.of(memberDocument));

    String viewName = memberController.showUserProfile(model, authentication, null, session);

    assertEquals("user-profile", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
  }

  @Test
  void showUserProfile_ShouldReturnUserProfileView_WithSessionUser() {
    String email = "test@example.com";
    MemberDocument memberDocument = new MemberDocument("1", "John Doe", email, "1234567890",
        "password", "USER");

    when(session.getAttribute("userEmail")).thenReturn(email);
    when(memberService.findByEmail(email)).thenReturn(Optional.of(memberDocument));

    String viewName = memberController.showUserProfile(model, null, null, session);

    assertEquals("user-profile", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
  }

  @Test
  void showUserProfile_ShouldReturnUserProfileView_WithDefaultMember() {
    when(session.getAttribute("userEmail")).thenReturn(null);

    String viewName = memberController.showUserProfile(model, null, null, session);

    assertEquals("user-profile", viewName);
    verify(model).addAttribute(eq("member"), any(Member.class));
  }

  @Test
  void showUserProfile_ShouldClearSession_WhenClearSessionIsTrue() {
    String viewName = memberController.showUserProfile(model, authentication, "true", session);

    assertEquals("redirect:/user-profile", viewName);
    verify(session).removeAttribute("accessToken");
    verify(session).removeAttribute("refreshToken");
    verify(session).removeAttribute("userEmail");
    verify(session).removeAttribute("userRole");
  }

  @Test
  void registerMember_WithInvalidData_ShouldReturnIndexView() {
    Member invalidMember = new Member();
    when(registrationContext.register(eq(invalidMember), eq(redirectAttributes))).thenReturn("index");

    String viewName = memberController.registerMember(invalidMember, "index", redirectAttributes);

    assertEquals("index", viewName);
    verify(registrationContext).setStrategy("index");
    verify(registrationContext).register(eq(invalidMember), eq(redirectAttributes));
  }

  @Test
  void error403_ShouldReturn403View() {
    String viewName = memberController.error403();
    assertEquals("error/403", viewName);
  }

  @Test
  void error401_ShouldReturn401View() {
    String viewName = memberController.error401();
    assertEquals("error/401", viewName);
  }
}
