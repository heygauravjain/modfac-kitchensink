package com.example.kitchensink.security;

import com.example.kitchensink.entity.MemberEntity;
import com.example.kitchensink.repository.MemberRepository;
import java.util.Collections;
import java.util.Set;
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

  private final MemberRepository memberRepository;

  @Autowired
  public CustomUserDetailsService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // Log the email being used for authentication
    System.out.println("Attempting to authenticate email: " + email);

    // Find user by email
    MemberEntity memberEntity = memberRepository.findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException("User not found with username: " + email));

    System.out.println("User found: " + memberEntity.getEmail());
    System.out.println("User role: " + memberEntity.getRole());

    // Convert the single role to a Set of GrantedAuthority
    Set<GrantedAuthority> authorities = Collections.singleton(
        new SimpleGrantedAuthority("ROLE_" + memberEntity.getRole()));

    // Return a UserDetails object with member information
    return new User(memberEntity.getEmail(), memberEntity.getPassword(), authorities);

  }
}
