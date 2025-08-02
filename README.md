# KitchenSink Application

A comprehensive Spring Boot application demonstrating modern web development practices with JWT authentication, MongoDB integration, role-based access control, and RESTful APIs.

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+ or Docker
- MongoDB (local or Docker)

### Run with Docker (Recommended)
```bash
# Clone the repository
git clone <repository-url>
cd modfac-kitchensink

# Start all services
docker-compose up --build

# Access the application
# Web App: http://localhost:8080
# MongoDB Express: http://localhost:8081 (admin/admin123)
# Health Check: http://localhost:8080/actuator/health
```

### Run Locally
```bash
# Build the application
mvn clean package

# Run with Maven
mvn spring-boot:run

# Or run with Java
java -jar target/kitchensink-0.0.1-SNAPSHOT.jar
```

## 📋 Features

### 🔐 Authentication & Security
- **JWT-based Authentication**: Secure token-based authentication system
- **Role-based Access Control**: Admin and User roles with different permissions
- **Password Encryption**: BCrypt password hashing
- **Session Management**: Stateless JWT sessions
- **CORS Configuration**: Cross-origin resource sharing setup
- **Self-Edit Prevention**: Users cannot edit or delete their own accounts

### 🗄️ Data Management
- **MongoDB Integration**: NoSQL database with Spring Data MongoDB
- **MongoDB Express**: Web-based MongoDB admin interface (http://localhost:8081)
- **MapStruct Mapping**: Efficient entity-to-DTO mapping
- **Data Initialization**: Automatic default user creation
- **Repository Pattern**: Clean data access layer

### 🌐 Web Interface
- **Thymeleaf Templates**: Server-side rendering
- **Responsive Design**: Modern web interface
- **Admin Dashboard**: Member management interface
- **User Profile**: Personal information display

### 🔌 RESTful APIs
- **Complete CRUD Operations**: Create, Read, Update, Delete
- **OpenAPI Documentation**: Swagger UI integration
- **Proper Error Handling**: Global exception handling
- **Validation**: Input validation and sanitization
- **Strategy Pattern**: Dynamic registration behavior based on source context

### 🛠️ Development Tools
- **Health Checks**: Actuator endpoints for monitoring
- **Test Coverage**: Comprehensive unit and integration tests
- **Docker Support**: Containerized deployment
- **Maven Wrapper**: Consistent build environment

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.2.5
- **Database**: MongoDB
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security
- **Templating**: Thymeleaf
- **Build Tool**: Maven
- **Java Version**: 21
- **Documentation**: OpenAPI 3.0 (Swagger)

### Component Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web Layer     │    │  Security Layer │    │  Data Layer     │
│                 │    │                 │    │                 │
│ • Controllers   │◄──►│ • JWT Filters   │◄──►│ • MongoDB       │
│ • Thymeleaf     │    │ • Auth Manager  │    │ • Repositories  │
│ • REST APIs     │    │ • User Details  │    │ • Entities      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Service Layer  │    │  Strategy Layer │    │  Config Layer   │
│                 │    │                 │    │                 │
│ • Auth Service  │◄──►│ • Registration  │◄──►│ • Security Config│
│ • Member Service│    │ • Context       │    │ • Data Init     │
│ • Business Logic│    │ • Strategies    │    │ • Bean Config   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🔑 Default Users

| Email | Password | Role | Access |
|-------|----------|------|--------|
| admin@admin.com | admin123 | Admin | Full access to all features |
| user@user.com | user123 | User | Limited access to user features |

## 📚 Documentation

### 📖 Complete Documentation
- **[Application Flow](docs/APPLICATION_FLOW.md)**: Detailed system architecture and authentication flow
- **[Project Documentation](docs/PROJECT_DOCUMENTATION.md)**: Complete setup, development, and deployment guide
- **[Build Guide](docs/BUILD_GUIDE.md)**: Build and testing instructions
- **[Troubleshooting](docs/TROUBLESHOOTING.md)**: Common issues and solutions
- **[Test Coverage](docs/TEST_COVERAGE_SUMMARY.md)**: Testing strategy and coverage report

### 🎨 Architecture Diagram
- **[Draw.io Diagram](docs/KitchenSink_Architecture_Diagram.xml)**: Visual architecture representation

## 🛣️ API Endpoints

### Authentication
- `POST /api/auth/login` - Authenticate user and get JWT token
- `POST /api/auth/signup` - Register new user
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/validate` - Validate JWT token

### Web Interface
- `GET /` - Redirect to login page
- `GET /login` - Login page
- `POST /jwt-login` - Web-based JWT authentication
- `GET /admin/home` - Admin dashboard (Admin only)
- `GET /user-profile` - User profile page (Authenticated users)

### Admin APIs (Admin only)
- `GET /admin/members` - List all members
- `GET /admin/members/{id}` - Get member by ID
- `GET /admin/members/search` - Search by email
- `POST /admin/members` - Create new member
- `PUT /admin/members/{id}` - Update member
- `DELETE /admin/members/{id}` - Delete member

### Monitoring
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /swagger-ui.html` - API documentation

## 🗄️ Database Schema

### Members Collection
```javascript
{
  "_id": ObjectId,
  "name": String (required, max 25 chars),
  "email": String (unique, required),
  "phoneNumber": String (required, 10-12 digits),
  "password": String (BCrypt encrypted),
  "role": String ("ROLE_ADMIN" or "ROLE_USER")
}
```

## 🔧 Configuration

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string | MongoDB Atlas |
| `JWT_SECRET_KEY` | JWT signing key | Generated key |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token expiration | 900000ms (15 min) |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token expiration | 604800000ms (7 days) |

### Profiles
- **default**: Uses MongoDB Atlas
- **docker**: Uses local MongoDB container
- **test**: Test configuration

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Test Categories
- **Unit Tests**: Service layer, repository layer, security components
- **Integration Tests**: REST API endpoints, authentication flows
- **End-to-End Tests**: Complete user workflows

### Test Coverage
- **Controller Layer**: 95%+
- **Service Layer**: 90%+
- **Security Layer**: 85%+
- **Overall Coverage**: 85%+

## 🚀 Deployment

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up --build

# Run in detached mode
docker-compose up --build -d

# Stop services
docker-compose down
```

### Production Deployment
```bash
# Build JAR file
mvn clean package

# Run with production profile
java -jar target/kitchensink-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Kubernetes Deployment
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

## 🔍 Monitoring

### Health Checks
- **Application Health**: `/actuator/health`
- **MongoDB Health**: Database connection status
- **JWT Token Health**: Token validation status

### Metrics
- **HTTP Requests**: Request/response times
- **JVM Metrics**: Memory usage, garbage collection
- **Database Metrics**: Connection pool, query performance
- **Security Metrics**: Authentication success/failure rates

### Logging
- **Application Logs**: User actions, system events
- **Security Logs**: Authentication attempts, authorization failures
- **Performance Logs**: Slow queries, memory usage

## 🛠️ Development

### Project Structure
```
src/
├── main/
│   ├── java/com/example/kitchensink/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── entity/          # MongoDB entities
│   │   ├── model/           # DTOs and models
│   │   ├── repository/      # MongoDB repositories
│   │   ├── security/        # JWT and security config
│   │   └── service/         # Business logic
│   └── resources/
│       ├── static/          # Static assets
│       ├── templates/       # Thymeleaf templates
│       └── application.yml  # Application config
└── test/                    # Unit and integration tests
```

### IDE Setup
1. **IntelliJ IDEA**: Import as Maven project, configure Java 21
2. **Eclipse**: Import as Maven project, install Lombok plugin
3. **VS Code**: Install Java Extension Pack and Spring Boot Extension Pack

## 🔒 Security Features

### Authentication
- JWT-based stateless authentication
- Password encryption with BCrypt
- Token refresh mechanism
- Secure cookie handling

### Authorization
- Role-based access control (ADMIN/USER)
- Method-level security
- URL-based access control
- Custom access denied handling

### Data Protection
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CORS configuration

## 📊 Performance

### Optimizations
- **JVM Tuning**: Optimized for containerized environments
- **Database Indexing**: Indexed fields for faster queries
- **Connection Pooling**: MongoDB connection optimization
- **Caching**: User details and token caching

### Monitoring
- **Application Metrics**: Request times, error rates
- **Database Performance**: Query execution times
- **Memory Usage**: JVM heap and garbage collection
- **Security Metrics**: Authentication success rates

## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make your changes
4. Write tests for new functionality
5. Run all tests: `mvn test`
6. Commit your changes: `git commit -m "Add feature description"`
7. Push to branch: `git push origin feature/your-feature`
8. Create a Pull Request

### Code Standards
- Follow Google Java Style Guide
- Write comprehensive unit tests
- Update documentation for new features
- Ensure test coverage remains above 85%

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Common Issues
- **MongoDB Connection**: Check if MongoDB is running and accessible
- **JWT Token Issues**: Verify JWT secret configuration
- **Port Conflicts**: Ensure port 8080 is available
- **Memory Issues**: Increase JVM heap size if needed

### Getting Help
- Check the [Troubleshooting Guide](docs/TROUBLESHOOTING.md)
- Review the [Project Documentation](docs/PROJECT_DOCUMENTATION.md)
- Examine the [Application Flow](docs/APPLICATION_FLOW.md)

---

**KitchenSink Application** - A comprehensive Spring Boot application demonstrating modern web development practices with JWT authentication, MongoDB integration, and role-based access control. 