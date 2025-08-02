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
- **MongoDB Express** web interface for database management

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
- **MongoDB Express**: http://localhost:8081 (admin/admin123)
- **Health Check**: http://localhost:8080/actuator/health
- **MongoDB**: localhost:27017

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
- **Database**: kitchensinkdb
- **Persistence**: Docker volume `mongodb_data`
- **Authentication**: Disabled for development simplicity

### MongoDB Express Service
- **Image**: mongo-express:latest
- **Port**: 8081
- **URL**: http://localhost:8081
- **Credentials**: admin/admin123
- **Features**: Web-based MongoDB management interface

### Application Service
- **Port**: 8080
- **Profile**: docker
- **Dependencies**: MongoDB (with health check)
- **Health Check**: Available at `/actuator/health`

## MongoDB Data Access

### Option 1: MongoDB Express (Web Interface) - Recommended
1. Open your browser and navigate to: http://localhost:8081
2. Login with credentials: `admin` / `admin123`
3. Navigate to `kitchensinkdb` database
4. Explore collections: `members` and `users`

### Option 2: MongoDB Compass (Desktop GUI)
1. Download MongoDB Compass from: https://www.mongodb.com/try/download/compass
2. Install and open MongoDB Compass
3. Connect using: `mongodb://localhost:27017`
4. Navigate to `kitchensinkdb` database

### Option 3: MongoDB Shell (Command Line)
```bash
# Connect to MongoDB shell
docker exec -it mongodb mongosh kitchensinkdb

# Useful commands:
show collections                    # Show all collections
db.members.find()                  # View all members
db.users.find()                    # View all users
db.members.find().pretty()         # Pretty print results
db.members.countDocuments()        # Count documents
db.members.find({email: "admin@admin.com"})  # Search by email
```

### Option 4: Direct Docker Commands
```bash
# View all containers
docker-compose ps

# View MongoDB logs
docker-compose logs mongodb

# Execute MongoDB commands
docker exec -it mongodb mongosh --eval "db.members.find()"
```

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
- `POST /api/auth/login` - Authenticate user and get JWT token
- `POST /api/auth/validate` - Validate JWT token

### Members
- `GET /admin/members` - Get all members (requires ADMIN role)
- `GET /admin/members/{id}` - Get member by ID (requires ADMIN role)
- `POST /admin/members` - Create new member (requires ADMIN role)
- `PUT /admin/members/{id}` - Update member (requires ADMIN role)
- `DELETE /admin/members/{id}` - Delete member (requires ADMIN role)

### Health & Monitoring
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information

### Swagger Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs

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
- **docker**: Uses local MongoDB container (no auth)
- **test**: Test configuration

## Database Schema

### Members Collection
```javascript
{
  "_id": ObjectId,
  "name": String,
  "email": String,
  "phoneNumber": String,
  "dateOfBirth": Date,
  "createdAt": Date,
  "updatedAt": Date
}
```

### Users Collection
```javascript
{
  "_id": ObjectId,
  "email": String,
  "password": String (encrypted),
  "role": String ("ADMIN" or "USER"),
  "enabled": Boolean,
  "createdAt": Date
}
```

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

4. **MongoDB Express Not Accessible**
   ```bash
   # Check if mongo-express container is running
   docker-compose ps mongo-express
   
   # Check mongo-express logs
   docker-compose logs mongo-express
   ```

### Health Checks

The application includes health checks for all services:

- **MongoDB**: Checks if MongoDB is responding to ping commands
- **MongoDB Express**: Web interface for database management
- **Application**: Checks if the Spring Boot application is healthy

### Logs

```bash
# View all logs
docker-compose logs

# View specific service logs
docker-compose logs mongodb
docker-compose logs mongo-express
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
- Health checks for all services
- Application metrics
- Logging with configurable levels
- MongoDB Express for database monitoring

## Data Management

### Backup MongoDB Data
```bash
# Create backup
docker exec mongodb mongodump --db kitchensinkdb --out /backup

# Copy backup from container
docker cp mongodb:/backup ./backup
```

### Restore MongoDB Data
```bash
# Copy backup to container
docker cp ./backup mongodb:/backup

# Restore data
docker exec mongodb mongorestore --db kitchensinkdb /backup/kitchensinkdb
```

### Clear All Data
```bash
# Remove all containers and volumes
docker-compose down -v

# Rebuild and start fresh
docker-compose up --build -d
```
