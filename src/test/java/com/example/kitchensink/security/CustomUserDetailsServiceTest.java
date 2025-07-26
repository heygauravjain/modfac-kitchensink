package com.example.kitchensink.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private CustomUserDetailsService customUserDetailsService;

  private MemberDocument mockMember;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Initialize a mock member for testing
    mockMember = new MemberDocument();
    mockMember.setId("1");
    mockMember.setName("John Doe");
    mockMember.setEmail("john.doe@example.com");
    mockMember.setPassword("password123");
    mockMember.setRole("USER");
  }

  @Test
  void loadUserByUsername_WithValidEmail_ShouldReturnUserDetails() {
    // Arrange: Mock the repository to return the mock member for the given email
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mockMember));

    // Act: Call the method with a valid email
    UserDetails userDetails = customUserDetailsService.loadUserByUsername("john.doe@example.com");

    // Assert: Verify that UserDetails is returned with the correct email and role
    assertNotNull(userDetails);
    assertEquals(mockMember.getEmail(), userDetails.getUsername());
    assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
    verify(memberRepository, times(1)).findByEmail("john.doe@example.com");
  }

  @Test
  void loadUserByUsername_WithInvalidEmail_ShouldThrowUsernameNotFoundException() {
    // Arrange: Mock the repository to return an empty Optional for the given email
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // Act & Assert: Call the method with an invalid email and verify exception is thrown
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserByUsername("unknown@example.com"));

    assertEquals("User not found with email: unknown@example.com", exception.getMessage());
    verify(memberRepository, times(1)).findByEmail("unknown@example.com");
  }

  @Test
  void loadUserByUsername_WithNullEmail_ShouldThrowUsernameNotFoundException() {
    // Act & Assert: Call the method with a null email and verify exception is thrown
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserByUsername(null));

    assertEquals("User not found with email: null", exception.getMessage());
    verify(memberRepository, times(1)).findByEmail(null);
  }

  @Test
  void loadUserByUsername_WithEmptyEmail_ShouldThrowUsernameNotFoundException() {
    // Act & Assert: Call the method with an empty email and verify exception is thrown
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
        () -> customUserDetailsService.loadUserByUsername(""));

    assertEquals("User not found with email: ", exception.getMessage());
    verify(memberRepository, times(1)).findByEmail("");
  }

  @Test
  void loadUserByUsername_ShouldConvertRoleToGrantedAuthority() {
    // Arrange: Mock the repository to return a member with a different role
    mockMember.setRole("ADMIN");
    when(memberRepository.findByEmail(anyString())).thenReturn(Optional.of(mockMember));

    // Act: Call the method and verify the role is correctly converted to GrantedAuthority
    UserDetails userDetails = customUserDetailsService.loadUserByUsername("john.doe@example.com");

    assertNotNull(userDetails);
    assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    verify(memberRepository, times(1)).findByEmail("john.doe@example.com");
  }

}
