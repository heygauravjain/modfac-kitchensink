package com.example.kitchensink.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  public CustomAuthenticationEntryPoint() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null) {
      log.error("User '" + auth.getName()
          + "' is not authorized to access the protected URL: "
          + request.getRequestURI());
    }

    // Check if this is an API request (based on Accept header or path)
    boolean isApiRequest = isApiRequest(request);
    
    if (isApiRequest) {
      // Return JSON response for API requests
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
      errorResponse.put("error", "Unauthorized");
      errorResponse.put("message", "Authentication required");
      errorResponse.put("path", request.getRequestURI());
      
      objectMapper.writeValue(response.getOutputStream(), errorResponse);
    } else {
      // Redirect to HTML page for web requests
      response.sendRedirect(request.getContextPath() + "/401");
    }
  }
  
  /**
   * Determines if the request is an API request based on headers or path.
   */
  private boolean isApiRequest(HttpServletRequest request) {
    String acceptHeader = request.getHeader("Accept");
    String userAgent = request.getHeader("User-Agent");
    String requestPath = request.getRequestURI();
    
    // Check Accept header for JSON preference first (highest priority)
    if (acceptHeader != null && 
        (acceptHeader.contains("application/json") || 
         acceptHeader.contains("*/*"))) {
      return true;
    }
    
    // Check if it's a Swagger UI request
    if (userAgent != null && userAgent.contains("Swagger")) {
      return true;
    }
    
    // Check if it's a Swagger/API documentation request
    if (requestPath != null && (requestPath.startsWith("/swagger-ui") || 
        requestPath.startsWith("/v3/api-docs"))) {
      return true;
    }
    
    // For admin paths, check if Accept header indicates HTML preference
    if (requestPath != null && requestPath.startsWith("/admin/")) {
      if (acceptHeader != null && acceptHeader.contains("text/html")) {
        return false; // Treat as web request
      }
      // Default to API for admin paths unless HTML is explicitly requested
      return true;
    }
    
    return false;
  }
}
