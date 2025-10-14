#!/bin/bash

# Provider Dashboard Comprehensive Testing Script
# Tests all backend APIs and verifies UI is running

echo "========================================"
echo "PROVIDER DASHBOARD TESTING SUITE"
echo "========================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counter
PASSED=0
FAILED=0

test_endpoint() {
    local name=$1
    local url=$2
    local method=${3:-GET}
    local data=$4
    local auth_required=${5:-true}
    
    echo -n "Testing $name... "
    
    if [ "$auth_required" = "true" ]; then
        if [ -z "$TOKEN" ]; then
            echo -e "${RED}FAILED${NC} (No token)"
            ((FAILED++))
            return
        fi
        
        if [ "$method" = "GET" ]; then
            response=$(curl -s -w "\n%{http_code}" -X $method "$url" -H "Authorization: Bearer $TOKEN")
        else
            response=$(curl -s -w "\n%{http_code}" -X $method "$url" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "$data")
        fi
    else
        if [ "$method" = "POST" ]; then
            response=$(curl -s -w "\n%{http_code}" -X $method "$url" -H "Content-Type: application/json" -d "$data")
        else
            response=$(curl -s -w "\n%{http_code}" -X $method "$url")
        fi
    fi
    
    status_code=$(echo "$response" | tail -n 1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$status_code" = "200" ] || [ "$status_code" = "201" ]; then
        echo -e "${GREEN}PASSED${NC} (HTTP $status_code)"
        ((PASSED++))
    else
        echo -e "${RED}FAILED${NC} (HTTP $status_code)"
        ((FAILED++))
    fi
}

# Base URL
BASE_URL="http://localhost:8080/api"
UI_URL="http://localhost:5174"

echo "=== Phase 1: Service Availability ==="
echo ""

# Check if backend is running
echo -n "Checking backend (port 8080)... "
if curl -s http://localhost:8080/api >/dev/null 2>&1; then
    echo -e "${GREEN}RUNNING${NC}"
else
    echo -e "${RED}NOT RUNNING${NC}"
    echo "Please start the backend: cd backend && mvn spring-boot:run"
    exit 1
fi

# Check if provider dashboard is running
echo -n "Checking provider dashboard (port 5174)... "
if curl -s http://localhost:5174 >/dev/null 2>&1; then
    echo -e "${GREEN}RUNNING${NC}"
else
    echo -e "${RED}NOT RUNNING${NC}"
    echo "Please start provider dashboard: cd provider-dashboard && npm run dev"
    exit 1
fi

# Check if admin dashboard is running
echo -n "Checking admin dashboard (port 5173)... "
if curl -s http://localhost:5173 >/dev/null 2>&1; then
    echo -e "${GREEN}RUNNING${NC}"
else
    echo -e "${YELLOW}NOT RUNNING${NC}"
fi

echo ""
echo "=== Phase 2: Authentication ==="
echo ""

# Login as provider
echo -n "Logging in as provider... "
login_response=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dr.sharma@lucknowhealthcare.com",
    "password": "password123"
  }')

TOKEN=$(echo $login_response | jq -r '.token')

if [ "$TOKEN" != "null" ] && [ ! -z "$TOKEN" ]; then
    echo -e "${GREEN}SUCCESS${NC}"
    echo "Token: ${TOKEN:0:50}..."
    ((PASSED++))
else
    echo -e "${RED}FAILED${NC}"
    echo "Response: $login_response"
    ((FAILED++))
    exit 1
fi

test_endpoint "GET /auth/me" "$BASE_URL/auth/me" "GET" "" "true"

echo ""
echo "=== Phase 3: Provider Profile APIs ==="
echo ""

test_endpoint "GET /providers/profile" "$BASE_URL/providers/profile" "GET" "" "true"
test_endpoint "GET /providers/stats" "$BASE_URL/providers/stats?period=month" "GET" "" "true"

echo ""
echo "=== Phase 4: Booking Management APIs ==="
echo ""

test_endpoint "GET /providers/bookings" "$BASE_URL/providers/bookings?page=0&size=10" "GET" "" "true"
test_endpoint "GET /providers/schedule" "$BASE_URL/providers/schedule?date=2025-10-16" "GET" "" "true"

echo ""
echo "=== Phase 5: Analytics & Stats APIs ==="
echo ""

test_endpoint "GET /providers/earnings" "$BASE_URL/providers/earnings?period=month" "GET" "" "true"

echo ""
echo "=== Phase 6: Reviews APIs ==="
echo ""

test_endpoint "GET /providers/reviews" "$BASE_URL/providers/reviews?page=0&size=10" "GET" "" "true"

echo ""
echo "=== Phase 7: UI Accessibility ==="
echo ""

# Test UI pages
echo -n "Testing Dashboard page... "
if curl -s "$UI_URL/" | grep -q "html"; then
    echo -e "${GREEN}ACCESSIBLE${NC}"
    ((PASSED++))
else
    echo -e "${RED}NOT ACCESSIBLE${NC}"
    ((FAILED++))
fi

echo -n "Testing Bookings page... "
if curl -s "$UI_URL/bookings" | grep -q "html"; then
    echo -e "${GREEN}ACCESSIBLE${NC}"
    ((PASSED++))
else
    echo -e "${RED}NOT ACCESSIBLE${NC}"
    ((FAILED++))
fi

echo -n "Testing Profile page... "
if curl -s "$UI_URL/profile" | grep -q "html"; then
    echo -e "${GREEN}ACCESSIBLE${NC}"
    ((PASSED++))
else
    echo -e "${RED}NOT ACCESSIBLE${NC}"
    ((FAILED++))
fi

echo -n "Testing Reviews page... "
if curl -s "$UI_URL/reviews" | grep -q "html"; then
    echo -e "${GREEN}ACCESSIBLE${NC}"
    ((PASSED++))
else
    echo -e "${RED}NOT ACCESSIBLE${NC}"
    ((FAILED++))
fi

echo -n "Testing Analytics page... "
if curl -s "$UI_URL/analytics" | grep -q "html"; then
    echo -e "${GREEN}ACCESSIBLE${NC}"
    ((PASSED++))
else
    echo -e "${RED}NOT ACCESSIBLE${NC}"
    ((FAILED++))
fi

echo ""
echo "========================================"
echo "TESTING SUMMARY"
echo "========================================"
echo ""
echo -e "Total Tests: $((PASSED + FAILED))"
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✅ ALL TESTS PASSED!${NC}"
    echo ""
    echo "🎉 Provider Dashboard is ready!"
    echo ""
    echo "Access URLs:"
    echo "  • Provider Dashboard: http://localhost:5174"
    echo "  • Admin Dashboard: http://localhost:5173"
    echo "  • Backend API: http://localhost:8080/api"
    echo ""
    echo "Login Credentials:"
    echo "  • Email: dr.sharma@lucknowhealthcare.com"
    echo "  • Password: password123"
    exit 0
else
    echo -e "${RED}❌ SOME TESTS FAILED${NC}"
    echo "Please review the failures above."
    exit 1
fi

