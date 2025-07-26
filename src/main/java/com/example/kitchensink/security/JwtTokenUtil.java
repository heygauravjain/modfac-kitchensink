package com.example.kitchensink.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {

  // Load the secret key and expiration time from environment variables or properties file
  @Value("${jwt.secret.key}")
  private String secretKeyString;

  @Value("${jwt.token.expiration}")
  private long expirationTime;

  // Generate a SecretKey using the Keys utility class from the secret string
  private SecretKey getKey() {
    return Keys.hmacShaKeyFor(secretKeyString.getBytes());
  }

  // Method to generate the JWT token
  public String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, username);
  }

  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Token validity
        .signWith(getKey(), SignatureAlgorithm.HS256) // Use the key and algorithm here
        .compact();
  }

  // Method to extract the username from the JWT token
  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  // Method to extract all claims from the JWT token
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getKey()) // Set the key for parsing the JWT
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  // Method to validate the JWT token
  public boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  // Method to check if the token is expired
  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }
}
