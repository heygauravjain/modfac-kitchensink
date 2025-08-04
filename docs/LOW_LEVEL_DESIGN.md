# KitchenSink - Low-Level Design (LLD) Document

## Table of Contents
1. [Project Overview](#project-overview)
2. [System Architecture](#system-architecture)
3. [Technology Stack](#technology-stack)
4. [Database Design](#database-design)
5. [Security Architecture](#security-architecture)
6. [API Design](#api-design)
7. [Design Patterns](#design-patterns)
8. [Component Details](#component-details)
9. [Configuration Management](#configuration-management)
10. [Testing Strategy](#testing-strategy)
11. [Deployment Architecture](#deployment-architecture)
12. [Performance Considerations](#performance-considerations)
13. [Security Considerations](#security-considerations)

## Project Overview

KitchenSink is a Spring Boot-based web application that demonstrates modern Java development practices with MongoDB integration, JWT authentication, and a comprehensive admin dashboard for member management.

### Key Features
- **User Authentication & Authorization**: JWT-based authentication with role-based access control
- **Member Management**: CRUD operations for user management with admin dashboard
- **RESTful APIs**: Comprehensive API endpoints with Swagger documentation
- **Web Interface**: Thymeleaf-based responsive web interface
- **Strategy Pattern**: Dynamic registration behavior based on source context
- **Security**: Spring Security with custom filters and JWT token management

## System Architecture

### High-Level Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Browser   │    │   Mobile App    │    │   API Client    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │    Spring Boot App        │
                    │  ┌─────────────────────┐  │
                    │  │   Controllers       │  │
                    │  │   - AuthController  │  │
                    │  │   - MemberController│  │
                    │  │   - RestService     │  │
                    │  └─────────────────────┘  │
                    │  ┌─────────────────────┐  │
                    │  │   Services         │  │
                    │  │   - AuthService    │  │
                    │  │   - MemberService  │  │
                    │  └─────────────────────┘  │
                    │  ┌─────────────────────┐  │
                    │  │   Security Layer   │  │
                    │  │   - JWT Filters    │  │
                    │  │   - SecurityConfig │  │
                    │  └─────────────────────┘  │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │      MongoDB Atlas        │
                    │   (Cloud Database)        │
                    └───────────────────────────┘
```

### Component Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                      │
├─────────────────────────────────────────────────────────────────┤
│  Thymeleaf Templates  │  REST Controllers  │  Static Resources │
│  - index.html        │  - AuthController  │  - CSS/JS/Images  │
│  - jwt-login.html    │  - MemberController│                   │
│  - jwt-signup.html   │  - RestService     │                   │
└───────────────────────┼─────────────────────┼───────────────────┘
                        │
┌───────────────────────┼─────────────────────┼───────────────────┐
│                    Business Logic Layer                         │
├─────────────────────────────────────────────────────────────────┤
│  Services            │  Strategy Pattern   │  Security Services│
│  - AuthService       │  - RegistrationContext│  - JwtTokenService│
│  - MemberService     │  - UserRegistrationStrategy│  - CustomUserDetailsService│
│                     │  - AdminRegistrationStrategy│             │
└───────────────────────┼─────────────────────┼───────────────────┘
                        │
┌───────────────────────┼─────────────────────┼───────────────────┐
│                     Data Access Layer                           │
├─────────────────────────────────────────────────────────────────┤
│  Repositories        │  Mappers           │  Entities          │
│  - MemberRepository  │  - MemberMapper    │  - MemberDocument  │
│                     │                     │                    │
└───────────────────────┼─────────────────────┼───────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│                    MongoDB Database                             │
│  Collections:                                                  │
│  - members (users, roles, authentication data)                │
└─────────────────────────────────────────────────────────────────┘
```

## Technology Stack

### Backend Technologies
- **Java 21**: Latest LTS version with modern features
- **Spring Boot 3.2.5**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data MongoDB**: Database integration
- **Thymeleaf**: Server-side templating engine
- **JWT (JSON Web Tokens)**: Stateless authentication
- **Lombok**: Reduces boilerplate code
- **MapStruct**: Object mapping framework
- **Maven**: Build and dependency management

### Frontend Technologies
- **HTML5/CSS3**: Modern web standards
- **JavaScript**: Client-side interactivity
- **Thymeleaf**: Server-side templating
- **Responsive Design**: Mobile-friendly interface

### Database
- **MongoDB Atlas**: Cloud-hosted NoSQL database
- **Spring Data MongoDB**: ORM for MongoDB

### Development Tools
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework
- **Swagger/OpenAPI**: API documentation
- **Docker**: Containerization

## Database Design

### MongoDB Collections

#### Members Collection
```javascript
{
  "_id": "ObjectId",
  "name": "String (required)",
  "email": "String (unique, indexed, required)",
  "phoneNumber": "String (required)",
  "password": "String (encrypted, required)",
  "role": "String (ROLE_ADMIN/ROLE_USER, required)"
}
```

### Indexes
- **Email Index**: Unique index on email field for fast lookups
- **Auto-index Creation**: Enabled for development flexibility

### Data Flow
1. **Registration**: User data → Validation → Encryption → MongoDB
2. **Authentication**: Email lookup → Password verification → JWT generation
3. **Authorization**: JWT validation → Role extraction → Access control

## Security Architecture

### Authentication Flow
```
1. User submits credentials
   ↓
2. AuthenticationManager validates credentials
   ↓
3. CustomUserDetailsService loads user from MongoDB
   ↓
4. JwtTokenService generates access and refresh tokens
   ↓
5. Tokens returned to client
   ↓
6. Client includes token in subsequent requests
   ↓
7. JwtAuthenticationFilter validates token
   ↓
8. SecurityContext updated with user details
```

### JWT Token Structure
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "user@email.com",
    "role": "ROLE_ADMIN",
    "iat": 1234567890,
    "exp": 1234567890
  },
  "signature": "HMACSHA256(base64UrlEncode(header) + '.' + base64UrlEncode(payload), secret)"
}
```

### Security Configuration
- **Stateless Sessions**: No server-side session storage
- **CORS Configuration**: Cross-origin resource sharing enabled
- **CSRF Protection**: Disabled for API endpoints
- **Password Encryption**: BCrypt with salt
- **Token Expiration**: 15 minutes (access), 7 days (refresh)

### Security Filters
1. **JwtAuthenticationFilter**: Validates JWT tokens from Authorization header
2. **JwtCookieAuthenticationFilter**: Validates JWT tokens from cookies
3. **CustomUserDetailsService**: Loads user details from MongoDB

## API Design

### REST API Endpoints

#### Authentication Endpoints
```
POST /api/auth/login
- Description: User login
- Request: AuthRequest (email, password)
- Response: AuthResponse (tokens, user info)
- Status: 200 (success), 401 (unauthorized)

POST /api/auth/signup
- Description: User registration
- Request: SignupRequest (name, email, password, role)
- Response: AuthResponse (tokens, user info)
- Status: 201 (created), 409 (conflict)

POST /api/auth/refresh
- Description: Refresh access token
- Request: RefreshTokenRequest (refresh token)
- Response: AuthResponse (new tokens)
- Status: 200 (success), 401 (invalid token)

POST /api/auth/validate
- Description: Validate JWT token
- Request: Authorization header
- Response: Token validation result
- Status: 200 (valid), 401 (invalid)
```

#### Member Management Endpoints
```
GET /admin/members
- Description: Get all members (Admin only)
- Response: List<Member>
- Status: 200 (success), 403 (forbidden)

GET /admin/members/{id}
- Description: Get member by ID (Admin only)
- Response: Member
- Status: 200 (success), 404 (not found)

PUT /admin/members/{id}
- Description: Update member (Admin only)
- Request: Member
- Response: Updated Member
- Status: 200 (success), 404 (not found)

DELETE /admin/members/{id}
- Description: Delete member (Admin only)
- Status: 204 (success), 404 (not found)
```

### Web Interface Endpoints
```
GET /
- Description: Redirect to login page
- Response: Redirect to /jwt-login

GET /jwt-login
- Description: Login page
- Response: jwt-login.html

GET /jwt-signup
- Description: Registration page
- Response: jwt-signup.html

GET /admin/home
- Description: Admin dashboard
- Response: index.html
- Security: Requires ADMIN role

GET /user-profile
- Description: User profile page
- Response: user-profile.html
- Security: Requires authentication

POST /register
- Description: Member registration
- Request: Member form data
- Response: Redirect with success/error message
```

## Design Patterns

### 1. Strategy Pattern
**Purpose**: Flexible registration strategies for different user types

**Components**:
- `RegistrationStrategy` (Interface)
- `UserRegistrationStrategy` (Concrete Strategy)
- `AdminRegistrationStrategy` (Concrete Strategy)
- `RegistrationContext` (Context)

**Implementation**:
```java
// Strategy Interface
public interface RegistrationStrategy {
    String register(Member member, RedirectAttributes redirectAttributes);
}

// Concrete Strategies
@Component
public class UserRegistrationStrategy implements RegistrationStrategy {
    // User-specific registration logic
}

@Component
public class AdminRegistrationStrategy implements RegistrationStrategy {
    // Admin-specific registration logic
}

// Context
@Component
public class RegistrationContext {
    private RegistrationStrategy registrationStrategy;
    
    public void setStrategy(String sourcePage) {
        // Set appropriate strategy based on source
    }
    
    public String register(Member member, RedirectAttributes redirectAttributes) {
        return registrationStrategy.register(member, redirectAttributes);
    }
}
```

### 2. Repository Pattern
**Purpose**: Abstract data access layer

**Implementation**:
```java
@Repository
public interface MemberRepository extends MongoRepository<MemberDocument, String> {
    Optional<MemberDocument> findByEmail(String email);
}
```

### 3. Service Layer Pattern
**Purpose**: Business logic encapsulation

**Implementation**:
```java
@Service
public class AuthService {
    // Authentication business logic
}

@Service
public class MemberService {
    // Member management business logic
}
```

### 4. Filter Pattern
**Purpose**: Request/response processing

**Implementation**:
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // JWT token validation logic
}
```

## Component Details

### Controllers

#### AuthController
- **Purpose**: REST API endpoints for authentication
- **Responsibilities**:
  - User login/logout
  - User registration
  - Token refresh
  - Token validation
- **Dependencies**: AuthenticationManager, JwtTokenService, AuthService

#### MemberController
- **Purpose**: Web interface controllers for member management
- **Responsibilities**:
  - Admin dashboard
  - User profile
  - Member registration
  - Page routing
- **Dependencies**: MemberService, RegistrationContext, MemberMapper

#### RestService
- **Purpose**: REST API endpoints for member management
- **Responsibilities**:
  - CRUD operations for members
  - Admin-only operations
- **Dependencies**: MemberService

### Services

#### AuthService
- **Purpose**: Authentication business logic
- **Responsibilities**:
  - User registration
  - Token generation
  - Token refresh
- **Dependencies**: MemberRepository, PasswordEncoder, JwtTokenService

#### MemberService
- **Purpose**: Member management business logic
- **Responsibilities**:
  - CRUD operations
  - Data validation
  - Business rules enforcement
- **Dependencies**: MemberRepository, MemberMapper

### Security Components

#### JwtTokenService
- **Purpose**: JWT token management
- **Responsibilities**:
  - Token generation
  - Token validation
  - Token extraction
- **Dependencies**: JWT library, configuration

#### CustomUserDetailsService
- **Purpose**: User details loading for Spring Security
- **Responsibilities**:
  - Load user from database
  - Convert to UserDetails
- **Dependencies**: MemberRepository

#### Security Filters
- **JwtAuthenticationFilter**: Validates Authorization header tokens
- **JwtCookieAuthenticationFilter**: Validates cookie-based tokens

### Data Layer

#### MemberDocument
- **Purpose**: MongoDB entity
- **Fields**: id, name, email, phoneNumber, password, role
- **Indexes**: Email (unique)

#### MemberRepository
- **Purpose**: Data access interface
- **Methods**: findByEmail, CRUD operations
- **Extends**: MongoRepository

#### MemberMapper
- **Purpose**: Object mapping between DTOs and entities
- **Implementation**: MapStruct
- **Mappings**: Member ↔ MemberDocument

## Configuration Management

### Application Properties
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://username:password@cluster.mongodb.net/
      database: kitchensink
      auto-index-creation: true
  thymeleaf:
    mode: HTML
    suffix: .html

jwt:
  secret:
    key: Base64EncodedSecretKey
  access:
    token:
      expiration: 900000  # 15 minutes
  refresh:
    token:
      expiration: 604800000  # 7 days

logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

### Environment-Specific Configurations
- **Development**: application.yml
- **Docker**: application-docker.yml
- **Testing**: application-test.yml

## Testing Strategy

### Test Structure
```
src/test/java/com/example/kitchensink/
├── controller/
│   ├── AuthControllerIntegrationTest.java
│   ├── MemberControllerTest.java
│   └── RestServiceTest.java
├── service/
│   ├── AuthServiceTest.java
│   └── MemberServiceTest.java
├── security/
│   ├── JwtTokenServiceTest.java
│   ├── SecurityConfigTest.java
│   └── CustomUserDetailsServiceTest.java
├── strategy/
│   ├── RegistrationContextTest.java
│   ├── UserRegistrationStrategyTest.java
│   └── AdminRegistrationStrategyTest.java
└── KitchensinkApplicationTests.java
```

### Testing Approaches
1. **Unit Tests**: Individual component testing
2. **Integration Tests**: End-to-end API testing
3. **Security Tests**: Authentication/authorization testing
4. **Strategy Tests**: Pattern implementation testing

### Test Configuration
- **Test Database**: In-memory MongoDB
- **Test Security**: Disabled for easier testing
- **Mock Dependencies**: External services mocked

## Deployment Architecture

### Docker Configuration
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/kitchensink-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/kitchensink
    depends_on:
      - mongo
  
  mongo:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db

volumes:
  mongo-data:
```

### Deployment Options
1. **Local Development**: Spring Boot with local MongoDB
2. **Docker Development**: Docker Compose with MongoDB container
3. **Cloud Deployment**: Spring Boot with MongoDB Atlas

## Performance Considerations

### Database Optimization
- **Indexes**: Email field indexed for fast lookups
- **Connection Pooling**: MongoDB connection pooling
- **Query Optimization**: Efficient MongoDB queries

### Caching Strategy
- **JWT Tokens**: Client-side storage
- **User Details**: Minimal caching (security sensitive)
- **Static Resources**: Browser caching

### Scalability
- **Stateless Design**: No server-side session storage
- **Horizontal Scaling**: Multiple application instances
- **Database Scaling**: MongoDB Atlas cloud scaling

## Security Considerations

### Authentication Security
- **Password Hashing**: BCrypt with salt
- **JWT Security**: Signed tokens with expiration
- **Token Storage**: Secure client-side storage

### Authorization Security
- **Role-Based Access**: ADMIN/USER roles
- **Endpoint Protection**: Method-level security
- **Resource Protection**: URL-based security

### Data Security
- **Input Validation**: Server-side validation
- **SQL Injection**: MongoDB query safety
- **XSS Protection**: Thymeleaf auto-escaping

### Configuration Security
- **Secret Management**: Environment variables for secrets
- **HTTPS**: SSL/TLS encryption
- **CORS**: Controlled cross-origin access

## Monitoring and Logging

### Logging Strategy
- **Security Events**: Authentication/authorization logs
- **Application Events**: Business logic logs
- **Error Tracking**: Exception logging
- **Performance Metrics**: Request timing logs

### Health Checks
- **Database Connectivity**: MongoDB health check
- **Application Health**: Spring Boot Actuator
- **Security Status**: Authentication system health

## Future Enhancements

### Planned Features
1. **Email Verification**: User email confirmation
2. **Password Reset**: Forgot password functionality
3. **Audit Logging**: User action tracking
4. **Rate Limiting**: API rate limiting
5. **Multi-factor Authentication**: 2FA support
6. **API Versioning**: Versioned API endpoints
7. **GraphQL Support**: Alternative to REST
8. **Real-time Features**: WebSocket support

### Technical Improvements
1. **Microservices**: Service decomposition
2. **Event Sourcing**: Event-driven architecture
3. **CQRS**: Command Query Responsibility Segregation
4. **Kubernetes**: Container orchestration
5. **Service Mesh**: Istio integration
6. **Observability**: Distributed tracing

---

*This LLD document provides a comprehensive overview of the KitchenSink project architecture, design patterns, and implementation details. It serves as a reference for developers, architects, and stakeholders involved in the project.* 