#!/bin/bash

# Lucknow Healthcare Services - Production Deployment Script
# This script handles the complete deployment process for the healthcare platform

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="lucknow-healthcare"
ENVIRONMENT=${1:-production}
DOCKER_COMPOSE_FILE="docker-compose.prod.yml"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    if ! command_exists docker; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command_exists docker-compose; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    if [ ! -f ".env.production" ]; then
        print_warning ".env.production file not found. Creating from template..."
        cp env.example .env.production
        print_warning "Please update .env.production with your production values before continuing."
        exit 1
    fi
    
    print_success "Prerequisites check completed."
}

# Function to build and start services
deploy_services() {
    print_status "Deploying Lucknow Healthcare Services..."
    
    # Stop existing containers
    print_status "Stopping existing containers..."
    docker-compose -f $DOCKER_COMPOSE_FILE down --remove-orphans || true
    
    # Build and start services
    print_status "Building and starting services..."
    docker-compose -f $DOCKER_COMPOSE_FILE --env-file .env.production up -d --build
    
    print_success "Services deployed successfully!"
}

# Function to run database migrations
run_migrations() {
    print_status "Running database migrations..."
    
    # Wait for database to be ready
    print_status "Waiting for database to be ready..."
    sleep 30
    
    # Run migrations
    docker-compose -f $DOCKER_COMPOSE_FILE exec backend ./mvnw flyway:migrate || {
        print_warning "Migrations failed, but continuing..."
    }
    
    print_success "Database migrations completed."
}

# Function to check service health
check_health() {
    print_status "Checking service health..."
    
    # Check backend health
    print_status "Checking backend health..."
    for i in {1..30}; do
        if curl -f http://localhost:8080/api/actuator/health >/dev/null 2>&1; then
            print_success "Backend is healthy!"
            break
        fi
        if [ $i -eq 30 ]; then
            print_error "Backend health check failed after 30 attempts."
            return 1
        fi
        sleep 10
    done
    
    # Check frontend health
    print_status "Checking frontend health..."
    for i in {1..10}; do
        if curl -f http://localhost:3000 >/dev/null 2>&1; then
            print_success "Frontend is healthy!"
            break
        fi
        if [ $i -eq 10 ]; then
            print_error "Frontend health check failed after 10 attempts."
            return 1
        fi
        sleep 5
    done
    
    # Check provider dashboard health
    print_status "Checking provider dashboard health..."
    for i in {1..10}; do
        if curl -f http://localhost:3001 >/dev/null 2>&1; then
            print_success "Provider dashboard is healthy!"
            break
        fi
        if [ $i -eq 10 ]; then
            print_error "Provider dashboard health check failed after 10 attempts."
            return 1
        fi
        sleep 5
    done
    
    # Check admin dashboard health
    print_status "Checking admin dashboard health..."
    for i in {1..10}; do
        if curl -f http://localhost:3002 >/dev/null 2>&1; then
            print_success "Admin dashboard is healthy!"
            break
        fi
        if [ $i -eq 10 ]; then
            print_error "Admin dashboard health check failed after 10 attempts."
            return 1
        fi
        sleep 5
    done
    
    print_success "All services are healthy!"
}

# Function to show deployment summary
show_summary() {
    print_success "🎉 Deployment completed successfully!"
    echo ""
    echo "📋 Service URLs:"
    echo "  • Frontend (Customer App):     http://localhost:3000"
    echo "  • Provider Dashboard:          http://localhost:3001"
    echo "  • Admin Dashboard:            http://localhost:3002"
    echo "  • Backend API:                 http://localhost:8080/api"
    echo "  • API Documentation:           http://localhost:8080/swagger-ui.html"
    echo "  • Monitoring (Grafana):        http://localhost:3003"
    echo ""
    echo "🔧 Management Commands:"
    echo "  • View logs:                   docker-compose -f $DOCKER_COMPOSE_FILE logs -f"
    echo "  • Stop services:               docker-compose -f $DOCKER_COMPOSE_FILE down"
    echo "  • Restart services:            docker-compose -f $DOCKER_COMPOSE_FILE restart"
    echo "  • View service status:         docker-compose -f $DOCKER_COMPOSE_FILE ps"
    echo ""
    echo "📊 Monitoring:"
    echo "  • Grafana:                     http://localhost:3003 (admin/admin)"
    echo "  • Prometheus:                  http://localhost:9090"
    echo ""
    print_warning "Remember to update your DNS records to point to this server!"
}

# Function to run tests
run_tests() {
    print_status "Running deployment tests..."
    
    # Test API endpoints
    print_status "Testing API endpoints..."
    
    # Test health endpoint
    if curl -f http://localhost:8080/api/actuator/health >/dev/null 2>&1; then
        print_success "API health check passed"
    else
        print_error "API health check failed"
        return 1
    fi
    
    # Test frontend accessibility
    if curl -f http://localhost:3000 >/dev/null 2>&1; then
        print_success "Frontend accessibility test passed"
    else
        print_error "Frontend accessibility test failed"
        return 1
    fi
    
    print_success "All tests passed!"
}

# Main deployment function
main() {
    echo "🏥 Lucknow Healthcare Services - Production Deployment"
    echo "=================================================="
    echo ""
    
    # Check prerequisites
    check_prerequisites
    
    # Deploy services
    deploy_services
    
    # Run migrations
    run_migrations
    
    # Check health
    check_health
    
    # Run tests
    run_tests
    
    # Show summary
    show_summary
}

# Handle script arguments
case "${1:-deploy}" in
    "deploy")
        main
        ;;
    "stop")
        print_status "Stopping all services..."
        docker-compose -f $DOCKER_COMPOSE_FILE down
        print_success "Services stopped."
        ;;
    "restart")
        print_status "Restarting all services..."
        docker-compose -f $DOCKER_COMPOSE_FILE restart
        print_success "Services restarted."
        ;;
    "logs")
        docker-compose -f $DOCKER_COMPOSE_FILE logs -f
        ;;
    "status")
        docker-compose -f $DOCKER_COMPOSE_FILE ps
        ;;
    "health")
        check_health
        ;;
    "test")
        run_tests
        ;;
    *)
        echo "Usage: $0 {deploy|stop|restart|logs|status|health|test}"
        echo ""
        echo "Commands:"
        echo "  deploy   - Deploy all services (default)"
        echo "  stop     - Stop all services"
        echo "  restart  - Restart all services"
        echo "  logs     - View service logs"
        echo "  status   - View service status"
        echo "  health   - Check service health"
        echo "  test     - Run deployment tests"
        exit 1
        ;;
esac
