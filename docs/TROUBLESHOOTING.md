# Troubleshooting Guide

## Test Failures

### Common Test Issues

#### 1. MockMvc Configuration Issues
**Problem**: `No qualifying bean of type 'org.springframework.test.web.servlet.MockMvc'`
**Solution**: 
- Use `@WebMvcTest` instead of `@SpringBootTest` for controller tests
- Add `@MockBean` for required dependencies
- Use `@ActiveProfiles("test")` for test configuration

#### 2. MongoDB Connection Issues in Tests
**Problem**: Tests fail due to MongoDB connection
**Solution**:
- Use embedded MongoDB for tests
- Add `de.flapdoodle.embed.mongo.spring3x` dependency
- Configure test profile with embedded MongoDB

#### 3. JWT Token Issues
**Problem**: JWT token generation/validation fails
**Solution**:
- Ensure JWT secret key is properly configured
- Use test-specific JWT configuration
- Mock JWT services in unit tests

### Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=JwtTokenServiceTest

# Run tests with debug output
mvn test -X

# Skip tests for build
mvn clean package -DskipTests
```

## Docker Build Issues

### Common Docker Issues

#### 1. Build Context Issues
**Problem**: Docker build fails due to missing files
**Solution**:
- Ensure all required files are in the build context
- Check `.dockerignore` file
- Verify file permissions

#### 2. Maven Build Issues in Docker
**Problem**: Maven build fails inside Docker
**Solution**:
- Use multi-stage build
- Cache Maven dependencies
- Use proper Maven wrapper

#### 3. Port Conflicts
**Problem**: Port already in use
**Solution**:
- Change ports in docker-compose.yml
- Stop existing containers
- Use different port mappings

### Docker Commands

```bash
# Build Docker image
docker build -t kitchensink-app .

# Run Docker Compose
docker-compose up -d

# Check container logs
docker-compose logs

# Stop services
docker-compose down

# Clean up
docker-compose down -v
docker system prune -f
```

## Application Issues

### Common Runtime Issues

#### 1. MongoDB Connection
**Problem**: Application can't connect to MongoDB
**Solution**:
- Check MongoDB container is running
- Verify connection string
- Check network connectivity

#### 2. JWT Configuration
**Problem**: JWT tokens not working
**Solution**:
- Verify JWT secret key is set
- Check token expiration settings
- Ensure proper JWT dependencies

#### 3. Security Configuration
**Problem**: Security filters not working
**Solution**:
- Check SecurityConfig configuration
- Verify filter order
- Test with different authentication methods

### Debug Commands

```bash
# Check application logs
docker-compose logs backend

# Access MongoDB
docker exec -it kitchensink-mongodb mongosh

# Test API endpoints
curl -X GET http://localhost:8080/actuator/health

# Check container status
docker-compose ps
```

## Environment Issues

### Java Version
- Ensure Java 21 is installed
- Check JAVA_HOME environment variable
- Verify Maven version compatibility

### Maven Issues
```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn dependency:resolve

# Check Maven version
mvn --version
```

### Network Issues
- Check firewall settings
- Verify port availability
- Test network connectivity

## Quick Fixes

### Reset Everything
```bash
# Stop all containers
docker-compose down -v

# Clean Maven
mvn clean

# Rebuild everything
mvn clean package
docker-compose up --build
```

### Test Build Process
```bash
# Run build test script
./build-test.sh

# Or on Windows
build-test.bat
```

### Check System Resources
```bash
# Check disk space
df -h

# Check memory
free -h

# Check Docker resources
docker system df
```

## Common Error Messages

### Test Errors
- `No qualifying bean of type 'MockMvc'`: Use `@WebMvcTest`
- `MongoDB connection failed`: Use embedded MongoDB for tests
- `JWT token invalid`: Check JWT configuration

### Docker Errors
- `Build context failed`: Check file permissions and `.dockerignore`
- `Port already in use`: Change ports or stop existing containers
- `Container failed to start`: Check logs with `docker-compose logs`

### Application Errors
- `Connection refused`: Check if MongoDB is running
- `Authentication failed`: Verify JWT configuration
- `404 Not Found`: Check endpoint mappings

## Performance Issues

### Slow Tests
- Use `@DirtiesContext` sparingly
- Mock external dependencies
- Use embedded databases for tests

### Slow Docker Builds
- Use multi-stage builds
- Cache dependencies
- Optimize Dockerfile layers

### Memory Issues
- Increase JVM heap size
- Use resource limits in Docker
- Monitor container resource usage

## Getting Help

### Debug Information
```bash
# Application logs
docker-compose logs -f backend

# Test logs
mvn test -X

# System information
docker version
java -version
mvn --version
```

### Common Commands
```bash
# Quick health check
curl http://localhost:8080/actuator/health

# Test JWT endpoint
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'

# Check MongoDB
docker exec -it kitchensink-mongodb mongosh --eval "db.adminCommand('ping')"
``` 