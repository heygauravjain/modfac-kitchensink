package com.example.kitchensink.security;

import com.example.kitchensink.entity.UserEntity;
import com.example.kitchensink.repository.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Autowired
  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Log the username being used for authentication
    System.out.println("Attempting to authenticate user: " + username);

    // Find user by username
    UserEntity userEntity = userRepository.findByUsername(username)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + username));

    System.out.println("User found: " + userEntity.getUsername());
    System.out.println("User roles: " + userEntity.getRoles());

    // Convert roles to GrantedAuthority format
    Set<GrantedAuthority> authorities = userEntity.getRoles().stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());

    return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.isEnabled(),
        true, true, true, authorities);
  }
}
