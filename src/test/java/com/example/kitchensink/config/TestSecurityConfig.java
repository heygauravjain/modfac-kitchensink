package com.example.kitchensink.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.kitchensink.security.JwtAuthenticationFilter;
import com.example.kitchensink.security.JwtTokenService;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.and())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/gfx/**", "/images/**", "/favicon.ico", "/favicon.png").permitAll() // Allow static resources
                .requestMatchers("/error/**", "/error").permitAll() // Allow error endpoints
                .requestMatchers("/jwt/login", "/jwt/signup", "/jwt/logout").permitAll() // Allow JWT auth endpoints
                .requestMatchers("/auth/**", "/api/auth/**").permitAll() // Allow auth endpoints
                .requestMatchers("/api/public/**").permitAll() // Allow public API endpoints
                .anyRequest().authenticated() // Require authentication for all other requests
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtTokenService jwtTokenService() {
        return new JwtTokenService();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenService jwtTokenService, com.example.kitchensink.security.CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtTokenService, userDetailsService);
    }
} 