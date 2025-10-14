#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

clear

echo -e "${MAGENTA}╔════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║                                                ║${NC}"
echo -e "${MAGENTA}║  🏥  Admin Dashboard Complete Fix Script  🏥   ║${NC}"
echo -e "${MAGENTA}║                                                ║${NC}"
echo -e "${MAGENTA}╚════════════════════════════════════════════════╝${NC}\n"

echo -e "${YELLOW}This script will:${NC}"
echo -e "  1. Kill any existing backend/frontend processes"
echo -e "  2. Start fresh backend on port 8080"
echo -e "  3. Wait for backend to be ready"
echo -e "  4. Test admin login"
echo -e "  5. Give you a working JWT token\n"

read -p "Press ENTER to continue or Ctrl+C to cancel..."
echo ""

# Step 1: Kill existing processes
echo -e "${BLUE}[1/5]${NC} ${YELLOW}Stopping any running processes...${NC}"
lsof -ti:8080 | xargs kill -9 2>/dev/null && echo -e "${GREEN}  ✓ Killed process on port 8080${NC}" || echo -e "${YELLOW}  • No process on port 8080${NC}"
ps aux | grep "spring-boot:run" | grep -v grep | awk '{print $2}' | xargs kill -9 2>/dev/null && echo -e "${GREEN}  ✓ Killed Spring Boot processes${NC}" || echo -e "${YELLOW}  • No Spring Boot processes${NC}"
lsof -ti:5173 | xargs kill -9 2>/dev/null && echo -e "${GREEN}  ✓ Killed process on port 5173${NC}" || echo -e "${YELLOW}  • No process on port 5173${NC}"
echo ""

# Step 2: Start backend
echo -e "${BLUE}[2/5]${NC} ${YELLOW}Starting backend...${NC}"
cd backend
mvn spring-boot:run > /tmp/backend-fix.log 2>&1 &
BACKEND_PID=$!
echo -e "${GREEN}  ✓ Backend starting (PID: $BACKEND_PID)${NC}"
cd ..
echo ""

# Step 3: Wait for backend
echo -e "${BLUE}[3/5]${NC} ${YELLOW}Waiting for backend to be ready...${NC}"
echo -e "${YELLOW}  This may take 30-40 seconds...${NC}\n"

for i in {1..40}; do
    if curl -s -o /dev/null -w "%{http_code}" -X POST "http://localhost:8080/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"email":"test","password":"test"}' 2>/dev/null | grep -q "4[0-9][0-9]\|2[0-9][0-9]"; then
        echo -e "${GREEN}  ✓ Backend is ready!${NC}\n"
        break
    fi
    echo -ne "${YELLOW}  ⏳ Waiting... (${i}/40)${NC}\r"
    sleep 1
done

# Step 4: Test admin login
echo -e "${BLUE}[4/5]${NC} ${YELLOW}Testing admin login...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@lucknowhealthcare.com","password":"password123"}')

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo -e "${GREEN}  ✓ Admin login successful!${NC}\n"
    
    # Extract token
    TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    # Test admin endpoint
    echo -e "${BLUE}[5/5]${NC} ${YELLOW}Testing admin endpoints...${NC}"
    STATS_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "http://localhost:8080/api/admin/stats?period=month" \
        -H "Authorization: Bearer $TOKEN")
    
    if [ "$STATS_CODE" == "200" ]; then
        echo -e "${GREEN}  ✓ Admin endpoints working!${NC}\n"
        
        # Success message
        echo -e "${MAGENTA}╔════════════════════════════════════════════════╗${NC}"
        echo -e "${MAGENTA}║                                                ║${NC}"
        echo -e "${MAGENTA}║            ${GREEN}✓ BACKEND IS READY! ✓${MAGENTA}              ║${NC}"
        echo -e "${MAGENTA}║                                                ║${NC}"
        echo -e "${MAGENTA}╚════════════════════════════════════════════════╝${NC}\n"
        
        echo -e "${BLUE}╔════════════════════════════════════════════════╗${NC}"
        echo -e "${BLUE}║  📋 WHAT TO DO NEXT                            ║${NC}"
        echo -e "${BLUE}╚════════════════════════════════════════════════╝${NC}\n"
        
        echo -e "${YELLOW}1. Start the admin dashboard:${NC}"
        echo -e "   ${GREEN}cd admin-dashboard && npm run dev${NC}\n"
        
        echo -e "${YELLOW}2. Open browser to:${NC}"
        echo -e "   ${GREEN}http://localhost:5173${NC}\n"
        
        echo -e "${YELLOW}3. Login with:${NC}"
        echo -e "   Email:    ${GREEN}admin@lucknowhealthcare.com${NC}"
        echo -e "   Password: ${GREEN}password123${NC}\n"
        
        echo -e "${YELLOW}4. If you see 401 errors:${NC}"
        echo -e "   ${BLUE}a)${NC} Open DevTools (F12)"
        echo -e "   ${BLUE}b)${NC} Go to Console tab"
        echo -e "   ${BLUE}c)${NC} Run: ${GREEN}localStorage.clear()${NC}"
        echo -e "   ${BLUE}d)${NC} Refresh page"
        echo -e "   ${BLUE}e)${NC} Login again\n"
        
        echo -e "${BLUE}╔════════════════════════════════════════════════╗${NC}"
        echo -e "${BLUE}║  🔑 YOUR JWT TOKEN (for manual testing)       ║${NC}"
        echo -e "${BLUE}╚════════════════════════════════════════════════╝${NC}\n"
        echo -e "${GREEN}${TOKEN}${NC}\n"
        
        echo -e "${YELLOW}To use this token in browser console:${NC}"
        echo -e "${GREEN}localStorage.setItem('token', '${TOKEN}')${NC}\n"
        
        echo -e "${MAGENTA}════════════════════════════════════════════════${NC}\n"
        echo -e "${GREEN}✅ Setup complete! Backend is running on port 8080${NC}\n"
        
    else
        echo -e "${RED}  ✗ Admin endpoint returned HTTP $STATS_CODE${NC}"
        echo -e "${YELLOW}  Check /tmp/backend-fix.log for errors${NC}\n"
    fi
else
    echo -e "${RED}  ✗ Admin login failed${NC}"
    echo -e "${RED}  Response: $LOGIN_RESPONSE${NC}\n"
    echo -e "${YELLOW}  Check /tmp/backend-fix.log for errors${NC}\n"
fi

echo -e "${YELLOW}Backend logs are at: ${BLUE}/tmp/backend-fix.log${NC}"
echo -e "${YELLOW}To view logs: ${BLUE}tail -f /tmp/backend-fix.log${NC}\n"

