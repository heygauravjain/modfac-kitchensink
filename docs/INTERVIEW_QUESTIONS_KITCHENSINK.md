# KitchenSink Project - 50 Interview Questions & Answers

## Table of Contents

1. [Spring Boot Fundamentals](#spring-boot-fundamentals)
2. [Security & Authentication](#security--authentication)
3. [Database & Data Access](#database--data-access)
4. [API Design & REST](#api-design--rest)
5. [Testing & Quality Assurance](#testing--quality-assurance)
6. [Architecture & Design Patterns](#architecture--design-patterns)
7. [Docker & Deployment](#docker--deployment)
8. [Frontend & UI](#frontend--ui)
9. [Performance & Optimization](#performance--optimization)
10. [Advanced Concepts](#advanced-concepts)

---

## Spring Boot Fundamentals

### Q1: What is the main purpose of `@SpringBootApplication` annotation?
**A:** The `@SpringBootApplication` annotation is a convenience annotation that combines three annotations:
- `@Configuration`: Marks the class as a source of bean definitions
- `@EnableAutoConfiguration`: Tells Spring Boot to start adding beans based on classpath settings
- `@ComponentScan`: Tells Spring to look for other components, configurations, and services in the package

In our KitchenSink project, it's used in `KitchenSinkApplication.java` as the entry point for the Spring Boot application.

### Q2: Explain the difference between `@Controller` and `@RestController` in Spring Boot.
**A:** 
- `@Controller`: Used for traditional web applications that return view names. In our project, `MemberController` and `JwtAuthController` use this for Thymeleaf template rendering.
- `@RestController`: Combines `@Controller` and `@ResponseBody`, automatically serializes return values to JSON/XML. Used in `RestService.java` for REST API endpoints.

### Q3: What is the purpose of `@Service` annotation and where is it used in the project?
**A:** `@Service` marks a class as a service layer component, indicating it contains business logic. In our project, `MemberService` and `AuthService` are annotated with `@Service`, handling member management and authentication business logic respectively.

### Q4: Explain the role of `@Repository` annotation in Spring Data.
**A:** `@Repository` marks an interface as a data access layer component. In our project, `MemberRepository` extends `MongoRepository` and is annotated with `@Repository`, providing CRUD operations for MongoDB documents.

### Q5: What is dependency injection and how is it implemented in the KitchenSink project?
**A:** Dependency injection is a design pattern where dependencies are provided to a class rather than the class creating them. In our project:
- Constructor injection: `@RequiredArgsConstructor` with `final` fields
- Field injection: `@Autowired` annotation
- Setter injection: Through `@Bean` methods in configuration classes

---

## Security & Authentication

### Q6: Explain JWT authentication flow implemented in the KitchenSink project.
**A:** The JWT authentication flow includes:
1. User submits credentials via `/jwt-login`
2. `AuthService` validates credentials against MongoDB
3. `JwtTokenService` generates access and refresh tokens
4. Tokens are stored in localStorage on client-side
5. `JwtAuthenticationFilter` validates tokens on subsequent requests
6. `SecurityConfig` configures protected endpoints

### Q7: What is the purpose of `@EnableWebSecurity` and `@EnableMethodSecurity`?
**A:** 
- `@EnableWebSecurity`: Enables Spring Security web security support, allowing customization of security settings
- `@EnableMethodSecurity`: Enables method-level security using annotations like `@PreAuthorize`, allowing fine-grained access control

### Q8: How is password encryption implemented in the project?
**A:** Password encryption is implemented using `BCryptPasswordEncoder`:
- Configured as a `@Bean` in `SecurityConfig`
- Used in `AuthService` for password hashing during registration
- Provides secure one-way hashing with salt

### Q9: Explain the role of `JwtAuthenticationFilter` in the security chain.
**A:** `JwtAuthenticationFilter` extends `OncePerRequestFilter` and:
- Intercepts all HTTP requests
- Extracts JWT tokens from Authorization header
- Validates tokens using `JwtTokenService`
- Sets authentication context for valid tokens
- Allows requests to proceed or returns 401 for invalid tokens

### Q10: How is role-based access control implemented in the project?
**A:** Role-based access control is implemented through:
- User roles stored in MongoDB (`ROLE_ADMIN`, `ROLE_USER`)
- `SecurityConfig` defines URL-based access rules
- `@PreAuthorize` annotations for method-level security
- `CustomUserDetailsService` loads user details with roles
- Frontend conditional rendering based on user roles

---

## Database & Data Access

### Q11: Explain the MongoDB document structure used in the project.
**A:** The `MemberDocument` class represents the MongoDB document structure:
```java
@Document(collection = "members")
public class MemberDocument {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String email;
    private String phoneNumber;
    private String password;
    private String role;
}
```

### Q12: What is the purpose of `@Indexed(unique = true)` on the email field?
**A:** `@Indexed(unique = true)` creates a unique index on the email field, ensuring:
- No duplicate email addresses in the database
- Faster queries when searching by email
- Database-level constraint enforcement

### Q13: How does Spring Data MongoDB simplify data access in the project?
**A:** Spring Data MongoDB provides:
- `MongoRepository` interface with built-in CRUD operations
- Query method derivation (e.g., `findByEmail`)
- Automatic implementation generation
- Integration with Spring's transaction management

### Q14: Explain the difference between `Member` and `MemberDocument` classes.
**A:** 
- `MemberDocument`: MongoDB entity with `@Document` annotation, represents database structure
- `Member`: DTO (Data Transfer Object) used for API responses and business logic
- `MemberMapper` (MapStruct) handles conversion between the two

### Q15: How is data initialization handled in the project?
**A:** Data initialization is handled by `DataInitializer` class:
- Uses `@PostConstruct` to run after application startup
- Checks if admin user exists, creates if not
- Ensures application has required initial data
- Runs only once during application startup

---

## API Design & REST

### Q16: Explain the REST API design patterns used in `RestService`.
**A:** The REST API follows standard patterns:
- `GET /admin/members` - List all members
- `GET /admin/members/{id}` - Get member by ID
- `POST /admin/members` - Create new member
- `PUT /admin/members/{id}` - Update member
- `DELETE /admin/members/{id}` - Delete member
- Proper HTTP status codes and response structures

### Q17: How is API documentation implemented using Swagger/OpenAPI?
**A:** API documentation is implemented using:
- `@Tag` for grouping endpoints
- `@Operation` for endpoint descriptions
- `@ApiResponses` for response documentation
- `@SecurityRequirement` for authentication requirements
- `SwaggerConfig` class configures OpenAPI specification

### Q18: What is the purpose of `@Valid` annotation in REST endpoints?
**A:** `@Valid` triggers validation of request bodies:
- Validates `@NotNull`, `@Email`, `@Size` annotations on DTOs
- Returns 400 Bad Request for invalid data
- Ensures data integrity before processing
- Works with Spring's validation framework

### Q19: How is error handling implemented in the REST API?
**A:** Error handling is implemented through:
- `GlobalExceptionHandler` with `@ControllerAdvice`
- Custom exceptions like `ResourceNotFoundException`
- Proper HTTP status codes (404, 403, 500)
- Consistent error response format
- Logging of errors for debugging

### Q20: Explain the security implementation that prevents users from editing their own accounts.
**A:** The security implementation:
- Checks authenticated user's email against target member's email
- Returns 403 Forbidden if user tries to edit/delete their own account
- Implemented in `RestService.updateMember()` and `deleteMemberById()`
- Frontend hides edit/delete buttons for current user

---

## Testing & Quality Assurance

### Q21: Explain the testing strategy used in the KitchenSink project.
**A:** The testing strategy includes:
- Unit tests with JUnit 5 and Mockito
- Integration tests with `@SpringBootTest`
- Service layer testing with mocked repositories
- Controller testing with `@WebMvcTest`
- Security testing with custom test configurations

### Q22: What is the purpose of `@Mock` and `@InjectMocks` in unit testing?
**A:** 
- `@Mock`: Creates mock objects for dependencies (e.g., `MemberRepository`)
- `@InjectMocks`: Injects mocks into the class under test (e.g., `MemberService`)
- Enables isolated testing without real database connections
- Allows control over mock behavior using `when()` and `verify()`

### Q23: How are integration tests configured in the project?
**A:** Integration tests are configured using:
- `@SpringBootTest` for full application context
- `@ActiveProfiles("test")` for test-specific configuration
- `@Import` for test-specific configurations
- `TestRestTemplate` for HTTP endpoint testing
- Test-specific application properties

### Q24: Explain the purpose of `@BeforeEach` in test classes.
**A:** `@BeforeEach` runs before each test method to:
- Set up test data and mock configurations
- Reset mock states between tests
- Ensure clean test environment
- Initialize common test objects

### Q25: How is test coverage measured and what areas are covered?
**A:** Test coverage includes:
- Service layer business logic
- Controller request handling
- Security configurations
- Repository data access
- Exception handling
- JWT token operations
- Strategy pattern implementations

---

## Architecture & Design Patterns

### Q26: Explain the Strategy Pattern implementation in the project.
**A:** The Strategy Pattern is implemented for user registration:
- `RegistrationStrategy` interface defines registration behavior
- `UserRegistrationStrategy` and `AdminRegistrationStrategy` provide different implementations
- `RegistrationContext` manages strategy selection
- Allows dynamic behavior based on registration source

### Q27: What is the purpose of the layered architecture in the project?
**A:** The layered architecture provides:
- **Controller Layer**: Handles HTTP requests and responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Manages data access
- **Entity Layer**: Represents data models
- Clear separation of concerns and maintainability

### Q28: How is dependency injection used to achieve loose coupling?
**A:** Dependency injection achieves loose coupling through:
- Interface-based design (e.g., `RegistrationStrategy`)
- Constructor injection with `@RequiredArgsConstructor`
- Configuration-based bean creation with `@Bean`
- Enables easy testing and component replacement

### Q29: Explain the MVC pattern implementation in the project.
**A:** MVC pattern is implemented as:
- **Model**: `Member`, `MemberDocument` entities
- **View**: Thymeleaf templates (`index.html`, `user-profile.html`)
- **Controller**: `MemberController`, `JwtAuthController`
- Clear separation between data, presentation, and logic

### Q30: How is the Single Responsibility Principle applied in the project?
**A:** SRP is applied through:
- Each service class has one responsibility (e.g., `AuthService` for authentication)
- Controllers handle only HTTP concerns
- Repositories manage only data access
- Entities represent only data structure
- Clear method names indicating single purpose

---

## Docker & Deployment

### Q31: Explain the Docker Compose setup in the project.
**A:** The Docker Compose setup includes:
- Spring Boot application container
- MongoDB database container
- MongoDB Express admin interface
- Network configuration for container communication
- Volume mounting for data persistence
- Environment variable configuration

### Q32: What is the purpose of the Dockerfile in the project?
**A:** The Dockerfile:
- Uses multi-stage build for optimization
- Copies Maven dependencies for caching
- Builds the application with Maven
- Creates a minimal runtime image
- Exposes port 8080 for the application

### Q33: How is MongoDB Express integrated into the Docker setup?
**A:** MongoDB Express is integrated through:
- Separate service in `docker-compose.yml`
- Connection to MongoDB container via network
- Environment variables for configuration
- Accessible on port 8081 for database administration
- No authentication required for development

### Q34: Explain the health check implementation in the project.
**A:** Health check is implemented through:
- Spring Boot Actuator dependency
- `/actuator/health` endpoint
- Database connectivity checks
- Application status monitoring
- Docker health check configuration

### Q35: How are environment-specific configurations managed?
**A:** Environment configurations are managed through:
- `application.yml` for default configuration
- `application-docker.yml` for Docker environment
- `application-test.yml` for testing environment
- Profile-based configuration with `@ActiveProfiles`
- Environment variables for sensitive data

---

## Frontend & UI

### Q36: Explain the Thymeleaf template structure used in the project.
**A:** Thymeleaf templates include:
- `index.html`: Admin dashboard with member management
- `user-profile.html`: User profile display
- `jwt-login.html` and `jwt-signup.html`: Authentication forms
- `header.html`: Common header component
- Error pages (`401.html`, `403.html`)
- Conditional rendering based on user roles

### Q37: How is client-side JavaScript used in the project?
**A:** Client-side JavaScript provides:
- Dynamic form handling and validation
- AJAX calls to REST endpoints
- JWT token management in localStorage
- UI state management (show/hide elements)
- Error handling and user feedback
- Real-time UI updates

### Q38: Explain the JWT token handling on the frontend.
**A:** JWT token handling includes:
- Token storage in localStorage
- Automatic token inclusion in API requests
- Token expiration detection
- Automatic logout on token expiry
- Token refresh mechanism
- Secure token transmission

### Q39: How is responsive design implemented in the project?
**A:** Responsive design is implemented through:
- CSS media queries for different screen sizes
- Bootstrap-like grid system
- Flexible layouts using CSS Flexbox
- Mobile-first design approach
- Consistent styling across devices

### Q40: Explain the session management implementation.
**A:** Session management includes:
- `HttpSession` for server-side session data
- Session attributes for user information
- Session timeout configuration
- Session cleanup on logout
- Cross-page session persistence
- Security session handling

---

## Performance & Optimization

### Q41: How is database performance optimized in the project?
**A:** Database performance optimization includes:
- Unique indexes on email field
- Efficient query methods in repository
- Connection pooling configuration
- Query optimization through Spring Data
- Proper indexing strategy

### Q42: Explain the caching strategy used in the project.
**A:** Caching strategy includes:
- JWT token caching for validation
- User details caching in session
- Repository-level caching for frequently accessed data
- HTTP response caching for static resources
- Memory-efficient caching implementation

### Q43: How is memory management handled in the application?
**A:** Memory management includes:
- Efficient object creation and disposal
- Proper use of Lombok annotations to reduce boilerplate
- Connection pooling for database connections
- Session cleanup and timeout
- Garbage collection optimization

### Q44: Explain the security performance considerations.
**A:** Security performance considerations:
- Efficient JWT validation algorithms
- Password hashing with BCrypt (adaptive)
- Rate limiting for authentication endpoints
- Session timeout optimization
- Secure token storage and transmission

### Q45: How is application startup time optimized?
**A:** Startup optimization includes:
- Lazy loading of non-critical components
- Efficient dependency injection
- Minimal configuration loading
- Optimized bean creation
- Profile-based configuration loading

---

## Advanced Concepts

### Q46: Explain the CORS configuration in the project.
**A:** CORS configuration is implemented in `SecurityConfig`:
- Allows cross-origin requests for development
- Configures allowed origins, methods, and headers
- Supports credentials for authenticated requests
- Handles preflight requests automatically
- Secure configuration for production deployment

### Q47: How is exception handling implemented across the application?
**A:** Exception handling includes:
- `GlobalExceptionHandler` with `@ControllerAdvice`
- Custom exception classes (`ResourceNotFoundException`)
- Proper HTTP status code mapping
- Consistent error response format
- Logging for debugging and monitoring

### Q48: Explain the logging strategy used in the project.
**A:** Logging strategy includes:
- `@Slf4j` for automatic logger injection
- Different log levels (DEBUG, INFO, ERROR)
- Structured logging for better analysis
- Security event logging
- Performance monitoring through logs

### Q49: How is the application configured for different environments?
**A:** Environment configuration includes:
- Profile-based configuration (`dev`, `test`, `prod`)
- Environment-specific properties files
- Docker environment variables
- Conditional bean creation
- Externalized configuration management

### Q50: Explain the overall architecture and scalability considerations.
**A:** Architecture and scalability considerations:
- **Microservices-ready**: Modular design allows service separation
- **Database scalability**: MongoDB supports horizontal scaling
- **Security scalability**: JWT tokens enable stateless authentication
- **Performance**: Caching and optimization strategies
- **Monitoring**: Health checks and logging for observability
- **Deployment**: Docker containerization for easy scaling
- **Testing**: Comprehensive test coverage for reliability

---

## Summary

These 50 questions cover all major aspects of the KitchenSink project, from basic Spring Boot concepts to advanced architectural patterns. The answers demonstrate:

- **Technical Depth**: Understanding of Spring Boot ecosystem
- **Practical Experience**: Real-world implementation knowledge
- **Best Practices**: Industry-standard approaches
- **Problem-Solving**: Ability to design and implement solutions
- **Security Awareness**: Understanding of authentication and authorization
- **Testing Knowledge**: Quality assurance and testing strategies
- **DevOps Understanding**: Docker, deployment, and monitoring

This comprehensive question set can be used to assess candidates' knowledge of Spring Boot development, security implementation, testing strategies, and overall software engineering practices. 