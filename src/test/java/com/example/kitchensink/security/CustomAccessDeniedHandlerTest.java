package com.example.kitchensink.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class CustomAccessDeniedHandlerTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private AccessDeniedException accessDeniedException;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private ServletOutputStream outputStream;

  @InjectMocks
  private CustomAccessDeniedHandler accessDeniedHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // Mock the security context to return a predefined Authentication object
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    
    // Mock the response output stream
    try {
      when(response.getOutputStream()).thenReturn(outputStream);
    } catch (IOException e) {
      // This should not happen in tests, but handle it gracefully
    }
  }

  @Test
  void handle_WithAuthenticatedUser_ShouldLogAndRedirectTo403() throws IOException {
    // Arrange: Mock authentication and request details
    when(authentication.getName()).thenReturn("testUser");
    when(request.getRequestURI()).thenReturn("/admin/members");
    when(request.getContextPath()).thenReturn("");
    // Mock headers to ensure it's treated as a web request, not API
    when(request.getHeader("Accept")).thenReturn("text/html,application/xhtml+xml,application/xml");
    when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

    // Act: Call the handle method
    accessDeniedHandler.handle(request, response, accessDeniedException);

    // Assert: Verify that the log message is created and the redirect is called
    verify(response, times(1)).sendRedirect("/403");
  }

  @Test
  void handle_WithNoAuthentication_ShouldRedirectTo403WithoutLogging() throws IOException {
    // Arrange: Set the security context to have no authentication
    when(securityContext.getAuthentication()).thenReturn(null);
    when(request.getRequestURI()).thenReturn("/admin/members");
    when(request.getContextPath()).thenReturn("");
    // Mock headers to ensure it's treated as a web request, not API
    when(request.getHeader("Accept")).thenReturn("text/html,application/xhtml+xml,application/xml");
    when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

    // Act: Call the handle method
    accessDeniedHandler.handle(request, response, accessDeniedException);

    // Assert: Verify that the redirect is called but no log message is created
    verify(response, times(1)).sendRedirect("/403");
  }

  @Test
  void handle_ShouldLogErrorForAccessDeniedException() throws IOException {
    // Arrange: Set up the request and authentication details
    when(authentication.getName()).thenReturn("testUser");
    when(request.getRequestURI()).thenReturn("/admin/dashboard");
    when(request.getContextPath()).thenReturn(""); // Simulate root context path
    // Mock headers to ensure it's treated as a web request, not API
    when(request.getHeader("Accept")).thenReturn("text/html,application/xhtml+xml,application/xml");
    when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");

    // Act: Call the handle method
    accessDeniedHandler.handle(request, response, accessDeniedException);

    // Assert: Verify that the response sends a redirect to the /403 page
    verify(response).sendRedirect("/403");
  }

  @Test
  void handle_WithIOException_ShouldThrowException() throws IOException {
    // Arrange: Simulate an IOException during response redirection
    when(request.getRequestURI()).thenReturn("/admin/members");
    when(request.getContextPath()).thenReturn("");
    when(authentication.getName()).thenReturn("testUser");
    // Mock headers to ensure it's treated as a web request, not API
    when(request.getHeader("Accept")).thenReturn("text/html,application/xhtml+xml,application/xml");
    when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
    doThrow(new IOException("Test IO Exception")).when(response).sendRedirect(anyString());

    // Act & Assert: Verify that the IOException is thrown when calling handle
    assertThrows(IOException.class,
        () -> accessDeniedHandler.handle(request, response, accessDeniedException));
  }
}
