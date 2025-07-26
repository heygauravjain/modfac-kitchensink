/*
package com.example.kitchensink.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.kitchensink.model.Member;
import com.example.kitchensink.service.MemberRegistrationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class MemberControllerTest {

  private MockMvc mockMvc;

  @Mock
  private MemberRegistrationService memberRegistrationService;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private MemberController memberController;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
  }

  @Test
  public void testShowRegistrationForm_UnauthenticatedUser() throws Exception {
    // Mock that user is not authenticated
    when(authentication.isAuthenticated()).thenReturn(false);

    mockMvc.perform(get("/members").principal(authentication))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/login"));
  }

  @Test
  public void testShowRegistrationForm_NotAdminUser() throws Exception {
    // Mock an authenticated user without ADMIN role
    UserDetails userDetails = new User("user", "password",
        List.of(new SimpleGrantedAuthority("ROLE_USER")));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    mockMvc.perform(get("/members").principal(authentication))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  public void testShowRegistrationForm_AdminUser() throws Exception {
    // Mock an authenticated user with ADMIN role
    UserDetails userDetails = new User("admin", "password",
        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getPrincipal()).thenReturn(userDetails);

    // Mock the service call to return a list of members
    List<Member> members = new ArrayList<>();
    members.add(new Member());
    when(memberRegistrationService.getAllMembers()).thenReturn(members);

    mockMvc.perform(get("/members").principal(authentication))
        .andExpect(status().isOk())
        .andExpect(view().name("index"))
        .andExpect(model().attributeExists("member"))
        .andExpect(model().attributeExists("members"));
  }

  @Test
  public void testRegisterMember_Success() throws Exception {
    // Prepare a valid member object
    Member member = new Member();
    member.setName("John Doe");
    member.setEmail("john@example.com");

    mockMvc.perform(post("/register")
            .flashAttr("member", member))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/members"));

    verify(memberRegistrationService, times(1)).registerMember(any(Member.class));
  }

  @Test
  public void testRegisterMember_Failure() throws Exception {
    // Prepare a member object and simulate an exception during registration
    Member member = new Member();
    member.setName("John Doe");
    member.setEmail("john@example.com");

    doThrow(new RuntimeException("Registration failed")).when(memberRegistrationService)
        .registerMember(any(Member.class));

    mockMvc.perform(post("/register")
            .flashAttr("member", member))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/members"));

    verify(memberRegistrationService, times(1)).registerMember(any(Member.class));
  }

  @Test
  public void testShowLoginPage() throws Exception {
    mockMvc.perform(get("/login"))
        .andExpect(status().isOk())
        .andExpect(view().name("login"));
  }
}
*/
