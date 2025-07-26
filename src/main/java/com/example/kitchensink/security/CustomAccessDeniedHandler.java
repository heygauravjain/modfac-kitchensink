package com.example.kitchensink.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.FORBIDDEN.value());

    Map<String, Object> responseBody = new HashMap<>();
    responseBody.put("timestamp", System.currentTimeMillis());
    responseBody.put("status", HttpStatus.FORBIDDEN.value());
    responseBody.put("error", "Forbidden");
    responseBody.put("message", accessDeniedException.getMessage());

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), responseBody);
  }
}
