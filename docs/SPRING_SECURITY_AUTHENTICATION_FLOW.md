# Spring Security Authentication Flow - KitchenSink Project

## Table of Contents
1. [Overview](#overview)
2. [Authentication Architecture](#authentication-architecture)
3. [Request Flow Diagram](#request-flow-diagram)
4. [Detailed Authentication Process](#detailed-authentication-process)
5. [JWT Token Flow](#jwt-token-flow)
6. [Security Filters](#security-filters)
7. [Authentication Providers](#authentication-providers)
8. [User Details Service](#user-details-service)
9. [Authorization Process](#authorization-process)
10. [Error Handling](#error-handling)
11. [Configuration Details](#configuration-details)

## Overview

Spring Security in the KitchenSink project implements a **stateless JWT-based authentication system** with the following key components:

- **JWT Tokens**: Access tokens (15 min) and refresh tokens (7 days)
- **Custom Filters**: JWT validation filters
- **MongoDB Integration**: User storage and retrieval
- **Role-based Authorization**: ADMIN and USER roles
- **Stateless Sessions**: No server-side session storage

## Authentication Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client        │    │   Spring Boot   │    │   MongoDB       │
│   (Browser/App) │    │   Application   │    │   Database      │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          │ 1. HTTP Request      │                      │
          │ (with JWT token)     │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │   Security Filter Chain   │
                    │  ┌─────────────────────┐  │
                    │  │ JWT Filters         │  │
                    │  │ - JwtAuthenticationFilter│  │
                    │  │ - JwtCookieAuthenticationFilter│  │
                    │  └─────────────────────┘  │
                    │  ┌─────────────────────┐  │
                    │  │ Authorization       │  │
                    │  │ - Role-based access │  │
                    │  │ - URL protection    │  │
                    │  └─────────────────────┘  │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │   Controller/Service      │
                    │   - Business Logic       │
                    │   - Response Generation   │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │   Response to Client      │
                    │   (with new JWT if needed)│
                    └───────────────────────────┘
```

## Request Flow Diagram

### **Complete Authentication Flow**

```
1. Client Request
   ↓
2. Security Filter Chain
   ├── JwtAuthenticationFilter (Header token)
   ├── JwtCookieAuthenticationFilter (Cookie token)
   └── UsernamePasswordAuthenticationFilter
   ↓
3. Token Validation
   ├── Extract token from request
   ├── Validate token signature
   ├── Check token expiration
   └── Extract user information
   ↓
4. User Details Service
   ├── Load user from MongoDB
   ├── Create UserDetails object
   └── Set authorities (roles)
   ↓
5. Authentication Object
   ├── Create Authentication object
   ├── Set principal (user details)
   ├── Set authorities (roles)
   └── Set authenticated = true
   ↓
6. Security Context
   ├── Set Authentication in SecurityContext
   └── Make available to application
   ↓
7. Authorization Check
   ├── Check user roles
   ├── Verify URL permissions
   └── Allow/deny access
   ↓
8. Controller Execution
   ├── Execute business logic
   └── Return response
```

## Detailed Authentication Process

### **Step 1: Request Arrives**

When a client makes a request to a protected endpoint:

```java
// Example request to /admin/members
GET /admin/members
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### **Step 2: Security Filter Chain Processing**

The request goes through the configured security filter chain:

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")  // ← Authorization rule
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtCookieAuthenticationFilter, JwtAuthenticationFilter.class);
    
    return http.build();
}
```

### **Step 3: JWT Filter Processing**

#### **JwtAuthenticationFilter**
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Extract token from Authorization header
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 2. Extract JWT token
        final String jwt = authHeader.substring(7);
        
        // 3. Validate token and extract username
        final String username = jwtTokenService.extractUsername(jwt);
        
        // 4. If username exists and no authentication is set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 5. Load user details from database
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            
            // 6. Validate token
            if (jwtTokenService.isTokenValid(jwt, userDetails)) {
                
                // 7. Create authentication object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                
                // 8. Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

### **Step 4: User Details Service**

#### **CustomUserDetailsService**
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Find user in MongoDB
        MemberDocument member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        // 2. Create UserDetails object
        return User.builder()
            .username(member.getEmail())
            .password(member.getPassword())
            .authorities(member.getRole())  // e.g., "ROLE_ADMIN"
            .build();
    }
}
```

### **Step 5: Authorization Check**

After authentication, Spring Security checks authorization:

```java
// Security configuration
.requestMatchers("/admin/**").hasRole("ADMIN")  // ← Requires ADMIN role
.requestMatchers("/user/**").hasRole("USER")    // ← Requires USER role
.anyRequest().authenticated()                   // ← Requires any authentication
```

**Authorization Process**:
1. **Extract User Roles**: From Authentication object
2. **Check URL Pattern**: Match request URL to configured patterns
3. **Verify Role Requirements**: Check if user has required roles
4. **Grant/Deny Access**: Allow or redirect to error page

## JWT Token Flow

### **Token Generation (Login)**

```java
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    
    // 1. Authenticate user credentials
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );
    
    // 2. Get user details
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String role = userDetails.getAuthorities().stream()
        .findFirst()
        .map(Object::toString)
        .orElse("ROLE_USER");
    
    // 3. Generate JWT tokens
    String accessToken = jwtTokenService.generateAccessToken(userDetails.getUsername(), role);
    String refreshToken = jwtTokenService.generateRefreshToken(userDetails.getUsername());
    
    // 4. Return tokens to client
    return ResponseEntity.ok(new AuthResponse(
        accessToken, refreshToken, "Bearer", 
        jwtTokenService.getAccessTokenExpiration(),
        userDetails.getUsername(), role
    ));
}
```

### **Token Validation (Each Request)**

```java
public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
}

public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
}
```

### **Token Structure**

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user@example.com",
    "role": "ROLE_ADMIN",
    "iat": 1234567890,
    "exp": 1234567890
  },
  "signature": "HMACSHA256(base64UrlEncode(header) + '.' + base64UrlEncode(payload), secret)"
}
```

## Security Filters

### **Filter Chain Order**

```
1. JwtCookieAuthenticationFilter (Cookie-based tokens)
2. JwtAuthenticationFilter (Header-based tokens)
3. UsernamePasswordAuthenticationFilter (Form-based login)
4. Authorization filters
5. Controller execution
```

### **JwtCookieAuthenticationFilter**

```java
@Component
public class JwtCookieAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Extract token from cookies
        Cookie[] cookies = request.getCookies();
        String token = null;
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt-token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        
        // 2. Process token if found
        if (token != null) {
            // Similar validation logic as JwtAuthenticationFilter
        }
        
        filterChain.doFilter(request, response);
    }
}
```

## Authentication Providers

### **DaoAuthenticationProvider**

```java
@Bean
public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(customUserDetailsService);  // ← Load user details
    authProvider.setPasswordEncoder(passwordEncoder());            // ← Encrypt/verify passwords
    return authProvider;
}
```

**Responsibilities**:
- ✅ **User Loading**: Uses CustomUserDetailsService to load users
- ✅ **Password Verification**: Uses BCryptPasswordEncoder to verify passwords
- ✅ **Authentication Object Creation**: Creates Authentication object after successful verification

### **AuthenticationManager**

```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
}
```

**Responsibilities**:
- ✅ **Provider Coordination**: Manages multiple authentication providers
- ✅ **Authentication Flow**: Orchestrates the authentication process
- ✅ **Exception Handling**: Handles authentication failures

## User Details Service

### **CustomUserDetailsService Implementation**

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberRepository memberRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Find user in MongoDB
        MemberDocument member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        // 2. Create UserDetails with authorities
        return User.builder()
            .username(member.getEmail())
            .password(member.getPassword())
            .authorities(member.getRole())  // e.g., "ROLE_ADMIN", "ROLE_USER"
            .build();
    }
}
```

**Key Points**:
- ✅ **MongoDB Integration**: Loads users from MongoDB
- ✅ **Role Mapping**: Converts database roles to Spring Security authorities
- ✅ **Exception Handling**: Throws UsernameNotFoundException for missing users

## Authorization Process

### **URL-based Authorization**

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/", "/jwt-login", "/jwt-signup").permitAll()           // ← Public access
    .requestMatchers("/api/auth/**").permitAll()                              // ← Public API
    .requestMatchers("/admin/**").hasRole("ADMIN")                            // ← Admin only
    .requestMatchers("/user/**").hasRole("USER")                              // ← User only
    .anyRequest().authenticated()                                             // ← Any authenticated user
)
```

### **Method-based Authorization**

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/members")
public List<Member> getAllMembers() {
    return memberService.getAllMembers();
}

@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
@GetMapping("/user/profile")
public String getUserProfile() {
    return "user-profile";
}
```

### **Role Hierarchy**

```
ROLE_ADMIN > ROLE_USER
- Admin users can access user endpoints
- User users cannot access admin endpoints
```

## Error Handling

### **Authentication Entry Point**

```java
@Bean
public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, authException) -> {
        log.warn("Authentication failed for request: {}", request.getRequestURI());
        response.sendRedirect("/401");  // ← Redirect to 401 error page
    };
}
```

### **Access Denied Handler**

```java
@Bean
public AccessDeniedHandler accessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
        log.warn("Access denied for user: {}", 
            request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous");
        response.sendRedirect("/403");  // ← Redirect to 403 error page
    };
}
```

### **Error Scenarios**

1. **Invalid JWT Token**:
   - Token expired
   - Invalid signature
   - Malformed token
   - → Redirect to `/401`

2. **Insufficient Permissions**:
   - User tries to access admin endpoint
   - Missing required role
   - → Redirect to `/403`

3. **Missing Authentication**:
   - No JWT token provided
   - Token not in header or cookie
   - → Redirect to `/401`

## Configuration Details

### **Security Configuration**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)                    // ← Disable CSRF for API
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/jwt-login", "/jwt-signup").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // ← Stateless sessions
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtCookieAuthenticationFilter, JwtAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### **JWT Configuration**

```yaml
jwt:
  secret:
    key: Srgl71VAmMhSVI+8Bb5eQB6HFr3HdUbidBb/xoTWZAM=
  access:
    token:
      expiration: 900000  # 15 minutes
  refresh:
    token:
      expiration: 604800000  # 7 days
```

## Summary

The Spring Security authentication flow in KitchenSink provides:

1. **Stateless Authentication**: JWT-based, no server-side sessions
2. **Dual Token Support**: Header and cookie-based JWT tokens
3. **Role-based Authorization**: ADMIN and USER role support
4. **MongoDB Integration**: User storage and retrieval
5. **Comprehensive Error Handling**: Proper error pages and logging
6. **Security Best Practices**: CSRF disabled, CORS configured, proper token validation

This implementation ensures **secure, scalable, and maintainable** authentication for the application. 