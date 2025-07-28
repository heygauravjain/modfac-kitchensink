# Modernization Factory: Developer Candidate Challenge

A Spring Boot application with MongoDB integration, featuring JWT authentication, REST APIs, and a modern web interface.

## Features

- **Spring Boot 3.2.5** with Java 21
- **MongoDB** integration with Spring Data MongoDB
- **JWT Authentication** with Spring Security
- **RESTful APIs** with proper error handling
- **Thymeleaf** templates for web interface
- **Docker** containerization with multi-stage builds
- **Health checks** and monitoring endpoints

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development)
- Maven (for local development)

## Quick Start with Docker

### 1. Build and Run with Docker Compose

```bash
# Clone the repository
git clone <repository-url>
cd modfac-kitchensink

# Build and start all services
docker-compose up --build

# Or run in detached mode
docker-compose up --build -d
```

### 2. Access the Application

- **Web Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **MongoDB**: localhost:27017 (root/example)

### 3. Stop the Application

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (will delete MongoDB data)
docker-compose down -v
```

## Docker Services

### MongoDB Service
- **Image**: mongo:7
- **Port**: 27017
- **Credentials**: root/example
- **Database**: kitchensinkdb
- **Persistence**: Docker volume `mongodb_data`

### Application Service
- **Port**: 8080
- **Profile**: docker
- **Dependencies**: MongoDB (with health check)
- **Health Check**: Available at `/actuator/health`

## Local Development

### 1. Prerequisites
- Java 21
- Maven 3.6+
- MongoDB (local or Docker)

### 2. Setup MongoDB
```bash
# Using Docker for MongoDB
docker run -d --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=example \
  -e MONGO_INITDB_DATABASE=kitchensinkdb \
  mongo:7
```

### 3. Run Application
```bash
# Build the application
./mvnw clean package

# Run with default profile
java -jar target/kitchensink-0.0.1-SNAPSHOT.jar

# Or run with Maven
./mvnw spring-boot:run
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Members
- `GET /api/members` - Get all members
- `GET /api/members/{id}` - Get member by ID
- `POST /api/members` - Create new member
- `PUT /api/members/{id}` - Update member
- `DELETE /api/members/{id}` - Delete member

### Health & Monitoring
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection string | MongoDB Atlas (default profile) |
| `JWT_SECRET_KEY` | JWT signing key | Generated key |
| `JWT_TOKEN_EXPIRATION` | JWT expiration time | 3600000ms |

### Profiles

- **default**: Uses MongoDB Atlas
- **docker**: Uses local MongoDB container
- **test**: Test configuration

## Troubleshooting

### Common Issues

1. **MongoDB Connection Failed**
   ```bash
   # Check if MongoDB container is running
   docker-compose ps
   
   # Check MongoDB logs
   docker-compose logs mongodb
   ```

2. **Application Won't Start**
   ```bash
   # Check application logs
   docker-compose logs kitchensink-app
   
   # Rebuild the application
   docker-compose up --build
   ```

3. **Port Already in Use**
   ```bash
   # Check what's using the port
   netstat -tulpn | grep :8080
   
   # Stop conflicting services
   docker-compose down
   ```

### Health Checks

The application includes health checks for both services:

- **MongoDB**: Checks if MongoDB is responding to ping commands
- **Application**: Checks if the Spring Boot application is healthy

### Logs

```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs mongodb
docker-compose logs kitchensink-app

# Follow logs in real-time
docker-compose logs -f
```

## Development

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

### Testing

```bash
# Run all tests
./mvnw test

# Run tests with Docker
docker-compose -f docker-compose.test.yml up --build
```

## Security

- JWT-based authentication
- Password encryption with BCrypt
- Role-based access control
- CORS configuration
- Input validation and sanitization

## Performance

- Multi-stage Docker builds for smaller images
- JVM optimizations for containers
- MongoDB connection pooling
- Static resource caching

## Monitoring

- Spring Boot Actuator endpoints
- Health checks for both services
- Application metrics
- Logging with configurable levels
