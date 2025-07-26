package com.example.kitchensink.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {

    Authentication auth
        = SecurityContextHolder.getContext().getAuthentication();

    if (auth != null) {
      log.error("User '" + auth.getName()
          + "' attempted to access the protected URL: "
          + request.getRequestURI());
    }

    response.sendRedirect(request.getContextPath() + "/403");
  }
}
