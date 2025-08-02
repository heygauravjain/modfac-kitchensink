#!/bin/bash

echo "🐳 Testing Docker build..."

# Build the Docker image
echo "📦 Building Docker image..."
docker build -t kitchensink-app .

if [ $? -eq 0 ]; then
    echo "✅ Docker image built successfully"
else
    echo "❌ Docker build failed"
    exit 1
fi

# Test Docker Compose
echo "🐳 Testing Docker Compose..."
docker-compose up -d

if [ $? -eq 0 ]; then
    echo "✅ Docker Compose started successfully"
    
    # Wait for services to be ready
    echo "⏳ Waiting for services to be ready..."
    sleep 30
    
    # Check if services are running
    if docker-compose ps | grep -q "Up"; then
        echo "✅ All services are running"
    else
        echo "❌ Some services failed to start"
        docker-compose logs
        exit 1
    fi
    
    # Stop services
    echo "🛑 Stopping services..."
    docker-compose down
    
    echo "🎉 Docker test completed successfully!"
else
    echo "❌ Docker Compose failed"
    exit 1
fi 