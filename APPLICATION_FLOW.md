# Application Flow Documentation

## Table of Contents
1. [Application Startup](#application-startup)
2. [Configuration & Initialization](#configuration--initialization)
3. [Security Configuration](#security-configuration)
4. [Data Layer](#data-layer)
5. [Business Logic Layer](#business-logic-layer)
6. [Controller Layer](#controller-layer)
7. [Authentication Flow](#authentication-flow)
8. [API Endpoints Flow](#api-endpoints-flow)
9. [Web Interface Flow](#web-interface-flow)
10. [Error Handling](#error-handling)

---

## Application Startup

### 1. Main Application Class
**File:** `src/main/java/com/example/kitchensink/KitchenSinkApplication.java`

**Flow:**
1. Spring Boot application starts with `@SpringBootApplication`
2. Component scanning begins for packages under `com.example.kitchensink`
3. Auto-configuration detects Spring Security, MongoDB, Web, etc.
4. Application context is created and beans are initialized

### 2. Configuration Loading
**Files:** 
- `src/main/resources/application.yml` (default profile)
- `src/main/resources/application-docker.yml` (docker profile)

**Flow:**
1. Spring Boot loads configuration based on active profile
2. MongoDB connection is established
3. JWT configuration is loaded
4. Logging levels are set
5. Actuator endpoints are configured

---

## Configuration & Initialization

### 3. Data Initialization
**File:** `src/main/java/com/example/kitchensink/config/DataInitializer.java`

**Flow:**
1. `CommandLineRunner` executes after application startup
2. Checks if database is empty (`memberRepository.count() == 0`)
3. Creates default users with BCrypt encrypted passwords:
   - **Admin User:** `admin@admin.com` / `admin123` (ADMIN role)
   - **Regular User:** `user@user.com` / `user123` (USER role)
4. Saves users to MongoDB

### 4. MongoDB Initialization
**File:** `mongo-init.js`

**Flow:**
1. MongoDB container starts
2. `mongo-init.js` script executes automatically
3. Creates `kitchensinkdb` database
4. Initializes collections: `members`, `users`
5. Sets up database structure

---

## Security Configuration

### 5. Security Configuration
**File:** `src/main/java/com/example/kitchensink/security/SecurityConfig.java`

**Flow:**
1. **Security Filter Chain Setup:**
   - Disables CSRF protection
   - Configures frame options for same origin
   - Sets up authentication providers

2. **Authorization Rules:**
   - Public endpoints: `/`, `/login`, `/register`, `/swagger-ui/**`, `/v3/api-docs/**`, `/api/auth/**`
   - Admin endpoints: `/admin/**` (requires ADMIN role)
   - User endpoints: `/user-profile` (requires USER role)
   - All other endpoints require authentication

3. **Authentication Configuration:**
   - Form login with custom success handler
   - JWT filter integration
   - Custom authentication entry point
   - Custom access denied handler

### 6. JWT Token Utility
**File:** `src/main/java/com/example/kitchensink/security/JwtTokenUtil.java`

**Functions:**
- `generateToken(String username)` - Creates JWT token
- `extractUsername(String token)` - Extracts username from token
- `validateToken(String token, UserDetails userDetails)` - Validates token
- `isTokenExpired(String token)` - Checks token expiration

### 7. JWT Request Filter
**File:** `src/main/java/com/example/kitchensink/security/JwtRequestFilter.java`

**Flow:**
1. Intercepts all HTTP requests
2. Extracts JWT token from Authorization header
3. Validates token using `JwtTokenUtil`
4. Sets authentication context if token is valid
5. Continues request processing

### 8. Custom User Details Service
**File:** `src/main/java/com/example/kitchensink/security/CustomUserDetailsService.java`

**Flow:**
1. Implements `UserDetailsService`
2. `loadUserByUsername(String email)` method:
   - Searches for user by email in MongoDB
   - Converts role to Spring Security authorities
   - Returns `UserDetails` object

### 9. Authentication Entry Point
**File:** `src/main/java/com/example/kitchensink/security/CustomAuthenticationEntryPoint.java`

**Flow:**
1. Handles unauthorized access attempts
2. Detects API vs Web requests
3. Returns JSON for API requests, HTML redirect for web requests
4. Provides proper error responses with status codes

### 10. Access Denied Handler
**File:** `src/main/java/com/example/kitchensink/security/CustomAccessDeniedHandler.java`

**Flow:**
1. Handles forbidden access attempts
2. Similar to authentication entry point
3. Returns JSON for API requests, HTML redirect for web requests

---

## Data Layer

### 11. Entity Model
**File:** `src/main/java/com/example/kitchensink/entity/MemberDocument.java`

**Structure:**
- MongoDB document entity
- Fields: `id`, `name`, `email`, `phoneNumber`, `password`, `role`
- Uses Spring Data MongoDB annotations

### 12. Repository Layer
**File:** `src/main/java/com/example/kitchensink/repository/MemberRepository.java`

**Functions:**
- Extends `MongoRepository<MemberDocument, String>`
- `findByEmail(String email)` - Custom query method
- Inherits CRUD operations from Spring Data MongoDB

### 13. Model Layer
**File:** `src/main/java/com/example/kitchensink/model/Member.java`

**Structure:**
- DTO for API responses
- Validation annotations for input validation
- Used for data transfer between layers

### 14. Mapper
**File:** `src/main/java/com/example/kitchensink/mapper/MemberMapper.java`

**Functions:**
- Maps between `MemberDocument` and `Member` objects
- Uses MapStruct for automatic mapping generation
- Handles entity-to-DTO conversion

---

## Business Logic Layer

### 15. Member Service
**File:** `src/main/java/com/example/kitchensink/service/MemberService.java`

**Functions:**
- `getAllMembers()` - Retrieves all members
- `findById(String id)` - Finds member by ID
- `findByEmail(String email)` - Finds member by email
- `registerMember(Member member)` - Registers new member
- `updateMember(Member existing, Member updated)` - Updates member
- `deleteById(String id)` - Deletes member

**Flow:**
1. Receives requests from controllers
2. Performs business logic validation
3. Interacts with repository layer
4. Returns processed data to controllers

---

## Controller Layer

### 16. REST API Controller
**File:** `src/main/java/com/example/kitchensink/controller/RestService.java`

**Endpoints:**
- `GET /admin/members` - List all members
- `GET /admin/members/{id}` - Get member by ID
- `POST /admin/members` - Create new member
- `PUT /admin/members/{id}` - Update member
- `DELETE /admin/members/{id}` - Delete member

**Flow:**
1. Receives HTTP requests
2. Validates input using `@Valid`
3. Calls service layer methods
4. Returns JSON responses with proper status codes
5. Uses Swagger annotations for API documentation

### 17. Authentication Controller
**File:** `src/main/java/com/example/kitchensink/controller/AuthController.java`

**Endpoints:**
- `POST /api/auth/login` - Authenticate user and get JWT token
- `POST /api/auth/validate` - Validate JWT token

**Flow:**
1. Receives login credentials
2. Authenticates using `AuthenticationProvider`
3. Generates JWT token using `JwtTokenUtil`
4. Returns token and user information
5. Handles authentication errors

### 18. Web Controller
**File:** `src/main/java/com/example/kitchensink/controller/MemberController.java`

**Endpoints:**
- `GET /` or `/login` - Login page
- `GET /admin/home` - Admin dashboard
- `GET /user-profile` - User profile page
- `POST /register` - User registration
- `GET /401` - 401 error page
- `GET /403` - 403 error page

**Flow:**
1. Handles web interface requests
2. Uses Thymeleaf templates
3. Manages form submissions
4. Redirects based on user roles

---

## Strategy Pattern Implementation

### 19. Registration Strategy
**Files:**
- `src/main/java/com/example/kitchensink/controller/strategy/RegistrationStrategy.java`
- `src/main/java/com/example/kitchensink/controller/strategy/AdminRegistrationStrategy.java`
- `src/main/java/com/example/kitchensink/controller/strategy/UserRegistrationStrategy.java`
- `src/main/java/com/example/kitchensink/controller/strategy/RegistrationContext.java`

**Flow:**
1. `RegistrationContext` determines strategy based on source
2. `AdminRegistrationStrategy` handles admin registrations
3. `UserRegistrationStrategy` handles user registrations
4. Each strategy implements different registration logic

---

## Exception Handling

### 20. Global Exception Handler
**File:** `src/main/java/com/example/kitchensink/exception/GlobalExceptionHandler.java`

**Functions:**
- `handleResourceNotFoundException()` - Handles 404 errors
- `handleValidationExceptions()` - Handles validation errors
- `handleAllExceptions()` - Handles general exceptions

**Flow:**
1. Catches exceptions from controllers
2. Creates standardized error responses
3. Returns JSON with error details
4. Logs error information

### 21. Custom Exceptions
**File:** `src/main/java/com/example/kitchensink/exception/ResourceNotFoundException.java`

**Flow:**
1. Thrown when resources are not found
2. Automatically mapped to HTTP 404 status
3. Handled by global exception handler

---

## Authentication Flow

### Complete Authentication Process:

1. **User Login Request:**
   ```
   POST /api/auth/login
   {
     "email": "admin@admin.com",
     "password": "admin123"
   }
   ```

2. **Authentication Processing:**
   - `AuthController` receives request
   - `AuthenticationProvider` validates credentials
   - `CustomUserDetailsService` loads user from database
   - BCrypt password encoder verifies password

3. **JWT Token Generation:**
   - `JwtTokenUtil.generateToken()` creates JWT
   - Token contains user information and expiration
   - Response includes token and user details

4. **API Access with Token:**
   ```
   GET /admin/members
   Authorization: Bearer <jwt-token>
   ```

5. **Token Validation:**
   - `JwtRequestFilter` intercepts request
   - Extracts token from Authorization header
   - `JwtTokenUtil.validateToken()` verifies token
   - Sets authentication context if valid

---

## API Endpoints Flow

### REST API Flow:

1. **Request Reception:**
   - HTTP request arrives at controller
   - Spring Security validates authentication
   - JWT token is processed by filter

2. **Business Logic:**
   - Controller calls service methods
   - Service performs business logic
   - Repository interacts with database

3. **Response Generation:**
   - Data is mapped to DTOs
   - JSON response is created
   - Proper HTTP status codes are set

### Web Interface Flow:

1. **Page Request:**
   - User accesses web page
   - Spring Security checks authentication
   - Controller loads data for template

2. **Template Rendering:**
   - Thymeleaf processes templates
   - Data is injected into HTML
   - Form actions are configured

3. **Form Submission:**
   - User submits form
   - Controller processes form data
   - Strategy pattern determines registration type
   - User is redirected based on role

---

## Docker Configuration

### 22. Docker Setup
**Files:**
- `Dockerfile` - Multi-stage build for application
- `docker-compose.yml` - Orchestrates all services
- `.dockerignore` - Excludes unnecessary files

**Services:**
1. **MongoDB:** Database service
2. **MongoDB Express:** Database management UI
3. **Application:** Spring Boot application

**Flow:**
1. Docker Compose starts all services
2. MongoDB initializes with `mongo-init.js`
3. Application connects to MongoDB
4. Health checks ensure service readiness

---

## Testing

### 23. Test Files
**Files:**
- `src/test/java/com/example/kitchensink/controller/RestServiceTest.java`
- `src/test/java/com/example/kitchensink/controller/MemberControllerTest.java`
- `src/test/java/com/example/kitchensink/service/MemberServiceTest.java`
- `src/test/java/com/example/kitchensink/exception/GlobalExceptionHandlerTest.java`
- `src/test/java/com/example/kitchensink/security/CustomUserDetailsServiceTest.java`
- `src/test/java/com/example/kitchensink/controller/strategy/AdminRegistrationStrategyTest.java`
- `src/test/java/com/example/kitchensink/controller/strategy/UserRegistrationStrategyTest.java`
- `src/test/java/com/example/kitchensink/controller/strategy/RegistrationContextTest.java`

**Flow:**
1. Unit tests for individual components
2. Integration tests for API endpoints
3. Security tests for authentication
4. Strategy pattern tests for registration logic

---

## Static Resources

### 24. Web Resources
**Files:**
- `src/main/resources/static/css/screen.css` - Styling
- `src/main/resources/static/js/script.js` - JavaScript
- `src/main/resources/static/gfx/` - Images and graphics

### 25. Templates
**Files:**
- `src/main/resources/templates/index.html` - Main dashboard
- `src/main/resources/templates/login.html` - Login page
- `src/main/resources/templates/user-profile.html` - User profile
- `src/main/resources/templates/header.html` - Common header
- `src/main/resources/templates/error/401.html` - 401 error page
- `src/main/resources/templates/error/403.html` - 403 error page

---

## Configuration Files

### 26. Build Configuration
**File:** `pom.xml`

**Dependencies:**
- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Data MongoDB
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Actuator
- JWT libraries
- Swagger/OpenAPI
- Lombok
- MapStruct

### 27. Maven Wrapper
**Files:**
- `mvnw` - Unix Maven wrapper
- `mvnw.cmd` - Windows Maven wrapper
- `.mvn/` - Maven wrapper configuration

---

## Application Lifecycle

### Startup Sequence:
1. **Docker Compose** starts containers
2. **MongoDB** initializes with data
3. **Spring Boot** application starts
4. **Component scanning** discovers beans
5. **Security configuration** is applied
6. **Data initializer** creates default users
7. **Health checks** verify service readiness
8. **Application** is ready to serve requests

### Request Processing:
1. **HTTP request** arrives
2. **JWT filter** processes authentication
3. **Security** validates authorization
4. **Controller** receives request
5. **Service** performs business logic
6. **Repository** accesses database
7. **Response** is generated and returned

### Error Handling:
1. **Exception** occurs in any layer
2. **Global exception handler** catches it
3. **Standardized error response** is created
4. **JSON error** is returned to client
5. **Error is logged** for debugging

---

## Key Features Summary

### Security Features:
- JWT-based authentication
- Role-based authorization (ADMIN/USER)
- BCrypt password encryption
- Custom authentication handlers
- API vs Web request differentiation

### API Features:
- RESTful endpoints with proper HTTP methods
- JSON request/response format
- Swagger/OpenAPI documentation
- Input validation
- Error handling with proper status codes

### Web Features:
- Thymeleaf templating
- Form-based authentication
- Role-based page access
- Responsive design with CSS/JS

### Database Features:
- MongoDB integration
- Automatic data initialization
- Repository pattern
- Entity mapping

### DevOps Features:
- Docker containerization
- Multi-stage builds
- Health checks
- Environment-specific configurations
- Comprehensive testing suite

---

This document provides a complete overview of the application's architecture, flow, and functionality. Each component works together to create a secure, scalable, and maintainable Spring Boot application with MongoDB integration and JWT authentication. 