# Build Guide

## Quick Start

### Prerequisites
- Java 21
- Maven 3.8+
- Docker (optional)

### Build Commands

#### 1. Simple Build (Skip Tests)
```bash
mvn clean package -DskipTests
```

#### 2. Build with Tests
```bash
mvn clean install
```

#### 3. Run Tests Only
```bash
mvn test
```

#### 4. Compile Only
```bash
mvn clean compile
```

## Troubleshooting Build Issues

### Common Issues and Solutions

#### 1. Java Version Issues
**Problem**: `UnsupportedClassVersionError`
**Solution**: Ensure Java 21 is installed and JAVA_HOME is set correctly

```bash
# Check Java version
java -version

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-21

# Set JAVA_HOME (Linux/Mac)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
```

#### 2. Maven Issues
**Problem**: `mvn command not found`
**Solution**: Use Maven wrapper

```bash
# Use Maven wrapper
./mvnw clean install

# On Windows
mvnw.cmd clean install
```

#### 3. Dependency Issues
**Problem**: `Could not resolve dependencies`
**Solution**: Clean and update dependencies

```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn dependency:resolve

# Force update
mvn clean install -U
```

#### 4. Test Issues
**Problem**: Tests fail due to MongoDB connection
**Solution**: Skip tests for build

```bash
# Build without tests
mvn clean package -DskipTests

# Run only specific tests
mvn test -Dtest=KitchenSinkApplicationTests
```

#### 5. Compilation Issues
**Problem**: Compilation errors
**Solution**: Check for missing imports or syntax errors

```bash
# Compile with verbose output
mvn clean compile -X

# Check specific compilation errors
mvn clean compile -e
```

## Build Scripts

### Windows
```bash
# Run diagnostic script
diagnose-build.bat

# Run build test
build-test.bat
```

### Linux/Mac
```bash
# Run diagnostic script
./diagnose-build.sh

# Run build test
./build-test.sh
```

## Docker Build

### Build Docker Image
```bash
docker build -t kitchensink-app .
```

### Run with Docker Compose
```bash
docker-compose up --build
```

### Test Docker Build
```bash
./docker-test.sh
```

## Project Structure

```
modfac-kitchensink/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/kitchensink/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── security/
│   │   │       ├── entity/
│   │   │       ├── model/
│   │   │       └── config/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── templates/
│   └── test/
│       ├── java/
│       │   └── com/example/kitchensink/
│       └── resources/
│           └── application-test.yml
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

## Configuration Files

### Main Application Configuration
- `src/main/resources/application.yml` - Main application config
- `src/main/resources/application-docker.yml` - Docker profile config

### Test Configuration
- `src/test/resources/application-test.yml` - Test profile config

### Build Configuration
- `pom.xml` - Maven dependencies and plugins
- `Dockerfile` - Docker image configuration
- `docker-compose.yml` - Docker services configuration

## Key Dependencies

### Spring Boot Starters
- `spring-boot-starter-web` - Web application
- `spring-boot-starter-security` - Security framework
- `spring-boot-starter-data-mongodb` - MongoDB integration
- `spring-boot-starter-thymeleaf` - Template engine
- `spring-boot-starter-validation` - Validation framework

### JWT Dependencies
- `jjwt-api` - JWT API
- `jjwt-impl` - JWT implementation
- `jjwt-jackson` - JWT Jackson integration

### Testing Dependencies
- `junit-jupiter` - JUnit 5
- `spring-boot-starter-test` - Spring Boot test support
- `mockito-core` - Mocking framework
- `spring-security-test` - Security testing

## Build Phases

### 1. Clean
```bash
mvn clean
```
- Removes target directory
- Cleans compiled classes

### 2. Compile
```bash
mvn compile
```
- Compiles main source code
- Generates classes in target/classes

### 3. Test Compile
```bash
mvn test-compile
```
- Compiles test source code
- Generates test classes in target/test-classes

### 4. Test
```bash
mvn test
```
- Runs unit tests
- Generates test reports

### 5. Package
```bash
mvn package
```
- Creates JAR file
- Includes all dependencies

### 6. Install
```bash
mvn install
```
- Installs JAR to local repository
- Available for other projects

## Environment Variables

### Development
```bash
export SPRING_PROFILES_ACTIVE=default
export SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/kitchensinkdb
```

### Docker
```bash
export SPRING_PROFILES_ACTIVE=docker
export SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/kitchensinkdb
export JWT_SECRET_KEY=your-secret-key
```

### Testing
```bash
export SPRING_PROFILES_ACTIVE=test
```

## Performance Optimization

### Maven Build Optimization
```bash
# Parallel build
mvn clean install -T 1C

# Skip tests for faster build
mvn clean package -DskipTests

# Use Maven wrapper
./mvnw clean install
```

### Docker Build Optimization
```bash
# Use build cache
docker build --cache-from kitchensink-app .

# Multi-stage build (already configured)
docker build -t kitchensink-app .
```

## Monitoring and Debugging

### Build Logs
```bash
# Verbose output
mvn clean install -X

# Debug output
mvn clean install -e

# Quiet output
mvn clean install -q
```

### Test Reports
```bash
# Generate test reports
mvn test jacoco:report

# View test results
open target/site/jacoco/index.html
```

### Docker Logs
```bash
# View container logs
docker-compose logs

# Follow logs
docker-compose logs -f

# View specific service logs
docker-compose logs backend
```

## Common Commands

### Development
```bash
# Run application
mvn spring-boot:run

# Run with profile
mvn spring-boot:run -Dspring.profiles.active=docker
```

### Testing
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=SimpleBuildTest

# Run with coverage
mvn test jacoco:report
```

### Docker
```bash
# Build and run
docker-compose up --build

# Stop services
docker-compose down

# Clean up
docker-compose down -v
docker system prune -f
```

## Troubleshooting

### Build Fails
1. Check Java version: `java -version`
2. Check Maven version: `mvn --version`
3. Clean project: `mvn clean`
4. Update dependencies: `mvn dependency:resolve`
5. Run with verbose output: `mvn clean install -X`

### Tests Fail
1. Check test configuration: `application-test.yml`
2. Run specific test: `mvn test -Dtest=SimpleBuildTest`
3. Skip tests for build: `mvn clean package -DskipTests`

### Docker Issues
1. Check Docker installation: `docker --version`
2. Check Docker Compose: `docker-compose --version`
3. Clean Docker: `docker system prune -f`
4. Rebuild: `docker-compose up --build`

### Runtime Issues
1. Check application logs
2. Verify configuration files
3. Check environment variables
4. Test endpoints: `curl http://localhost:8080/actuator/health` 