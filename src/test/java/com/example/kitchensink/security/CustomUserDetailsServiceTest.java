package com.example.kitchensink.security;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private MemberDocument memberDocument;

    @BeforeEach
    void setUp() {
        memberDocument = new MemberDocument();
        memberDocument.setId("1");
        memberDocument.setName("Test User");
        memberDocument.setEmail("test@example.com");
        memberDocument.setPassword("encodedPassword");
        memberDocument.setRole("ROLE_USER");
    }

    @Test
    void loadUserByUsername_WithValidEmail_ShouldReturnUserDetails() {
        // Given
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(memberDocument));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_WithInvalidEmail_ShouldThrowException() {
        // Given
        when(memberRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("invalid@example.com"));
        assertEquals("User not found with email: invalid@example.com", exception.getMessage());
    }

    @Test
    void loadUserByUsername_WithAdminRole_ShouldReturnUserDetailsWithAdminAuthority() {
        // Given
        MemberDocument adminMember = new MemberDocument();
        adminMember.setId("2");
        adminMember.setName("Admin User");
        adminMember.setEmail("admin@example.com");
        adminMember.setPassword("encodedPassword");
        adminMember.setRole("ROLE_ADMIN");
        
        when(memberRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminMember));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("admin@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_WithUserRole_ShouldReturnUserDetailsWithUserAuthority() {
        // Given
        MemberDocument userMember = new MemberDocument();
        userMember.setId("3");
        userMember.setName("Regular User");
        userMember.setEmail("user@example.com");
        userMember.setPassword("encodedPassword");
        userMember.setRole("ROLE_USER");
        
        when(memberRepository.findByEmail("user@example.com")).thenReturn(Optional.of(userMember));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }
}
