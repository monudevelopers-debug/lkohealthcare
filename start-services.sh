#!/bin/bash

# Healthcare Services Startup Script
# This script starts all services for the Lucknow Healthcare platform

set -e

echo "🏥 Starting Lucknow Healthcare Services..."
echo "=========================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if port is in use
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo -e "${YELLOW}⚠️  Port $port is already in use${NC}"
        return 1
    else
        return 0
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${BLUE}⏳ Waiting for $service_name to be ready...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✅ $service_name is ready!${NC}"
            return 0
        fi
        echo -e "${YELLOW}   Attempt $attempt/$max_attempts - waiting...${NC}"
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}❌ $service_name failed to start within expected time${NC}"
    return 1
}

# Function to start service in background
start_service() {
    local service_name=$1
    local command=$2
    local port=$3
    local log_file=$4
    
    echo -e "${BLUE}🚀 Starting $service_name on port $port...${NC}"
    
    if check_port $port; then
        nohup $command > "$log_file" 2>&1 &
        local pid=$!
        echo $pid > "${service_name}.pid"
        echo -e "${GREEN}✅ $service_name started with PID $pid${NC}"
    else
        echo -e "${RED}❌ Failed to start $service_name - port $port is in use${NC}"
        return 1
    fi
}

# Create logs directory
mkdir -p logs

echo -e "${BLUE}📁 Setting up environment...${NC}"

# 1. Start PostgreSQL Database (port 5432)
echo -e "\n${BLUE}🗄️  Starting PostgreSQL Database...${NC}"
if check_port 5432; then
    # Start PostgreSQL using Docker if available, otherwise use local installation
    if command -v docker &> /dev/null; then
        echo -e "${BLUE}🐳 Starting PostgreSQL with Docker...${NC}"
        docker run -d \
            --name lucknow-healthcare-db \
            -e POSTGRES_DB=lucknow_healthcare \
            -e POSTGRES_USER=healthcare_user \
            -e POSTGRES_PASSWORD=healthcare_password \
            -p 5432:5432 \
            -v postgres_data:/var/lib/postgresql/data \
            postgres:15-alpine
    else
        echo -e "${YELLOW}⚠️  Docker not found. Please ensure PostgreSQL is running on port 5432${NC}"
    fi
else
    echo -e "${GREEN}✅ PostgreSQL already running on port 5432${NC}"
fi

# 2. Start Redis (port 6379)
echo -e "\n${BLUE}🔴 Starting Redis Cache...${NC}"
if check_port 6379; then
    if command -v docker &> /dev/null; then
        echo -e "${BLUE}🐳 Starting Redis with Docker...${NC}"
        docker run -d \
            --name lucknow-healthcare-redis \
            -p 6379:6379 \
            redis:7-alpine
    else
        echo -e "${YELLOW}⚠️  Docker not found. Please ensure Redis is running on port 6379${NC}"
    fi
else
    echo -e "${GREEN}✅ Redis already running on port 6379${NC}"
fi

# 3. Start Spring Boot Backend (port 8080)
echo -e "\n${BLUE}☕ Starting Spring Boot Backend...${NC}"
cd backend
if [ -f "target/healthcare-1.0.0.jar" ]; then
    start_service "backend" "java -jar target/healthcare-1.0.0.jar" 8080 "../logs/backend.log"
    wait_for_service "http://localhost:8080/actuator/health" "Backend Service"
else
    echo -e "${YELLOW}⚠️  Backend JAR not found. Building backend first...${NC}"
    if command -v mvn &> /dev/null; then
        mvn clean package -DskipTests
        start_service "backend" "java -jar target/healthcare-1.0.0.jar" 8080 "../logs/backend.log"
        wait_for_service "http://localhost:8080/actuator/health" "Backend Service"
    else
        echo -e "${RED}❌ Maven not found. Please build the backend manually${NC}"
    fi
fi
cd ..

# 4. Start Provider Dashboard (port 5174)
echo -e "\n${BLUE}👨‍⚕️  Starting Provider Dashboard...${NC}"
cd provider-dashboard
if [ -d "node_modules" ]; then
    start_service "provider-dashboard" "npm run dev -- --port 5174" 5174 "../logs/provider-dashboard.log"
    wait_for_service "http://localhost:5174" "Provider Dashboard"
else
    echo -e "${YELLOW}⚠️  Node modules not found. Installing dependencies...${NC}"
    npm install
    start_service "provider-dashboard" "npm run dev -- --port 5174" 5174 "../logs/provider-dashboard.log"
    wait_for_service "http://localhost:5174" "Provider Dashboard"
fi
cd ..

# 5. Start Admin Dashboard (port 1573)
echo -e "\n${BLUE}👨‍💼 Starting Admin Dashboard...${NC}"
cd admin-dashboard
if [ -d "node_modules" ]; then
    start_service "admin-dashboard" "npm run dev -- --port 1573" 1573 "../logs/admin-dashboard.log"
    wait_for_service "http://localhost:1573" "Admin Dashboard"
else
    echo -e "${YELLOW}⚠️  Node modules not found. Installing dependencies...${NC}"
    npm install
    start_service "admin-dashboard" "npm run dev -- --port 1573" 1573 "../logs/admin-dashboard.log"
    wait_for_service "http://localhost:1573" "Admin Dashboard"
fi
cd ..

# 6. Start Customer Portal (port 1575)
echo -e "\n${BLUE}👤 Starting Customer Portal...${NC}"
cd customer-portal
if [ -d "node_modules" ]; then
    start_service "customer-portal" "npm run dev -- --port 1575" 1575 "../logs/customer-portal.log"
    wait_for_service "http://localhost:1575" "Customer Portal"
else
    echo -e "${YELLOW}⚠️  Node modules not found. Installing dependencies...${NC}"
    npm install
    start_service "customer-portal" "npm run dev -- --port 1575" 1575 "../logs/customer-portal.log"
    wait_for_service "http://localhost:1575" "Customer Portal"
fi
cd ..

echo -e "\n${GREEN}🎉 All services started successfully!${NC}"
echo -e "\n${BLUE}📊 Service Status:${NC}"
echo -e "${GREEN}✅ PostgreSQL Database: http://localhost:5432${NC}"
echo -e "${GREEN}✅ Redis Cache: http://localhost:6379${NC}"
echo -e "${GREEN}✅ Backend API: http://localhost:8080${NC}"
echo -e "${GREEN}✅ Provider Dashboard: http://localhost:5174${NC}"
echo -e "${GREEN}✅ Admin Dashboard: http://localhost:1573${NC}"
echo -e "${GREEN}✅ Customer Portal: http://localhost:1575${NC}"

echo -e "\n${BLUE}📝 Logs are available in the logs/ directory${NC}"
echo -e "${BLUE}🛑 To stop all services, run: ./stop-services.sh${NC}"

echo -e "\n${YELLOW}💡 Tips:${NC}"
echo -e "   • Check logs with: tail -f logs/[service-name].log"
echo -e "   • Monitor services with: ps aux | grep java"
echo -e "   • Test backend health: curl http://localhost:8080/actuator/health"
