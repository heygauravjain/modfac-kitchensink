package com.example.kitchensink.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberService;
import com.example.kitchensink.repository.MemberRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class MemberControllerTest {

  @Mock
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

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
  void registerMember_WhenSourceIsIndex_AndUserExists_ShouldReturnError() {
    Member member = new Member();
    member.setEmail("test@test.com");
    
    MemberDocument existingMember = new MemberDocument();
    existingMember.setEmail("test@test.com");
    existingMember.setPassword("encodedPassword");
    
    when(memberService.findByEmail("test@test.com")).thenReturn(Optional.of(existingMember));

    String viewName = memberController.registerMember(member, "index", redirectAttributes);

    assertEquals("redirect:/admin/home", viewName);
    verify(redirectAttributes).addFlashAttribute("registrationError", true);
    verify(redirectAttributes).addFlashAttribute("errorMessage", "Member already registered with this email!");
  }

  @Test
  void registerMember_WhenSourceIsIndex_AndNewUser_ShouldRegisterSuccessfully() {
    Member member = new Member();
    member.setEmail("new@test.com");
    
    when(memberService.findByEmail("new@test.com")).thenReturn(Optional.empty());
    when(memberService.registerMember(any(Member.class))).thenReturn(member);

    String viewName = memberController.registerMember(member, "index", redirectAttributes);

    assertEquals("redirect:/admin/home", viewName);
    verify(memberService).registerMember(member);
    verify(redirectAttributes).addFlashAttribute("registrationSuccess", true);
    verify(redirectAttributes).addFlashAttribute("successMessage", "Member successfully registered!");
  }

  @Test
  void registerMember_WhenSourceIsRegister_AndUserExistsWithoutPassword_ShouldUpdatePassword() {
    Member member = new Member();
    member.setEmail("test@test.com");
    member.setPassword("newPassword");
    
    MemberDocument existingMember = new MemberDocument();
    existingMember.setEmail("test@test.com");
    existingMember.setPassword(null); // No password set
    
    when(memberService.findByEmail("test@test.com")).thenReturn(Optional.of(existingMember));
    when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

    String viewName = memberController.registerMember(member, "register", redirectAttributes);

    assertEquals("redirect:/jwt-login", viewName);
    verify(memberRepository).save(existingMember);
    verify(redirectAttributes).addFlashAttribute("registrationSuccess", true);
    verify(redirectAttributes).addFlashAttribute("successMessage", "Password updated successfully!");
  }

  @Test
  void registerMember_WhenSourceIsRegister_AndUserExistsWithPassword_ShouldReturnError() {
    Member member = new Member();
    member.setEmail("test@test.com");
    
    MemberDocument existingMember = new MemberDocument();
    existingMember.setEmail("test@test.com");
    existingMember.setPassword("existingPassword");
    
    when(memberService.findByEmail("test@test.com")).thenReturn(Optional.of(existingMember));

    String viewName = memberController.registerMember(member, "register", redirectAttributes);

    assertEquals("redirect:/jwt-login", viewName);
    verify(redirectAttributes).addFlashAttribute("registrationError", true);
    verify(redirectAttributes).addFlashAttribute("errorMessage", "Account already exists with this email. Please log in.");
  }

  @Test
  void registerMember_WhenSourceIsRegister_AndNewUser_ShouldRegisterSuccessfully() {
    Member member = new Member();
    member.setEmail("new@test.com");
    member.setPassword("password");
    
    when(memberService.findByEmail("new@test.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
    when(memberService.registerMember(any(Member.class))).thenReturn(member);

    String viewName = memberController.registerMember(member, "register", redirectAttributes);

    assertEquals("redirect:/jwt-login", viewName);
    verify(memberService).registerMember(any(Member.class));
    verify(redirectAttributes).addFlashAttribute("registrationSuccess", true);
    verify(redirectAttributes).addFlashAttribute("successMessage", "User successfully registered!");
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
