#!/bin/bash

# Lucknow Healthcare Services - Development Setup Script
# This script sets up the development environment for the MVP

set -e

echo "🏥 Setting up Lucknow Healthcare Services MVP..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}✓${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

# Check if required tools are installed
check_requirements() {
    print_status "Checking requirements..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java 17+ is required but not installed."
        exit 1
    fi
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        print_error "Node.js 18+ is required but not installed."
        exit 1
    fi
    
    # Check PostgreSQL
    if ! command -v psql &> /dev/null; then
        print_warning "PostgreSQL is not installed. Please install PostgreSQL 15+"
    fi
    
    print_status "Requirements check completed."
}

# Setup backend
setup_backend() {
    print_status "Setting up backend..."
    cd backend
    
    # Create logs directory
    mkdir -p logs
    
    # Create uploads directory
    mkdir -p uploads
    
    # Copy environment file if it doesn't exist
    if [ ! -f .env ]; then
        cp ../env.example .env
        print_warning "Created .env file. Please update with your configuration."
    fi
    
    cd ..
    print_status "Backend setup completed."
}

# Setup frontend
setup_frontend() {
    print_status "Setting up Flutter frontend..."
    cd frontend
    
    # Create assets directories
    mkdir -p assets/{images,fonts,translations}
    
    # Create test directories
    mkdir -p test/{unit,widget,integration}
    
    cd ..
    print_status "Frontend setup completed."
}

# Setup provider dashboard
setup_provider_dashboard() {
    print_status "Setting up provider dashboard..."
    cd provider-dashboard
    
    # Install dependencies
    if [ -f package.json ]; then
        npm install
    fi
    
    cd ..
    print_status "Provider dashboard setup completed."
}

# Setup admin dashboard
setup_admin_dashboard() {
    print_status "Setting up admin dashboard..."
    cd admin-dashboard
    
    # Install dependencies
    if [ -f package.json ]; then
        npm install
    fi
    
    cd ..
    print_status "Admin dashboard setup completed."
}

# Setup database
setup_database() {
    print_status "Setting up database..."
    
    # Create database if it doesn't exist
    if command -v psql &> /dev/null; then
        # Check if database exists
        if ! psql -h localhost -U postgres -lqt | cut -d \| -f 1 | grep -qw lucknow_healthcare; then
            print_status "Creating database..."
            createdb -h localhost -U postgres lucknow_healthcare
        fi
    else
        print_warning "PostgreSQL not found. Please install and configure PostgreSQL."
    fi
    
    print_status "Database setup completed."
}

# Main setup function
main() {
    echo "🚀 Starting setup process..."
    
    check_requirements
    setup_backend
    setup_frontend
    setup_provider_dashboard
    setup_admin_dashboard
    setup_database
    
    echo ""
    print_status "Setup completed successfully! 🎉"
    echo ""
    echo "Next steps:"
    echo "1. Update .env file in backend directory with your configuration"
    echo "2. Start PostgreSQL service"
    echo "3. Run database migrations: ./scripts/migrate.sh"
    echo "4. Start the backend: cd backend && ./mvnw spring-boot:run"
    echo "5. Start the frontend: cd frontend && flutter run"
    echo "6. Start provider dashboard: cd provider-dashboard && npm run dev"
    echo "7. Start admin dashboard: cd admin-dashboard && npm run dev"
    echo ""
    echo "For more information, see README.md"
}

# Run main function
main "$@"
