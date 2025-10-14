#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Starting Admin Dashboard${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Check if backend is running
echo -e "${YELLOW}Checking backend status...${NC}"
if curl -s -o /dev/null -w "%{http_code}" -X POST "http://localhost:8080/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"email":"test","password":"test"}' 2>/dev/null | grep -q "4[0-9][0-9]\|2[0-9][0-9]"; then
    echo -e "${GREEN}✓ Backend is running${NC}\n"
else
    echo -e "${RED}✗ Backend is NOT running${NC}"
    echo -e "${YELLOW}Starting backend in background...${NC}\n"
    
    cd backend
    mvn spring-boot:run > ../logs/backend.log 2>&1 &
    BACKEND_PID=$!
    echo -e "${BLUE}Backend starting... (PID: $BACKEND_PID)${NC}"
    echo -e "${YELLOW}Waiting 30 seconds for backend to start...${NC}\n"
    sleep 30
    cd ..
fi

# Start admin dashboard
echo -e "${YELLOW}Starting admin dashboard...${NC}"
cd admin-dashboard

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo -e "${YELLOW}Installing dependencies...${NC}"
    npm install
fi

echo -e "${GREEN}✓ Starting development server...${NC}\n"
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Admin Dashboard Info${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}URL:${NC} http://localhost:5173"
echo -e "${GREEN}Login:${NC} admin@lucknowhealthcare.com"
echo -e "${GREEN}Password:${NC} password123"
echo -e "${BLUE}========================================${NC}\n"

npm run dev

