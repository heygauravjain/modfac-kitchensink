package com.example.kitchensink.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("timestamp", System.currentTimeMillis());
    responseBody.put("status", HttpStatus.UNAUTHORIZED.value());
    responseBody.put("error", "Unauthorized");
    responseBody.put("message", authException.getMessage());

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), responseBody);
  }
}
