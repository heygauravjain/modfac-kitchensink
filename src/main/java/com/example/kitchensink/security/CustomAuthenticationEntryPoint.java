package com.example.kitchensink.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    Authentication auth
        = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null) {
      log.error("User '" + auth.getName()
          + "' is not authorized to access the protected URL: "
          + request.getRequestURI());
    }

    response.sendRedirect(request.getContextPath() + "/401");
  }
}
