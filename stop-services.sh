#!/bin/bash

# Healthcare Services Stop Script
# This script stops all services for the Lucknow Healthcare platform

set -e

echo "🛑 Stopping Lucknow Healthcare Services..."
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to stop service by PID file
stop_service_by_pid() {
    local service_name=$1
    local pid_file="${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${BLUE}🛑 Stopping $service_name (PID: $pid)...${NC}"
            kill $pid
            sleep 2
            if ps -p $pid > /dev/null 2>&1; then
                echo -e "${YELLOW}⚠️  Force killing $service_name...${NC}"
                kill -9 $pid
            fi
            echo -e "${GREEN}✅ $service_name stopped${NC}"
        else
            echo -e "${YELLOW}⚠️  $service_name was not running${NC}"
        fi
        rm -f "$pid_file"
    else
        echo -e "${YELLOW}⚠️  No PID file found for $service_name${NC}"
    fi
}

# Function to stop service by port
stop_service_by_port() {
    local port=$1
    local service_name=$2
    
    local pid=$(lsof -ti:$port)
    if [ ! -z "$pid" ]; then
        echo -e "${BLUE}🛑 Stopping $service_name on port $port (PID: $pid)...${NC}"
        kill $pid
        sleep 2
        if lsof -ti:$port > /dev/null; then
            echo -e "${YELLOW}⚠️  Force killing process on port $port...${NC}"
            kill -9 $pid
        fi
        echo -e "${GREEN}✅ $service_name stopped${NC}"
    else
        echo -e "${YELLOW}⚠️  No process found on port $port${NC}"
    fi
}

# Stop all services
echo -e "${BLUE}🛑 Stopping all services...${NC}"

# Stop Node.js services
stop_service_by_port 1575 "Customer Portal"
stop_service_by_port 1574 "Provider Dashboard"
stop_service_by_port 1573 "Admin Dashboard"

# Stop Java backend
stop_service_by_port 8080 "Backend Service"

# Stop Docker containers if they exist
echo -e "\n${BLUE}🐳 Stopping Docker containers...${NC}"

if command -v docker &> /dev/null; then
    # Stop PostgreSQL container
    if docker ps -q -f name=lucknow-healthcare-db | grep -q .; then
        echo -e "${BLUE}🛑 Stopping PostgreSQL container...${NC}"
        docker stop lucknow-healthcare-db
        docker rm lucknow-healthcare-db
        echo -e "${GREEN}✅ PostgreSQL container stopped${NC}"
    fi
    
    # Stop Redis container
    if docker ps -q -f name=lucknow-healthcare-redis | grep -q .; then
        echo -e "${BLUE}🛑 Stopping Redis container...${NC}"
        docker stop lucknow-healthcare-redis
        docker rm lucknow-healthcare-redis
        echo -e "${GREEN}✅ Redis container stopped${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Docker not found. Please stop PostgreSQL and Redis manually if running${NC}"
fi

# Clean up PID files
echo -e "\n${BLUE}🧹 Cleaning up...${NC}"
rm -f *.pid

echo -e "\n${GREEN}✅ All services stopped successfully!${NC}"
echo -e "\n${BLUE}💡 To start services again, run: ./start-services.sh${NC}"
