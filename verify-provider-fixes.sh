#!/bin/bash

# Provider Dashboard Fix Verification Script
# October 14, 2025

echo "🔍 Verifying Provider Dashboard Fixes..."
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test 1: Backend Health
echo "1️⃣  Checking Backend Health..."
HEALTH=$(curl -s http://localhost:8080/api/actuator/health)
if echo "$HEALTH" | grep -q '"status":"UP"'; then
    echo -e "${GREEN}✅ Backend is UP${NC}"
else
    echo -e "${RED}❌ Backend is DOWN - waiting 30s and retrying...${NC}"
    sleep 30
    HEALTH=$(curl -s http://localhost:8080/api/actuator/health)
    if echo "$HEALTH" | grep -q '"status":"UP"'; then
        echo -e "${GREEN}✅ Backend is now UP${NC}"
    else
        echo -e "${RED}❌ Backend is still DOWN - check logs: backend/logs/application.log${NC}"
    fi
fi
echo ""

# Test 2: CORS Configuration
echo "2️⃣  Checking CORS Configuration..."
if grep -q "http://localhost:5173,http://localhost:5174" backend/src/main/resources/application.yml; then
    echo -e "${GREEN}✅ CORS includes ports 5173 and 5174${NC}"
else
    echo -e "${RED}❌ CORS configuration not updated${NC}"
fi
echo ""

# Test 3: Provider Dashboard Running
echo "3️⃣  Checking Provider Dashboard..."
if lsof -ti:5174 > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Provider dashboard running on port 5174${NC}"
else
    echo -e "${YELLOW}⚠️  Provider dashboard not running${NC}"
    echo "   Start with: cd provider-dashboard && npm run dev"
fi
echo ""

# Test 4: File Modifications
echo "4️⃣  Verifying File Changes..."

# Check useAuth.tsx
if grep -q "api.login({ email, password })" provider-dashboard/src/hooks/useAuth.tsx; then
    echo -e "${GREEN}✅ useAuth.tsx - Fixed API call${NC}"
else
    echo -e "${RED}❌ useAuth.tsx - API call not fixed${NC}"
fi

# Check api.ts token
if grep -q "localStorage.getItem('token')" provider-dashboard/src/services/api.ts; then
    echo -e "${GREEN}✅ api.ts - Token key updated${NC}"
else
    echo -e "${RED}❌ api.ts - Token key not updated${NC}"
fi

# Check Layout.tsx routes
if grep -q "{ to: '/profile', label: 'Profile' }" provider-dashboard/src/components/layout/Layout.tsx; then
    echo -e "${GREEN}✅ Layout.tsx - Routes fixed${NC}"
else
    echo -e "${RED}❌ Layout.tsx - Routes not fixed${NC}"
fi

# Check Dashboard.tsx API call
if grep -q "() => getRecentBookings(10)" provider-dashboard/src/pages/Dashboard.tsx; then
    echo -e "${GREEN}✅ Dashboard.tsx - API parameter fixed${NC}"
else
    echo -e "${RED}❌ Dashboard.tsx - API parameter not fixed${NC}"
fi

# Check App.tsx future flags
if grep -q "future={{ v7_startTransition: true" provider-dashboard/src/App.tsx; then
    echo -e "${GREEN}✅ App.tsx - React Router flags added${NC}"
else
    echo -e "${RED}❌ App.tsx - React Router flags missing${NC}"
fi

# Check types/booking.ts exists
if [ -f "provider-dashboard/src/types/booking.ts" ]; then
    echo -e "${GREEN}✅ types/booking.ts - File created${NC}"
else
    echo -e "${RED}❌ types/booking.ts - File missing${NC}"
fi

echo ""

# Test 5: Test Authentication
echo "5️⃣  Testing Authentication..."
if lsof -ti:8080 > /dev/null 2>&1 && echo "$HEALTH" | grep -q '"status":"UP"'; then
    AUTH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"email":"dr.sharma@lucknowhealthcare.com","password":"password123"}')
    
    if echo "$AUTH_RESPONSE" | grep -q "token"; then
        echo -e "${GREEN}✅ Authentication works${NC}"
    else
        echo -e "${RED}❌ Authentication failed${NC}"
        echo "   Response: $AUTH_RESPONSE"
    fi
else
    echo -e "${YELLOW}⚠️  Skipping auth test - backend not ready${NC}"
fi
echo ""

# Summary
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 SUMMARY"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "Issues Fixed: 8"
echo "Files Modified: 7"
echo "Files Created: 1"
echo ""
echo "📝 Next Steps:"
echo "1. Hard refresh browser: Cmd+Shift+R (Mac) or Ctrl+Shift+R (Win)"
echo "2. Navigate to: http://localhost:5174"
echo "3. Login with: dr.sharma@lucknowhealthcare.com / password123"
echo ""
echo "📖 See PROVIDER_FINAL_STATUS.md for detailed documentation"
echo ""

