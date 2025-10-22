#!/bin/bash

# OTP Test Script for Lucknow Healthcare Services
# This script demonstrates the OTP functionality in development mode

echo "🧪 Testing OTP Functionality"
echo "============================"

# Test phone number
PHONE="+919876543210"

echo "📱 Testing OTP for phone: $PHONE"
echo ""

# Send OTP
echo "1️⃣ Sending OTP..."
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/send-otp \
  -H "Content-Type: application/json" \
  -d "{\"phone\": \"$PHONE\"}")

echo "Response: $RESPONSE"
echo ""

# Check if OTP was sent successfully
if echo "$RESPONSE" | grep -q "OTP sent successfully"; then
    echo "✅ OTP sent successfully!"
    echo ""
    echo "🔧 DEVELOPMENT MODE:"
    echo "   Check the backend console for the OTP code"
    echo "   Look for: '🔧 DEVELOPMENT MODE - OTP for $PHONE: XXXXXX'"
    echo ""
    echo "📝 To test verification, use the OTP code from the backend console"
    echo "   Example: curl -X POST http://localhost:8080/api/auth/verify-otp \\"
    echo "            -H 'Content-Type: application/json' \\"
    echo "            -d '{\"phone\": \"$PHONE\", \"otp\": \"123456\"}'"
else
    echo "❌ Failed to send OTP"
    echo "Response: $RESPONSE"
fi

echo ""
echo "🔧 Backend Console Instructions:"
echo "   1. Look at the backend terminal/console"
echo "   2. Find the line: '🔧 DEVELOPMENT MODE - OTP for $PHONE: XXXXXX'"
echo "   3. Copy the 6-digit OTP code"
echo "   4. Use it in the customer portal or test verification API"
