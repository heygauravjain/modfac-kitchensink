# Spring Boot Annotations Guide - KitchenSink Project

## Overview

This document provides a comprehensive explanation of all Spring Boot annotations used in the KitchenSink application. The annotations are categorized by their purpose and usage context.

## Table of Contents

1. [Core Spring Boot Annotations](#core-spring-boot-annotations)
2. [Spring Security Annotations](#spring-security-annotations)
3. [Spring Data MongoDB Annotations](#spring-data-mongodb-annotations)
4. [Spring Web Annotations](#spring-web-annotations)
5. [Swagger/OpenAPI Annotations](#swaggeropenapi-annotations)
6. [Validation Annotations](#validation-annotations)
7. [Lombok Annotations](#lombok-annotations)
8. [Testing Annotations](#testing-annotations)
9. [Configuration Annotations](#configuration-annotations)

---

## Core Spring Boot Annotations

### **@SpringBootApplication**
**Location**: `KitchenSinkApplication.java`
```java
@SpringBootApplication
public class KitchenSinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(KitchenSinkApplication.class, args);
    }
}
```
**Purpose**: 
- Main annotation that marks a class as a Spring Boot application
- Combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`
- Enables auto-configuration and component scanning
- Entry point for Spring Boot applications

---

## Spring Security Annotations

### **@EnableWebSecurity**
**Location**: `SecurityConfig.java`
```java
@EnableWebSecurity
public class SecurityConfig {
    // Security configuration
}
```
**Purpose**:
- Enables Spring Security web security support
- Required for web-based security configuration
- Allows customization of security settings

### **@EnableMethodSecurity**
**Location**: `SecurityConfig.java`
```java
@EnableMethodSecurity
public class SecurityConfig {
    // Method-level security configuration
}
```
**Purpose**:
- Enables method-level security using annotations like `@PreAuthorize`
- Allows securing individual methods with role-based access
- Supports expression-based access control

### **@Bean**
**Location**: `SecurityConfig.java`, `SwaggerConfig.java`
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // Security configuration
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
**Purpose**:
- Indicates that a method produces a bean to be managed by Spring container
- Used in `@Configuration` classes to define beans
- Spring will manage the lifecycle of these beans

---

## Spring Data MongoDB Annotations

### **@Document**
**Location**: `MemberDocument.java`
```java
@Document(collection = "members")
public class MemberDocument {
    // Entity fields
}
```
**Purpose**:
- Marks a class as a MongoDB document
- Specifies the collection name in MongoDB
- Maps Java class to MongoDB document structure

### **@Id**
**Location**: `MemberDocument.java`
```java
@Id
private String id;
```
**Purpose**:
- Marks a field as the primary key in MongoDB
- Automatically generates ID if not provided
- Maps to MongoDB's `_id` field

### **@Indexed**
**Location**: `MemberDocument.java`
```java
@Indexed(unique = true)
private String email;
```
**Purpose**:
- Creates database indexes for better query performance
- `unique = true` ensures email uniqueness
- Improves search and validation performance

### **@Repository**
**Location**: `MemberRepository.java`
```java
@Repository
public interface MemberRepository extends MongoRepository<MemberDocument, String> {
    // Repository methods
}
```
**Purpose**:
- Marks an interface as a Spring Data repository
- Enables automatic implementation generation
- Provides data access layer functionality

---

## Spring Web Annotations

### **@Controller**
**Location**: `MemberController.java`, `JwtAuthController.java`
```java
@Controller
public class MemberController {
    // Controller methods
}
```
**Purpose**:
- Marks a class as a Spring MVC controller
- Handles HTTP requests and returns view names
- Used for web page rendering with Thymeleaf

### **@RestController**
**Location**: `RestService.java`
```java
@RestController
@RequestMapping("/admin/members")
public class RestService {
    // REST API endpoints
}
```
**Purpose**:
- Marks a class as a REST controller
- Combines `@Controller` and `@ResponseBody`
- Returns JSON/XML responses instead of view names

### **@RequestMapping**
**Location**: `RestService.java`
```java
@RequestMapping("/admin/members")
public class RestService {
    // Base path for all endpoints in this controller
}
```
**Purpose**:
- Maps web requests to handler methods
- Defines base URL path for controller
- Can be applied at class or method level

### **@GetMapping**
**Location**: Multiple controllers
```java
@GetMapping("/user-profile")
public String showUserProfile(Model model, Authentication authentication) {
    // Handle GET request
}
```
**Purpose**:
- Maps HTTP GET requests to handler methods
- Shorthand for `@RequestMapping(method = RequestMethod.GET)`
- Used for retrieving data

### **@PostMapping**
**Location**: Multiple controllers
```java
@PostMapping("/jwt-login")
public String jwtLogin(@Valid @ModelAttribute AuthRequest request) {
    // Handle POST request
}
```
**Purpose**:
- Maps HTTP POST requests to handler methods
- Used for creating new resources
- Handles form submissions and API calls

### **@PutMapping**
**Location**: `RestService.java`
```java
@PutMapping("/{id}")
public ResponseEntity<Member> updateMember(@Valid @RequestBody Member updatedMember,
                                         @PathVariable("id") String id) {
    // Handle PUT request
}
```
**Purpose**:
- Maps HTTP PUT requests to handler methods
- Used for updating existing resources
- Typically requires full resource data

### **@DeleteMapping**
**Location**: `RestService.java`
```java
@DeleteMapping("/{id}")
public ResponseEntity<String> deleteMemberById(@PathVariable("id") String id) {
    // Handle DELETE request
}
```
**Purpose**:
- Maps HTTP DELETE requests to handler methods
- Used for deleting resources
- Returns success/error responses

### **@PathVariable**
**Location**: Multiple controllers
```java
public ResponseEntity<Member> lookupMemberById(@PathVariable("id") String id) {
    // Extract path variable
}
```
**Purpose**:
- Extracts values from URL path segments
- Maps URL parameters to method parameters
- Supports path template variables like `{id}`

### **@RequestParam**
**Location**: Multiple controllers
```java
public String showJwtLoginPage(@RequestParam(value = "message", required = false) String message) {
    // Extract query parameter
}
```
**Purpose**:
- Extracts query parameters from URL
- Maps URL query parameters to method parameters
- Supports optional parameters with `required = false`

### **@RequestBody**
**Location**: `RestService.java`
```java
public ResponseEntity<Member> registerMember(@Valid @RequestBody Member member) {
    // Extract JSON body
}
```
**Purpose**:
- Extracts HTTP request body content
- Deserializes JSON/XML to Java objects
- Used for complex data structures

### **@ModelAttribute**
**Location**: Multiple controllers
```java
public String jwtLogin(@Valid @ModelAttribute AuthRequest request) {
    // Extract form data
}
```
**Purpose**:
- Extracts form data or query parameters
- Binds request parameters to object properties
- Used for form handling and data binding

---

## Swagger/OpenAPI Annotations

### **@Tag**
**Location**: `RestService.java`
```java
@Tag(name = "Member Management", description = "APIs for managing members")
public class RestService {
    // API documentation
}
```
**Purpose**:
- Groups related API endpoints in Swagger UI
- Provides category and description for API documentation
- Organizes endpoints by functionality

### **@Operation**
**Location**: `RestService.java`
```java
@Operation(summary = "List All Members")
public ResponseEntity<List<Member>> listAllMembers() {
    // API endpoint
}
```
**Purpose**:
- Documents individual API endpoints
- Provides summary and description for Swagger UI
- Describes what the endpoint does

### **@ApiResponses**
**Location**: `RestService.java`
```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of members"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
```
**Purpose**:
- Documents possible response codes and descriptions
- Provides comprehensive API documentation
- Helps developers understand expected responses

### **@ApiResponse**
**Location**: `RestService.java`
```java
@ApiResponse(responseCode = "200", description = "Successfully retrieved list of members")
```
**Purpose**:
- Documents a specific HTTP response code
- Provides description for each response type
- Part of `@ApiResponses` annotation

### **@SecurityRequirement**
**Location**: `RestService.java`
```java
@SecurityRequirement(name = "bearerAuth")
public class RestService {
    // Requires JWT authentication
}
```
**Purpose**:
- Indicates that endpoints require authentication
- Specifies the security scheme to use
- Shows authentication requirements in Swagger UI

### **@Parameter**
**Location**: `RestService.java`
```java
@Parameter(description = "User email", required = true) @RequestParam String email
```
**Purpose**:
- Documents method parameters in API documentation
- Provides description and requirements for parameters
- Enhances Swagger UI documentation

### **@Content**
**Location**: `RestService.java`
```java
@ApiResponse(responseCode = "200", description = "Login successful", 
             content = @Content(schema = @Schema(implementation = String.class)))
```
**Purpose**:
- Specifies the content type and schema of responses
- Documents response body structure
- Provides detailed response documentation

### **@Schema**
**Location**: `RestService.java`
```java
@Schema(implementation = String.class)
```
**Purpose**:
- Defines the schema for request/response bodies
- Specifies data types and structures
- Used in API documentation

---

## Validation Annotations

### **@Valid**
**Location**: Multiple controllers
```java
public ResponseEntity<Member> registerMember(@Valid @RequestBody Member member) {
    // Validate request body
}
```
**Purpose**:
- Triggers validation of annotated objects
- Validates nested objects recursively
- Ensures data integrity before processing

---

## Lombok Annotations

### **@Slf4j**
**Location**: Multiple classes
```java
@Slf4j
public class MemberController {
    // Automatic logger injection
}
```
**Purpose**:
- Automatically generates a logger field
- Provides `log` variable for logging
- Reduces boilerplate code

### **@RequiredArgsConstructor**
**Location**: Multiple classes
```java
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // Constructor automatically generated
}
```
**Purpose**:
- Generates constructor for final fields
- Enables dependency injection
- Reduces constructor boilerplate

### **@Data**
**Location**: `MemberDocument.java`
```java
@Data
public class MemberDocument {
    // Getters, setters, toString, equals, hashCode
}
```
**Purpose**:
- Generates getters, setters, toString, equals, and hashCode
- Reduces boilerplate code for POJOs
- Provides complete data class functionality

### **@AllArgsConstructor**
**Location**: `MemberDocument.java`
```java
@AllArgsConstructor
public class MemberDocument {
    // Constructor with all parameters
}
```
**Purpose**:
- Generates constructor with all fields as parameters
- Useful for creating objects with all values
- Reduces constructor boilerplate

### **@NoArgsConstructor**
**Location**: `MemberDocument.java`
```java
@NoArgsConstructor
public class MemberDocument {
    // Default constructor
}
```
**Purpose**:
- Generates no-argument constructor
- Required for frameworks like Spring Data
- Enables object creation without parameters

---

## Testing Annotations

### **@SpringBootTest**
**Location**: `KitchensinkApplicationTests.java`
```java
@SpringBootTest
@ActiveProfiles("test")
public class KitchensinkApplicationTests {
    // Integration tests
}
```
**Purpose**:
- Creates full application context for testing
- Loads all beans and configurations
- Used for integration testing

### **@ActiveProfiles**
**Location**: Test classes
```java
@ActiveProfiles("test")
```
**Purpose**:
- Activates specific Spring profiles for testing
- Uses test-specific configuration
- Separates test and production configurations

### **@ExtendWith**
**Location**: Test classes
```java
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    // Unit tests with Mockito
}
```
**Purpose**:
- Integrates testing frameworks with JUnit 5
- Enables Mockito support for mocking
- Provides testing utilities

### **@Mock**
**Location**: Test classes
```java
@Mock
private MemberRepository memberRepository;
```
**Purpose**:
- Creates mock objects for testing
- Simulates dependencies
- Enables isolated unit testing

### **@InjectMocks**
**Location**: Test classes
```java
@InjectMocks
private MemberService memberService;
```
**Purpose**:
- Injects mocks into the class under test
- Automatically wires mocked dependencies
- Simplifies test setup

### **@BeforeEach**
**Location**: Test classes
```java
@BeforeEach
void setUp() {
    // Test setup code
}
```
**Purpose**:
- Runs before each test method
- Sets up test data and mocks
- Ensures clean test state

### **@Test**
**Location**: Test classes
```java
@Test
void testRegisterMember() {
    // Test implementation
}
```
**Purpose**:
- Marks a method as a test case
- Identifies methods to be executed by test runner
- Defines individual test scenarios

### **@MockBean**
**Location**: Integration tests
```java
@MockBean
private MemberRepository memberRepository;
```
**Purpose**:
- Creates mock beans in Spring context
- Replaces real beans with mocks in integration tests
- Enables testing with mocked dependencies

### **@Autowired**
**Location**: Test classes
```java
@Autowired
private TestRestTemplate restTemplate;
```
**Purpose**:
- Injects Spring beans into test classes
- Enables dependency injection in tests
- Provides access to application components

---

## Configuration Annotations

### **@Configuration**
**Location**: `SwaggerConfig.java`
```java
@Configuration
public class SwaggerConfig {
    // Configuration beans
}
```
**Purpose**:
- Marks a class as a source of bean definitions
- Indicates that the class contains `@Bean` methods
- Used for application configuration

### **@Import**
**Location**: Test classes
```java
@Import({TestSecurityConfig.class, TestApplicationConfig.class})
```
**Purpose**:
- Imports additional configuration classes
- Combines multiple configuration sources
- Used in testing to include test-specific configs

---

## Service Layer Annotations

### **@Service**
**Location**: `MemberService.java`
```java
@Service
public class MemberService {
    // Business logic
}
```
**Purpose**:
- Marks a class as a service component
- Indicates business logic layer
- Enables dependency injection and component scanning

---

## Component Annotations

### **@Component**
**Location**: `JwtAuthenticationFilter.java`
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Filter implementation
}
```
**Purpose**:
- Marks a class as a Spring component
- Enables automatic detection and registration
- Used for custom components and filters

---

## Summary

### **Most Commonly Used Annotations**

1. **@SpringBootApplication** - Main application entry point
2. **@Controller/@RestController** - Web request handling
3. **@Service** - Business logic layer
4. **@Repository** - Data access layer
5. **@Bean** - Configuration and dependency injection
6. **@GetMapping/@PostMapping** - HTTP request mapping
7. **@Valid** - Data validation
8. **@Slf4j** - Logging support
9. **@Data** - Lombok data class generation
10. **@Test** - Test method identification

### **Security Annotations**

1. **@EnableWebSecurity** - Web security configuration
2. **@EnableMethodSecurity** - Method-level security
3. **@PreAuthorize** - Method access control (used implicitly)

### **Data Annotations**

1. **@Document** - MongoDB document mapping
2. **@Id** - Primary key identification
3. **@Indexed** - Database indexing
4. **@Repository** - Data access layer

### **API Documentation Annotations**

1. **@Tag** - API grouping
2. **@Operation** - Endpoint documentation
3. **@ApiResponses** - Response documentation
4. **@SecurityRequirement** - Authentication requirements

### **Testing Annotations**

1. **@SpringBootTest** - Integration testing
2. **@Mock/@InjectMocks** - Unit testing with mocks
3. **@Test** - Test method identification
4. **@BeforeEach** - Test setup

This comprehensive guide covers all Spring Boot annotations used in the KitchenSink project, providing clear explanations of their purpose and usage context. 