package com.example.kitchensink.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class CustomAuthenticationEntryPointTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @InjectMocks
  private CustomAuthenticationEntryPoint authenticationEntryPoint;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Set up mock SecurityContext
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void commence_WithAuthenticatedUser_ShouldLogAndRedirectTo401() throws IOException {
    // Arrange: Set up the mock request, response, and authentication objects
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("testUser");
    when(request.getRequestURI()).thenReturn("/admin/protected-resource");
    when(request.getContextPath()).thenReturn("");

    // Act: Call the commence method of the entry point
    authenticationEntryPoint.commence(request, response, null);

    // Assert: Verify that the log message is generated and the redirect is made to /401
    verify(response, times(1)).sendRedirect("/401");
  }

  @Test
  void commence_WithNoAuthentication_ShouldRedirectTo401WithoutLogging() throws IOException {
    // Arrange: Set up the security context to have no authentication
    when(securityContext.getAuthentication()).thenReturn(null);
    when(request.getRequestURI()).thenReturn("/admin/protected-resource");
    when(request.getContextPath()).thenReturn("");

    // Act: Call the commence method of the entry point
    authenticationEntryPoint.commence(request, response, null);

    // Assert: Verify that the redirect is made to /401 without logging the message
    verify(response, times(1)).sendRedirect("/401");
  }

  @Test
  void commence_WithIOException_ShouldThrowIOException() throws IOException {
    // Arrange: Set up the mock request and response
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("testUser");
    when(request.getRequestURI()).thenReturn("/admin/protected-resource");
    when(request.getContextPath()).thenReturn("");

    // Simulate IOException when calling sendRedirect
    doThrow(new IOException("Test IOException")).when(response).sendRedirect(anyString());

    // Act & Assert: Call the commence method and expect IOException to be thrown
    assertThrows(IOException.class,
        () -> authenticationEntryPoint.commence(request, response, null));
  }

  @Test
  void commence_WithNullAuthException_ShouldRedirectTo401() throws IOException {
    // Arrange: Set up the request to have no exception and no authentication
    when(securityContext.getAuthentication()).thenReturn(null);
    when(request.getRequestURI()).thenReturn("/admin/members");
    when(request.getContextPath()).thenReturn("");

    // Act: Call the commence method of the entry point
    authenticationEntryPoint.commence(request, response, null);

    // Assert: Verify that the response redirects to /401
    verify(response).sendRedirect("/401");
  }

  @Test
  void commence_ShouldHandleNonRootContextPath() throws IOException {
    // Arrange: Set up the request with a context path
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("testUser");
    when(request.getRequestURI()).thenReturn("/admin/members");
    when(request.getContextPath()).thenReturn("/app");

    // Act: Call the commence method of the entry point
    authenticationEntryPoint.commence(request, response, null);

    // Assert: Verify that the response redirects to the correct URL with context path
    verify(response).sendRedirect("/app/401");
  }
}
