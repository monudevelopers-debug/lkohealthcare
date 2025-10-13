#!/bin/bash

# Database Migration Script for Lucknow Healthcare Services
# This script runs database migrations using Flyway

set -e

echo "🗄️ Running database migrations..."

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

# Check if Flyway is available
check_flyway() {
    if ! command -v flyway &> /dev/null; then
        print_error "Flyway is not installed. Please install Flyway CLI."
        echo "Download from: https://flywaydb.org/documentation/commandline/"
        exit 1
    fi
}

# Run migrations
run_migrations() {
    print_status "Running database migrations..."
    
    cd backend
    
    # Run Flyway migrations
    ./mvnw flyway:migrate
    
    print_status "Database migrations completed successfully!"
    
    cd ..
}

# Main function
main() {
    check_flyway
    run_migrations
    
    echo ""
    print_status "Database setup completed! 🎉"
    echo ""
    echo "Database is ready for development."
    echo "You can now start the backend application."
}

# Run main function
main "$@"
