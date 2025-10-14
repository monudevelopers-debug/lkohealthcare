#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Admin Dashboard Authentication Test${NC}"
echo -e "${BLUE}========================================${NC}\n"

# Configuration
API_URL="http://localhost:8080/api"
ADMIN_EMAIL="admin@lucknowhealthcare.com"
ADMIN_PASSWORD="password123"

# Step 1: Check if backend is running
echo -e "${YELLOW}[1/5] Checking if backend is running...${NC}"
HTTP_CHECK=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$API_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"email":"test","password":"test"}' 2>/dev/null || echo "000")

if [ "$HTTP_CHECK" != "000" ] && [ "$HTTP_CHECK" != "" ]; then
    echo -e "${GREEN}✓ Backend is running (HTTP $HTTP_CHECK)${NC}\n"
else
    echo -e "${RED}✗ Backend is NOT running. Please start the backend first.${NC}"
    echo -e "${YELLOW}Run: cd backend && mvn spring-boot:run${NC}\n"
    exit 1
fi

# Step 2: Test admin login
echo -e "${YELLOW}[2/5] Testing admin login...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$ADMIN_EMAIL\",\"password\":\"$ADMIN_PASSWORD\"}")

echo "Login Response: $LOGIN_RESPONSE"

# Check if login was successful
if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo -e "${GREEN}✓ Admin login successful${NC}\n"
    
    # Extract token
    TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$TOKEN" ]; then
        echo -e "${RED}✗ Failed to extract token${NC}\n"
        exit 1
    fi
    
    echo -e "${BLUE}Token (first 50 chars): ${TOKEN:0:50}...${NC}\n"
else
    echo -e "${RED}✗ Admin login failed${NC}"
    echo -e "${RED}Response: $LOGIN_RESPONSE${NC}\n"
    echo -e "${YELLOW}Possible issues:${NC}"
    echo -e "  1. Admin user not in database"
    echo -e "  2. Incorrect credentials"
    echo -e "  3. Database not initialized with data.sql"
    echo -e "\n${YELLOW}Try running:${NC}"
    echo -e "  cd backend && mvn spring-boot:run${NC}\n"
    exit 1
fi

# Step 3: Test admin stats endpoint
echo -e "${YELLOW}[3/5] Testing /api/admin/stats endpoint...${NC}"
STATS_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$API_URL/admin/stats?period=month" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json")

HTTP_CODE=$(echo "$STATS_RESPONSE" | grep "HTTP_CODE" | cut -d':' -f2)
STATS_BODY=$(echo "$STATS_RESPONSE" | sed '/HTTP_CODE/d')

echo "Stats Response (HTTP $HTTP_CODE):"
echo "$STATS_BODY" | python3 -m json.tool 2>/dev/null || echo "$STATS_BODY"

if [ "$HTTP_CODE" == "200" ]; then
    echo -e "${GREEN}✓ Admin stats endpoint working${NC}\n"
else
    echo -e "${RED}✗ Admin stats endpoint failed with HTTP $HTTP_CODE${NC}\n"
    if [ "$HTTP_CODE" == "401" ]; then
        echo -e "${YELLOW}Issue: Unauthorized - JWT token might not have ADMIN role${NC}\n"
    fi
fi

# Step 4: Test system health endpoint
echo -e "${YELLOW}[4/5] Testing /api/admin/health endpoint...${NC}"
HEALTH_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$API_URL/admin/health" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json")

HTTP_CODE=$(echo "$HEALTH_RESPONSE" | grep "HTTP_CODE" | cut -d':' -f2)
HEALTH_BODY=$(echo "$HEALTH_RESPONSE" | sed '/HTTP_CODE/d')

echo "Health Response (HTTP $HTTP_CODE):"
echo "$HEALTH_BODY" | python3 -m json.tool 2>/dev/null || echo "$HEALTH_BODY"

if [ "$HTTP_CODE" == "200" ]; then
    echo -e "${GREEN}✓ Admin health endpoint working${NC}\n"
else
    echo -e "${RED}✗ Admin health endpoint failed with HTTP $HTTP_CODE${NC}\n"
fi

# Step 5: Test analytics endpoint
echo -e "${YELLOW}[5/5] Testing /api/admin/analytics endpoint...${NC}"
ANALYTICS_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X GET "$API_URL/admin/analytics?period=month" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json")

HTTP_CODE=$(echo "$ANALYTICS_RESPONSE" | grep "HTTP_CODE" | cut -d':' -f2)
ANALYTICS_BODY=$(echo "$ANALYTICS_RESPONSE" | sed '/HTTP_CODE/d')

echo "Analytics Response (HTTP $HTTP_CODE):"
echo "$ANALYTICS_BODY" | python3 -m json.tool 2>/dev/null || echo "$ANALYTICS_BODY"

if [ "$HTTP_CODE" == "200" ]; then
    echo -e "${GREEN}✓ Admin analytics endpoint working${NC}\n"
else
    echo -e "${RED}✗ Admin analytics endpoint failed with HTTP $HTTP_CODE${NC}\n"
fi

# Summary
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}Test Summary${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Your JWT Token:${NC}"
echo -e "${BLUE}$TOKEN${NC}\n"
echo -e "${YELLOW}To use in admin dashboard:${NC}"
echo -e "1. Open browser DevTools (F12)"
echo -e "2. Go to Console tab"
echo -e "3. Run: localStorage.setItem('token', '$TOKEN')"
echo -e "4. Refresh the page\n"
echo -e "${GREEN}Done!${NC}\n"

