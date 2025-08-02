package com.example.kitchensink.config;

import com.example.kitchensink.security.CustomUserDetailsService;
import com.example.kitchensink.security.JwtTokenService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;

@TestConfiguration
public class TestApplicationConfig {

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public JwtTokenService jwtTokenService() {
        return new JwtTokenService();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        // For testing, we'll use a mock implementation
        return new org.springframework.security.core.userdetails.UserDetailsService() {
            @Override
            public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws org.springframework.security.core.userdetails.UsernameNotFoundException {
                // Return a mock user for testing
                return org.springframework.security.core.userdetails.User.builder()
                        .username(username)
                        .password("password")
                        .authorities(Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER")))
                        .build();
            }
        };
    }
} 