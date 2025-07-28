package com.example.kitchensink.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Slf4j
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  private final JwtRequestFilter jwtRequestFilter;


  private final JwtTokenUtil jwtTokenUtil;
  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
      JwtRequestFilter jwtRequestFilter, JwtTokenUtil jwtTokenUtil,
      CustomAccessDeniedHandler customAccessDeniedHandler) {
    this.customUserDetailsService = customUserDetailsService;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.jwtRequestFilter = jwtRequestFilter;
    this.jwtTokenUtil = jwtTokenUtil;
    this.customAccessDeniedHandler = customAccessDeniedHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin)
        )
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/register","/swagger-ui/**", "/swagger-ui.html",
             "/v3/api-docs/**", "/v3/api-docs", "/v3/api-docs/swagger-config", "/401", "/api/auth/**").permitAll()
            .requestMatchers("/admin/home").hasRole("ADMIN")
            .requestMatchers("/admin/members/**").hasRole("ADMIN")
            .requestMatchers("/user-profile").hasRole("USER")
            .anyRequest().authenticated()
        )
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(customAuthenticationEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
        )
        .formLogin(form -> form
            .loginPage("/login")
            .successHandler(customAuthenticationSuccessHandler())
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login")
            .permitAll()
        )
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtRequestFilter,
            UsernamePasswordAuthenticationFilter.class); // Add JWT filter

    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * Custom Authentication Success Handler for role-based redirection.
   */
  @Bean
  public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
    return new AuthenticationSuccessHandler() {
      @Override
      public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
          org.springframework.security.core.Authentication authentication)
          throws IOException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate JWT token using the authenticated user's username
        String jwtToken = jwtTokenUtil.generateToken(userDetails.getUsername());

        // Add the JWT token to the response header
        response.setHeader("Authorization", "Bearer " + jwtToken);

        log.info("token-----{}", jwtToken);

        // Check if the user has the ADMIN role and redirect accordingly
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
          response.sendRedirect("/admin/home");
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
          response.sendRedirect("/user-profile"); // Redirect to user profile page for USER
        } else {
          response.sendRedirect("/login"); // Default redirect for other roles (or no roles)
        }
      }
    };
  }
}
