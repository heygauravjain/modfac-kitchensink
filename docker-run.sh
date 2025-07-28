#!/bin/bash

# KitchenSink Docker Management Script

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
}

# Function to build and start the application
start() {
    print_header "Starting KitchenSink Application"
    check_docker
    
    print_status "Building and starting services..."
    docker-compose up --build -d
    
    print_status "Waiting for services to be ready..."
    sleep 10
    
    # Check if services are healthy
    if docker-compose ps | grep -q "healthy"; then
        print_status "Application is ready!"
        print_status "Access the application at: http://localhost:8080"
        print_status "Health check: http://localhost:8080/actuator/health"
    else
        print_warning "Some services may still be starting up. Check logs with: docker-compose logs"
    fi
}

# Function to stop the application
stop() {
    print_header "Stopping KitchenSink Application"
    check_docker
    
    print_status "Stopping services..."
    docker-compose down
    
    print_status "Application stopped successfully!"
}

# Function to restart the application
restart() {
    print_header "Restarting KitchenSink Application"
    stop
    start
}

# Function to show logs
logs() {
    print_header "Showing Application Logs"
    check_docker
    
    if [ "$1" = "follow" ]; then
        print_status "Following logs (Ctrl+C to exit)..."
        docker-compose logs -f
    else
        docker-compose logs
    fi
}

# Function to show status
status() {
    print_header "Application Status"
    check_docker
    
    docker-compose ps
}

# Function to clean up everything
clean() {
    print_header "Cleaning Up Docker Resources"
    check_docker
    
    print_warning "This will remove all containers, volumes, and images for this project."
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_status "Stopping and removing containers..."
        docker-compose down -v
        
        print_status "Removing images..."
        docker rmi $(docker images -q kitchensink-kitchensink-app) 2>/dev/null || true
        
        print_status "Cleanup completed!"
    else
        print_status "Cleanup cancelled."
    fi
}

# Function to show help
help() {
    print_header "KitchenSink Docker Management Script"
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start     Build and start the application"
    echo "  stop      Stop the application"
    echo "  restart   Restart the application"
    echo "  logs      Show application logs"
    echo "  logs follow  Follow logs in real-time"
    echo "  status    Show application status"
    echo "  clean     Remove all containers, volumes, and images"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start"
    echo "  $0 logs follow"
    echo "  $0 clean"
}

# Main script logic
case "${1:-help}" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    logs)
        logs "$2"
        ;;
    status)
        status
        ;;
    clean)
        clean
        ;;
    help|--help|-h)
        help
        ;;
    *)
        print_error "Unknown command: $1"
        help
        exit 1
        ;;
esac 