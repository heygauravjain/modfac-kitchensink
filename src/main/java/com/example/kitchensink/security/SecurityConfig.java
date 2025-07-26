package com.example.kitchensink.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

  private final CustomAccessDeniedHandler customAccessDeniedHandler;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
      CustomAccessDeniedHandler customAccessDeniedHandler) {
    this.customUserDetailsService = customUserDetailsService;
    this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    this.customAccessDeniedHandler = customAccessDeniedHandler;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .frameOptions(FrameOptionsConfig::sameOrigin) //Set X-Frame-Options to same origin
        )
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/register")
            .permitAll()
            .requestMatchers("/members").hasRole("ADMIN")
            .requestMatchers("/rest/members/**").hasRole("ADMIN")
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
        ).httpBasic(Customizer.withDefaults());
    ;

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

        // Check if the user has the ADMIN role and redirect accordingly
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
          response.sendRedirect("/members");
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
          response.sendRedirect("/user-profile"); // Redirect to user profile page for USER
        } else {
          response.sendRedirect("/login"); // Default redirect for other roles (or no roles)
        }
      }
    };
  }
}
