package com.example.kitchensink.security;

import com.example.kitchensink.entity.MemberDocument;
import com.example.kitchensink.repository.MemberRepository;
import java.util.Collections;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Autowired
  public CustomUserDetailsService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Log the email being used for authentication
    log.info("Attempting to authenticate email: {}", email);

    // Find user by email
    MemberDocument memberDocument = memberRepository.findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with email: " + email));

    log.info("User found: {}", memberDocument.getEmail());
    log.info("User role: {}", memberDocument.getRole());

    // Convert the single role to a Set of GrantedAuthority
    Set<GrantedAuthority> authorities = Collections.singleton(
        new SimpleGrantedAuthority("ROLE_" + memberDocument.getRole()));

    // Return a UserDetails object with member information
    return new User(memberDocument.getEmail(), memberDocument.getPassword(), authorities);

  }
}
