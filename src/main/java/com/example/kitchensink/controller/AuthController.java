package com.example.kitchensink.controller;

import com.example.kitchensink.security.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {

  private final AuthenticationProvider authenticationProvider;
  private final JwtTokenUtil jwtTokenUtil;

  public AuthController(AuthenticationProvider authenticationProvider, JwtTokenUtil jwtTokenUtil) {
    this.authenticationProvider = authenticationProvider;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  /**
   * REST endpoint for user authentication and JWT token generation.
   * 
   * @param loginRequest the login credentials
   * @return JWT token and user information
   */
  @Operation(summary = "Authenticate user and get JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Authentication successful"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials"),
      @ApiResponse(responseCode = "400", description = "Invalid request data")
  })
  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> authenticateUser(
      @Valid @RequestBody LoginRequest loginRequest) {
    
    try {
      // Authenticate the user using AuthenticationProvider
      Authentication authentication = authenticationProvider.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(), 
              loginRequest.getPassword()
          )
      );

      // Get user details
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      
      // Generate JWT token
      String jwtToken = jwtTokenUtil.generateToken(userDetails.getUsername());
      
      // Prepare response
      Map<String, Object> response = new HashMap<>();
      response.put("timestamp", LocalDateTime.now());
      response.put("status", "success");
      response.put("message", "Authentication successful");
      response.put("token", jwtToken);
      response.put("tokenType", "Bearer");
      response.put("expiresIn", "3600000"); // 1 hour in milliseconds
      response.put("user", Map.of(
          "email", userDetails.getUsername(),
          "authorities", userDetails.getAuthorities()
      ));
      
      log.info("User {} authenticated successfully", userDetails.getUsername());
      
      return ResponseEntity.ok(response);
      
    } catch (Exception e) {
      log.error("Authentication failed for user: {}", loginRequest.getEmail(), e);
      
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", "error");
      errorResponse.put("message", "Invalid email or password");
      errorResponse.put("error", "Authentication failed");
      
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
  }

  /**
   * REST endpoint to validate JWT token.
   * 
   * @param tokenRequest the token to validate
   * @return validation result
   */
  @Operation(summary = "Validate JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Token is valid"),
      @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
  })
  @PostMapping("/validate")
  public ResponseEntity<Map<String, Object>> validateToken(
      @Valid @RequestBody TokenRequest tokenRequest) {
    
    try {
      String username = jwtTokenUtil.extractUsername(tokenRequest.getToken());
      
      if (username != null && !jwtTokenUtil.isTokenExpired(tokenRequest.getToken())) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "valid");
        response.put("message", "Token is valid");
        response.put("username", username);
        
        return ResponseEntity.ok(response);
      } else {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", "invalid");
        errorResponse.put("message", "Token is invalid or expired");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
      }
      
    } catch (Exception e) {
      log.error("Token validation failed", e);
      
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("timestamp", LocalDateTime.now());
      errorResponse.put("status", "error");
      errorResponse.put("message", "Token validation failed");
      errorResponse.put("error", e.getMessage());
      
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
  }

  // Request DTOs
  public static class LoginRequest {
    private String email;
    private String password;

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
  }

  public static class TokenRequest {
    private String token;

    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
  }
} 