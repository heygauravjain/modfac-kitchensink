# KitchenSink Build Guide

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Development Setup](#development-setup)
3. [Build Process](#build-process)
4. [Testing](#testing)
5. [Deployment](#deployment)
6. [Troubleshooting](#troubleshooting)
7. [Performance Optimization](#performance-optimization)

---

## Prerequisites

### System Requirements
- **Java**: JDK 21 or higher
- **Maven**: 3.6 or higher
- **MongoDB**: 4.4 or higher
- **Git**: Latest version
- **Memory**: Minimum 2GB RAM (4GB recommended)
- **Storage**: 1GB free space

### Development Tools
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code
- **Browser**: Chrome, Firefox, Safari, or Edge (latest versions)
- **Docker**: Optional for containerized development
- **Node.js**: Optional for frontend tooling

### Required Software Installation

#### Java 21 Installation
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# macOS (using Homebrew)
brew install openjdk@21

# Windows
# Download from Oracle or use Chocolatey
choco install openjdk21

# Verify installation
java -version
javac -version
```

#### Maven Installation
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# Windows
# Download from Apache Maven website

# Verify installation
mvn -version
```

#### MongoDB Installation
```bash
# Ubuntu/Debian
sudo apt install mongodb

# macOS
brew install mongodb-community

# Windows
# Download from MongoDB website

# Start MongoDB
sudo systemctl start mongod
sudo systemctl enable mongod
```

---

## Development Setup

### 1. Clone Repository
```bash
git clone <repository-url>
cd modfac-kitchensink
```

### 2. Configure Environment

#### Application Properties
Create `src/main/resources/application-dev.yml`:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/kitchensink-dev
  security:
    jwt:
      secret: your-development-secret-key
      expiration: 86400000
      refresh-expiration: 604800000

logging:
  level:
    com.example.kitchensink: DEBUG
    org.springframework.security: DEBUG
```

#### Environment Variables
```bash
# Development environment
export SPRING_PROFILES_ACTIVE=dev
export MONGODB_URI=mongodb://localhost:27017/kitchensink-dev
export JWT_SECRET=your-development-secret-key

# Production environment
export SPRING_PROFILES_ACTIVE=prod
export MONGODB_URI=mongodb://prod-mongo:27017/kitchensink
export JWT_SECRET=your-production-secret-key
```

### 3. IDE Configuration

#### IntelliJ IDEA Setup
1. **Open Project**: File → Open → Select project folder
2. **Import Maven Project**: Import as Maven project
3. **Configure SDK**: File → Project Structure → SDK → Java 21
4. **Enable Annotation Processing**: Settings → Build → Compiler → Annotation Processors → Enable
5. **Install Plugins**:
   - Lombok
   - Spring Boot
   - Thymeleaf

#### Eclipse Setup
1. **Import Project**: File → Import → Maven → Existing Maven Projects
2. **Configure JRE**: Project Properties → Java Build Path → Libraries → Add Library → JRE System Library
3. **Install Plugins**:
   - Spring Tools Suite
   - Lombok

#### VS Code Setup
1. **Install Extensions**:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Thymeleaf Support
2. **Configure Java**: Set Java 21 as default
3. **Install Lombok**: Download and install Lombok jar

### 4. Database Setup

#### MongoDB Configuration
```bash
# Start MongoDB
sudo systemctl start mongod

# Verify connection
mongo --eval "db.runCommand('ping')"

# Create database
mongo
use kitchensink-dev
db.createUser({
  user: "kitchensink",
  pwd: "password",
  roles: ["readWrite"]
})
```

#### Docker MongoDB (Alternative)
```bash
# Run MongoDB in Docker
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  mongo:latest

# Connect to MongoDB
docker exec -it mongodb mongosh
```

---

## Build Process

### 1. Clean Build
```bash
# Clean previous builds
mvn clean

# Compile and package
mvn package

# Run tests
mvn test

# Generate reports
mvn jacoco:report
```

### 2. Development Build
```bash
# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### 3. Production Build
```bash
# Build for production
mvn clean package -Pprod

# Create Docker image
docker build -t kitchensink:latest .

# Run with production profile
java -jar target/kitchensink-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 4. Docker Build
```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/kitchensink-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build Docker image
docker build -t kitchensink .

# Run container
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/kitchensink \
  kitchensink
```

---

## Testing

### 1. Unit Tests
```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=AuthServiceTest

# Run tests with coverage
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### 2. Integration Tests
```bash
# Run integration tests
mvn test -Dtest=*IntegrationTest

# Run with test profile
mvn test -Dspring.profiles.active=test
```

### 3. Security Tests
```bash
# Run security tests
mvn test -Dtest=*SecurityTest

# Test authentication flows
mvn test -Dtest=JwtAuthenticationFilterTest
```

### 4. UI Tests
```bash
# Manual UI testing checklist
- [ ] Login functionality
- [ ] Theme switching
- [ ] Form validation
- [ ] Responsive design
- [ ] Accessibility features
- [ ] Error handling
```

### 5. Performance Tests
```bash
# Load testing with Apache Bench
ab -n 1000 -c 10 http://localhost:8080/

# Memory profiling
java -Xprof -jar target/kitchensink-0.0.1-SNAPSHOT.jar
```

---

## Deployment

### 1. Local Deployment
```bash
# Build application
mvn clean package

# Run application
java -jar target/kitchensink-0.0.1-SNAPSHOT.jar

# Access application
open http://localhost:8080
```

### 2. Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f app
```

### 3. Kubernetes Deployment
```yaml
# deployment.yaml
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
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: MONGODB_URI
          valueFrom:
            secretKeyRef:
              name: mongodb-secret
              key: uri
```

```bash
# Deploy to Kubernetes
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml
```

### 4. Cloud Deployment

#### AWS Deployment
```bash
# Build Docker image
docker build -t kitchensink .

# Tag for ECR
docker tag kitchensink:latest 123456789012.dkr.ecr.us-east-1.amazonaws.com/kitchensink:latest

# Push to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-east-1.amazonaws.com
docker push 123456789012.dkr.ecr.us-east-1.amazonaws.com/kitchensink:latest

# Deploy to ECS
aws ecs update-service --cluster kitchensink-cluster --service kitchensink-service --force-new-deployment
```

#### Google Cloud Deployment
```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/kitchensink

# Deploy to Cloud Run
gcloud run deploy kitchensink \
  --image gcr.io/PROJECT_ID/kitchensink \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

---

## Troubleshooting

### Common Issues

#### 1. Build Failures
```bash
# Clean and rebuild
mvn clean package

# Check Java version
java -version

# Check Maven version
mvn -version

# Update dependencies
mvn dependency:resolve
```

#### 2. Database Connection Issues
```bash
# Check MongoDB status
sudo systemctl status mongod

# Check MongoDB logs
sudo journalctl -u mongod

# Test connection
mongo --eval "db.runCommand('ping')"

# Reset MongoDB
sudo systemctl restart mongod
```

#### 3. Port Conflicts
```bash
# Check port usage
netstat -tulpn | grep 8080

# Kill process using port
sudo kill -9 <PID>

# Change port
java -jar app.jar --server.port=8081
```

#### 4. Memory Issues
```bash
# Increase heap size
java -Xmx2g -jar app.jar

# Monitor memory usage
jstat -gc <PID>

# Check memory settings
java -XX:+PrintFlagsFinal -version | grep -i heapsize
```

### Debug Mode
```bash
# Enable debug logging
mvn spring-boot:run -Dlogging.level.com.example.kitchensink=DEBUG

# Remote debugging
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Performance Issues

#### Database Performance
```bash
# Check MongoDB indexes
mongo
use kitchensink
db.members.getIndexes()

# Monitor slow queries
db.setProfilingLevel(1, 100)

# Optimize queries
db.members.find().explain("executionStats")
```

#### Application Performance
```bash
# Monitor JVM metrics
jstat -gc <PID>

# Profile with JProfiler
java -agentpath:/path/to/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849 -jar app.jar

# Use VisualVM
jvisualvm
```

---

## Performance Optimization

### 1. JVM Optimization
```bash
# Production JVM settings
java -server \
  -Xms1g \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -jar app.jar
```

### 2. Database Optimization
```javascript
// Create indexes for better performance
db.members.createIndex({ "email": 1 }, { unique: true })
db.members.createIndex({ "role": 1 })
db.members.createIndex({ "createdAt": -1 })

// Monitor query performance
db.members.find({ "role": "USER" }).explain("executionStats")
```

### 3. Application Optimization
```java
// Enable caching
@EnableCaching
@Configuration
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("members", "users");
    }
}

// Optimize database queries
@Query(value = "{ 'role': ?0 }", fields = "{ 'password': 0 }")
List<MemberDocument> findByRole(String role);
```

### 4. Frontend Optimization
```html
<!-- Preload critical resources -->
<link rel="preload" th:href="@{/css/unified-theme.css}" as="style">
<link rel="preload" th:href="@{/js/theme-toggle.js}" as="script">

<!-- Lazy load images -->
<img data-src="/images/large-image.jpg" class="lazy" alt="Description">
```

```javascript
// Implement lazy loading
const lazyLoadImages = () => {
    const images = document.querySelectorAll('img[data-src]');
    const imageObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const img = entry.target;
                img.src = img.dataset.src;
                imageObserver.unobserve(img);
            }
        });
    });
    images.forEach(img => imageObserver.observe(img));
};
```

---

## Monitoring and Health Checks

### 1. Application Health
```bash
# Health check endpoint
curl http://localhost:8080/actuator/health

# Detailed health information
curl http://localhost:8080/actuator/health/details

# Application info
curl http://localhost:8080/actuator/info
```

### 2. Metrics Monitoring
```bash
# Application metrics
curl http://localhost:8080/actuator/metrics

# JVM metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# HTTP metrics
curl http://localhost:8080/actuator/metrics/http.server.requests
```

### 3. Log Monitoring
```bash
# View application logs
tail -f logs/application.log

# Filter error logs
grep "ERROR" logs/application.log

# Monitor specific components
grep "com.example.kitchensink" logs/application.log
```

---

## Security Considerations

### 1. Production Security
```bash
# Use strong JWT secret
export JWT_SECRET=$(openssl rand -base64 64)

# Enable HTTPS
server:
  ssl:
    key-store: classpath:keystore.p12
    key-store-password: your-keystore-password
    key-store-type: PKCS12

# Configure CORS
spring:
  web:
    cors:
      allowed-origins: "https://yourdomain.com"
      allowed-methods: "GET,POST,PUT,DELETE"
```

### 2. Database Security
```javascript
// Create secure database user
use admin
db.createUser({
  user: "kitchensink",
  pwd: "strong-password",
  roles: [
    { role: "readWrite", db: "kitchensink" }
  ]
})

// Enable authentication
security:
  authorization: enabled
```

### 3. Network Security
```bash
# Configure firewall
sudo ufw allow 8080
sudo ufw allow 27017

# Use VPN for remote access
# Implement rate limiting
# Enable DDoS protection
```

---

## Summary

This comprehensive build guide provides all the necessary steps to:

1. **Set up the development environment** with all required tools
2. **Build the application** for different environments
3. **Test the application** thoroughly
4. **Deploy the application** to various platforms
5. **Troubleshoot common issues** effectively
6. **Optimize performance** for production use
7. **Monitor and maintain** the application

The guide ensures that developers can successfully build, test, and deploy the KitchenSink application while following best practices for security, performance, and maintainability. 