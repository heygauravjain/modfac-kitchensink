# KitchenSink Project Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Architecture Details](#architecture-details)
5. [UI/UX Design System](#uiux-design-system)
6. [API Documentation](#api-documentation)
7. [Database Schema](#database-schema)
8. [Security Implementation](#security-implementation)
9. [Testing Strategy](#testing-strategy)
10. [Performance & Optimization](#performance--optimization)
11. [Deployment Guide](#deployment-guide)
12. [Troubleshooting](#troubleshooting)
13. [Contributing](#contributing)

## Project Overview

### Description
KitchenSink is a comprehensive Spring Boot application that demonstrates modern web development practices including JWT authentication, MongoDB integration, role-based access control, and RESTful API design. The application features a modern, responsive UI with dark/light theme support and glass morphism design elements.

### Key Features
- 🔐 **JWT Authentication**: Secure token-based authentication system with refresh tokens
- 👥 **Role-based Access Control**: Admin and User roles with different permissions
- 🗄️ **MongoDB Integration**: NoSQL database for flexible data storage
- 🌐 **RESTful APIs**: Complete CRUD operations with OpenAPI documentation
- 🎨 **Modern Web Interface**: Thymeleaf templates with enhanced UX/UI
- 🛡️ **Security**: Spring Security with custom filters and handlers
- 📊 **Monitoring**: Actuator endpoints for health checks and metrics
- 🎯 **Strategy Pattern**: Dynamic registration behavior based on source context
- 🌙 **Theme Support**: Dark/Light mode with smooth transitions
- 📱 **Responsive Design**: Mobile-first approach with modern CSS
- ⚡ **Performance Optimized**: Enhanced loading states and animations

### Technology Stack
- **Backend**: Spring Boot 3.2.5
- **Database**: MongoDB
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security
- **Templating**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 21
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Frontend**: Modern CSS with Glass Morphism
- **JavaScript**: Enhanced UX with real-time validation

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher
- MongoDB 4.4 or higher
- Git

### Quick Start

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd modfac-kitchensink
   ```

2. **Start MongoDB**
   ```bash
   # Using Docker
   docker run -d -p 27017:27017 --name mongodb mongo:latest
   
   # Or using MongoDB service
   sudo systemctl start mongod
   ```

3. **Build and Run**
   ```bash
   mvn clean package
   java -jar target/kitchensink-0.0.1-SNAPSHOT.jar
   ```

4. **Access the Application**
   - Web Interface: http://localhost:8080
   - Login Page: http://localhost:8080/jwt-login
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

### Default Users
| Email | Password | Role | Access |
|-------|----------|------|--------|
| admin@admin.com | admin123 | Admin | Full access |
| user@user.com | user123 | User | Limited access |
| g@g.com | password123 | Admin | Full access |

## Development Setup

### IDE Configuration

#### IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Import as Maven project
3. Configure Java 21 SDK
4. Enable annotation processing for MapStruct and Lombok

#### Eclipse
1. Import as Maven project
2. Configure Java 21 JRE
3. Install Lombok plugin
4. Configure annotation processing

#### VS Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Configure Java 21

### Environment Configuration

#### Application Properties
```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/kitchensink
  security:
    jwt:
      secret: your-secret-key-here
      expiration: 86400000 # 24 hours
      refresh-expiration: 604800000 # 7 days
```

#### Docker Setup
```yaml
# docker-compose.yml
version: '3.8'
services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
  
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mongodb
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/kitchensink

volumes:
  mongodb_data:
```

## Architecture Details

### System Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Layer     │    │  Business Layer │    │  Data Layer     │
│   (Controllers) │◄──►│   (Services)    │◄──►│   (Repository)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Thymeleaf     │    │   Security      │    │   MongoDB       │
│   Templates     │    │   (JWT)         │    │   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Package Structure
```
com.example.kitchensink/
├── config/          # Configuration classes
├── controller/      # REST and MVC controllers
├── entity/          # MongoDB entities
├── exception/       # Custom exceptions
├── mapper/          # MapStruct mappers
├── model/           # DTOs and request/response models
├── repository/      # MongoDB repositories
├── security/        # JWT and security configuration
└── service/         # Business logic services
```

### Key Design Patterns

1. **Strategy Pattern**: Dynamic registration behavior
2. **Repository Pattern**: Data access abstraction
3. **DTO Pattern**: Data transfer objects
4. **Builder Pattern**: Object construction
5. **Observer Pattern**: Event handling

## UI/UX Design System

### Design Philosophy
The application follows modern design principles with a focus on:
- **Accessibility**: WCAG 2.1 compliance
- **Responsiveness**: Mobile-first approach
- **Performance**: Optimized loading and transitions
- **User Experience**: Intuitive navigation and feedback

### Theme System
```css
:root {
    /* Light Theme */
    --primary-color: #007bff;
    --bg-primary: #ffffff;
    --text-primary: #2c3e50;
}

[data-theme="dark"] {
    /* Dark Theme */
    --bg-primary: #1a1a1a;
    --text-primary: #ffffff;
}
```

### Key UI Components

1. **Glass Morphism Containers**
   - Translucent backgrounds with backdrop blur
   - Subtle borders and shadows
   - Smooth hover effects

2. **Enhanced Form Validation**
   - Real-time validation feedback
   - Password strength indicators
   - Accessible error messages

3. **Responsive Dashboard**
   - Grid-based layout system
   - Adaptive table design
   - Mobile-optimized interactions

4. **Theme Toggle**
   - Smooth transitions between themes
   - Persistent theme preference
   - System theme detection

### CSS Architecture
- **CSS Custom Properties**: For theming and consistency
- **BEM Methodology**: For maintainable CSS
- **Mobile-First**: Responsive design approach
- **Performance**: Optimized animations and transitions

## API Documentation

### Authentication Endpoints

#### POST /jwt-login
Authenticate user and generate JWT tokens.

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000
}
```

#### POST /jwt-signup
Register a new user account.

**Request:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password123!",
  "phoneNumber": "1234567890",
  "role": "USER"
}
```

### Member Management Endpoints

#### GET /admin/members
Retrieve all members (Admin only).

#### POST /admin/members
Create a new member (Admin only).

#### PUT /admin/members/{id}
Update member information (Admin only).

#### DELETE /admin/members/{id}
Delete a member (Admin only).

### Health Check Endpoints

#### GET /actuator/health
Application health status.

#### GET /actuator/info
Application information.

## Database Schema

### Member Document
```json
{
  "_id": "ObjectId",
  "name": "String",
  "email": "String",
  "password": "String (encrypted)",
  "phoneNumber": "String",
  "role": "String (ROLE_ADMIN/ROLE_USER)",
  "createdAt": "Date",
  "updatedAt": "Date"
}
```

### Indexes
- `email`: Unique index for email addresses
- `role`: Index for role-based queries
- `createdAt`: Index for time-based queries

## Security Implementation

### JWT Authentication Flow
1. **Login**: User provides credentials
2. **Validation**: Server validates credentials
3. **Token Generation**: Server generates access and refresh tokens
4. **Token Storage**: Client stores tokens securely
5. **API Requests**: Client includes token in Authorization header
6. **Token Validation**: Server validates token on each request
7. **Token Refresh**: Client uses refresh token to get new access token

### Security Features
- **Password Encryption**: BCrypt password hashing
- **Token Expiration**: Configurable token lifetimes
- **CORS Configuration**: Cross-origin resource sharing
- **CSRF Protection**: Cross-site request forgery protection
- **XSS Prevention**: Content Security Policy headers

### Role-based Access Control
- **Admin Role**: Full access to all endpoints
- **User Role**: Limited access to specific endpoints
- **Anonymous**: Access to login and public endpoints only

## Testing Strategy

### Test Coverage
- **Unit Tests**: Individual component testing
- **Integration Tests**: API endpoint testing
- **Security Tests**: Authentication and authorization testing
- **UI Tests**: User interface testing

### Test Categories

#### Unit Tests
- Service layer business logic
- Repository data access
- Utility functions
- Security components

#### Integration Tests
- REST API endpoints
- Database operations
- Authentication flows
- Error handling

#### Security Tests
- JWT token validation
- Role-based access control
- Password encryption
- Input validation

### Running Tests
```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test category
mvn test -Dtest=*ServiceTest
```

## Performance & Optimization

### Frontend Optimizations
- **Resource Preloading**: Critical CSS and JS files
- **Lazy Loading**: Images and non-critical resources
- **Minification**: CSS and JavaScript compression
- **Caching**: Browser and server-side caching

### Backend Optimizations
- **Database Indexing**: Optimized query performance
- **Connection Pooling**: Efficient database connections
- **Caching**: Redis for session and data caching
- **Compression**: Gzip response compression

### Monitoring
- **Application Metrics**: Custom business metrics
- **Performance Monitoring**: Response time tracking
- **Error Tracking**: Exception monitoring
- **Health Checks**: System health monitoring

## Deployment Guide

### Production Deployment

#### Docker Deployment
```bash
# Build Docker image
docker build -t kitchensink .

# Run container
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod kitchensink
```

#### Kubernetes Deployment
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: kitchensink
spec:
  replicas: 3
  selector:
    matchLabels:
      app: kitchensink
  template:
    metadata:
      labels:
        app: kitchensink
    spec:
      containers:
      - name: kitchensink
        image: kitchensink:latest
        ports:
        - containerPort: 8080
```

#### Environment Variables
```bash
# Production configuration
SPRING_PROFILES_ACTIVE=prod
SPRING_DATA_MONGODB_URI=mongodb://prod-mongo:27017/kitchensink
JWT_SECRET=your-production-secret-key
JWT_EXPIRATION=86400000
```

### CI/CD Pipeline
```yaml
# GitHub Actions workflow
name: Deploy KitchenSink
on:
  push:
    branches: [main]
jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 21
      uses: actions/setup-java@v2
      with:
        java-version: '21'
    - name: Build with Maven
      run: mvn clean package
    - name: Deploy to production
      run: ./deploy.sh
```

## Troubleshooting

### Common Issues

#### MongoDB Connection Issues
```bash
# Check MongoDB status
sudo systemctl status mongod

# Check MongoDB logs
sudo journalctl -u mongod

# Test connection
mongo --host localhost --port 27017
```

#### JWT Token Issues
```bash
# Check JWT configuration
curl -X POST http://localhost:8080/jwt-login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","password":"admin123"}'
```

#### Application Startup Issues
```bash
# Check application logs
tail -f logs/application.log

# Check Java version
java -version

# Check Maven version
mvn -version
```

### Performance Issues

#### Database Performance
- Check MongoDB indexes
- Monitor slow queries
- Optimize database connections

#### Memory Issues
- Monitor JVM heap usage
- Adjust memory settings
- Check for memory leaks

#### Network Issues
- Check firewall settings
- Verify port availability
- Test network connectivity

## Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Update documentation
6. Submit a pull request

### Code Standards
- **Java**: Follow Google Java Style Guide
- **CSS**: Follow BEM methodology
- **JavaScript**: Use ES6+ features
- **Documentation**: Keep docs updated

### Testing Requirements
- All new features must have tests
- Maintain minimum 80% code coverage
- Include integration tests for APIs
- Test both positive and negative scenarios

### Documentation Updates
- Update relevant documentation files
- Include code examples
- Add troubleshooting guides
- Keep API documentation current

---

## Version History

### v1.0.0 (Current)
- ✅ JWT Authentication with refresh tokens
- ✅ Role-based access control
- ✅ MongoDB integration
- ✅ RESTful API with OpenAPI documentation
- ✅ Modern responsive UI with theme support
- ✅ Comprehensive test coverage (80%+ branch coverage)
- ✅ Performance optimizations
- ✅ Security best practices
- ✅ Docker and Kubernetes support
- ✅ CI/CD pipeline ready

### Planned Features
- 🔄 Real-time notifications
- 🔄 Advanced search and filtering
- 🔄 File upload capabilities
- 🔄 Email notifications
- 🔄 Advanced analytics dashboard
- 🔄 Multi-language support
- 🔄 Advanced security features
- 🔄 Performance monitoring dashboard

---

*Last Updated: August 2024*
*Version: 1.0.0* 