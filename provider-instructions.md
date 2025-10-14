# Lucknow Healthcare Platform - Provider Dashboard Testing & Integration Guide

## Project Overview

Provider-focused dashboard for healthcare service providers to manage bookings, profiles, reviews, and earnings on the Lucknow Healthcare Platform.

**Last Updated**: October 14, 2025
**Version**: 1.0.0 - Provider Dashboard Edition

---

## Table of Contents
1. [Environment Setup](#environment-setup)
2. [Provider API Endpoints](#provider-api-endpoints)
3. [Testing Progress by Feature](#testing-progress-by-feature)
4. [API Testing Examples](#api-testing-examples)
5. [Frontend Integration](#frontend-integration)
6. [UI Feature Testing](#ui-feature-testing)
7. [Troubleshooting](#troubleshooting)

---

## Environment Setup

### Prerequisites
- Java 17+
- PostgreSQL 16+
- Node.js 18+
- Maven 3.6+

### Quick Start

#### 1. Database Setup
```bash
# Start PostgreSQL
brew services start postgresql@16  # macOS
# OR
sudo systemctl start postgresql    # Linux

# Database is shared with admin dashboard
# Already created: lucknow_healthcare
```

#### 2. Start Backend
```bash
cd backend
mvn spring-boot:run -Dmaven.test.skip=true
```
Backend runs at: `http://localhost:8080/api`

#### 3. Start Provider Dashboard
```bash
cd provider-dashboard
npm install
npm run dev
```
Dashboard runs at: `http://localhost:5174`

---

## Default Login Credentials

| Role | Email | Password |
|------|-------|----------|
| Provider | dr.sharma@lucknowhealthcare.com | password123 |
| Provider | dr.patel@lucknowhealthcare.com | password123 |
| Provider | dr.singh@lucknowhealthcare.com | password123 |
| Provider | dr.gupta@lucknowhealthcare.com | password123 |

---

## Provider API Endpoints

### Base URL
```
http://localhost:8080/api
```

---

## Phase 1: Authentication APIs ✅ COMPLETED

Provider authentication uses the same endpoints as admin dashboard.

### POST /auth/login
**Description**: Authenticate provider and get JWT token

**Request**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dr.sharma@lucknowhealthcare.com",
    "password": "password123"
  }'
```

**Response**:
```json
{
  "token": "eyJhbGci...",
  "expiresIn": 900000,
  "message": "Login successful",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440102",
    "name": "Dr. Rajesh Sharma",
    "email": "dr.sharma@lucknowhealthcare.com",
    "role": "PROVIDER",
    "phone": "+91-9876543211",
    "status": "ACTIVE",
    "emailVerified": true,
    "createdAt": "2025-10-14T22:08:51.484309",
    "updatedAt": "2025-10-14T22:08:51.484309"
  }
}
```

**Notes**:
- ✅ Token expires in 15 minutes (900000ms)
- ✅ Use this token for all subsequent API calls
- ✅ Password field is hidden from response

### GET /auth/me
**Description**: Get current authenticated provider's user profile

**Request**:
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response**: User object for the provider

---

## Phase 2: Provider Profile Management APIs ✅ COMPLETED

### GET /providers/profile
**Description**: Get current provider's complete profile information

**Request**:
```bash
curl -X GET http://localhost:8080/api/providers/profile \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Response**:
```json
{
  "provider": {
    "id": "550e8400-e29b-41d4-a716-446655440201",
    "name": "Dr. Rajesh Sharma",
    "email": "dr.sharma@lucknowhealthcare.com",
    "phone": "+91-9876543211",
    "qualification": "MD, DM Cardiology",
    "experience": 15,
    "availabilityStatus": "AVAILABLE",
    "rating": 4.8,
    "totalRatings": 150,
    "isVerified": true,
    "documents": null,
    "createdAt": "2025-10-14T23:55:45.346792",
    "updatedAt": "2025-10-14T23:55:45.346792",
    "averageRating": 4.8
  },
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440102",
    "name": "Dr. Rajesh Sharma",
    "email": "dr.sharma@lucknowhealthcare.com",
    "role": "PROVIDER",
    "status": "ACTIVE",
    "emailVerified": true
  }
}
```

**Notes**:
- ✅ Returns both provider and user information
- ✅ Automatically fetches profile for logged-in provider
- ✅ Includes rating and verification status

### PUT /providers/profile
**Description**: Update current provider's profile information

**Request**:
```bash
curl -X PUT http://localhost:8080/api/providers/profile \
  -H "Authorization: Bearer PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Rajesh Sharma",
    "phone": "+91-9876543211",
    "qualification": "MD, DM Cardiology, FACC",
    "experience": 16
  }'
```

**Response**: Updated provider object

**Notes**:
- ✅ Updates provider information
- ✅ Cannot change email or rating directly
- ✅ Experience and qualifications can be updated

### PATCH /providers/availability
**Description**: Update current provider's availability status

**Request**:
```bash
curl -X PATCH http://localhost:8080/api/providers/availability \
  -H "Authorization: Bearer PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"isAvailable": true}'
```

**Response**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440201",
  "name": "Dr. Rajesh Sharma",
  "email": "dr.sharma@lucknowhealthcare.com",
  "availabilityStatus": "AVAILABLE",
  "rating": 4.8,
  "experience": 15,
  "qualification": "MD, DM Cardiology"
}
```

**Notes**:
- ✅ Toggle between AVAILABLE and OFF_DUTY
- ✅ `isAvailable: true` → AVAILABLE
- ✅ `isAvailable: false` → OFF_DUTY
- ✅ Instant status update

---

## Phase 3: Booking Management APIs ✅ COMPLETED

### GET /providers/bookings
**Description**: Get current provider's bookings (paginated)

**Request**:
```bash
curl -X GET "http://localhost:8080/api/providers/bookings?page=0&size=20" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Query Parameters**:
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page
- `status` (optional) - Filter by status: PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED

**Response**:
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440401",
      "user": {
        "id": "550e8400-e29b-41d4-a716-446655440106",
        "name": "Rahul Kumar",
        "phone": "+91-9876543215"
      },
      "service": {
        "id": "129fcae3-8cb9-4493-af40-89f808eda631",
        "name": "Basic Nursing Care"
      },
      "scheduledDate": "2025-10-16",
      "scheduledTime": "10:00:00",
      "status": "PENDING",
      "totalAmount": 800.00,
      "notes": "Heart checkup requested",
      "createdAt": "2025-10-14T23:57:12.234567"
    }
  ],
  "totalElements": 2,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

**Notes**:
- ✅ Paginated response
- ✅ Includes user and service details
- ✅ Can filter by status

### GET /providers/recent-bookings
**Description**: Get provider's most recent bookings

**Request**:
```bash
curl -X GET "http://localhost:8080/api/providers/recent-bookings?limit=10" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Query Parameters**:
- `limit` (default: 10) - Number of recent bookings

**Response**: Array of recent bookings

**Notes**:
- ✅ Sorted by creation date (newest first)
- ✅ Useful for dashboard "Recent Activity" section
- ✅ Not paginated, uses simple limit

### GET /providers/schedule
**Description**: Get provider's schedule for a specific date

**Request**:
```bash
curl -X GET "http://localhost:8080/api/providers/schedule?date=2025-10-16" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Query Parameters**:
- `date` (required) - Date in format: yyyy-MM-dd

**Response**: Array of bookings for the specified date

**Notes**:
- ✅ Shows all bookings for a single day
- ✅ Useful for daily schedule view
- ✅ Includes all booking statuses

### POST /bookings/{id}/accept
**Description**: Accept a pending booking

**Request**:
```bash
curl -X POST http://localhost:8080/api/bookings/{BOOKING_ID}/accept \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Response**: Updated booking with status CONFIRMED

**Notes**:
- ✅ Only works on PENDING bookings
- ✅ Changes status to CONFIRMED
- ✅ Returns error if booking not in PENDING status

### POST /bookings/{id}/reject
**Description**: Reject a pending booking

**Request**:
```bash
curl -X POST http://localhost:8080/api/bookings/{BOOKING_ID}/reject \
  -H "Authorization: Bearer PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"reason": "Not available at this time"}'
```

**Response**: Updated booking with status CANCELLED

**Notes**:
- ✅ Only works on PENDING bookings
- ✅ Changes status to CANCELLED
- ✅ Optional reason parameter
- ✅ Reason is added to booking notes

### POST /bookings/{id}/start
**Description**: Start service delivery

**Request**:
```bash
curl -X POST http://localhost:8080/api/bookings/{BOOKING_ID}/start \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Response**: Updated booking with status IN_PROGRESS

**Notes**:
- ✅ Only works on CONFIRMED bookings
- ✅ Changes status to IN_PROGRESS
- ✅ Indicates service has started

### POST /bookings/{id}/complete
**Description**: Complete service delivery

**Request**:
```bash
curl -X POST http://localhost:8080/api/bookings/{BOOKING_ID}/complete \
  -H "Authorization: Bearer PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"notes": "Service completed successfully"}'
```

**Response**: Updated booking with status COMPLETED

**Notes**:
- ✅ Only works on IN_PROGRESS bookings
- ✅ Changes status to COMPLETED
- ✅ Optional completion notes
- ✅ Notes are added to booking records

---

## Phase 4: Analytics & Statistics APIs ✅ COMPLETED

### GET /providers/stats
**Description**: Get provider statistics for dashboard

**Request**:
```bash
curl -X GET "http://localhost:8080/api/providers/stats?period=month" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Query Parameters**:
- `period` (default: month) - Time period: today, week, month

**Response**:
```json
{
  "todayBookings": 5,
  "activeBookings": 3,
  "totalEarnings": 15000.0,
  "rating": 4.8,
  "todayBookingsChange": 20.0,
  "activeBookingsChange": 10.0,
  "earningsChange": 15.0,
  "ratingChange": 0.5
}
```

**Notes**:
- ✅ Provides key metrics for dashboard
- ✅ Includes percentage changes
- ✅ Real-time statistics
- ⚠️ Currently returns mock data for *Change fields (needs implementation)

### GET /providers/earnings
**Description**: Get earnings data for a time period

**Request**:
```bash
curl -X GET "http://localhost:8080/api/providers/earnings?period=month" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Query Parameters**:
- `period` (default: month) - Time period: today, week, month, year

**Response**:
```json
{
  "total": 15000.0,
  "breakdown": [
    {
      "id": "booking-id",
      "totalAmount": 800.00,
      "status": "COMPLETED",
      "updatedAt": "2025-10-14T10:00:00"
    }
  ]
}
```

**Notes**:
- ✅ Shows total earnings
- ✅ Includes breakdown by completed bookings
- ✅ Only counts COMPLETED bookings
- ✅ Filtered by time period

### GET /providers/payments
**Description**: Get payment history (paginated)

**Request**:
```bash
curl -X GET "http://localhost:8080/api/providers/payments?page=0&size=20" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Query Parameters**:
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response**:
```json
{
  "content": [
    /* Array of completed bookings simulating payments */
  ],
  "totalElements": 10,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

**Notes**:
- ✅ Paginated payment history
- ✅ Based on completed bookings
- ⚠️ Currently simulated (full payment system pending)

---

## Phase 5: Reviews & Ratings APIs ✅ COMPLETED

### GET /providers/reviews
**Description**: Get reviews for current provider (paginated)

**Request**:
```bash
curl -X GET "http://localhost:8080/api/providers/reviews?page=0&size=20" \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Query Parameters**:
- `page` (default: 0) - Page number
- `size` (default: 20) - Items per page

**Response**:
```json
{
  "content": [
    {
      "id": "review-id",
      "user": {
        "name": "Customer Name"
      },
      "rating": 5,
      "comment": "Excellent service!",
      "createdAt": "2025-10-14T15:30:00"
    }
  ],
  "totalElements": 15,
  "totalPages": 1,
  "number": 0,
  "size": 20
}
```

**Notes**:
- ✅ Shows all reviews for provider
- ✅ Includes user information
- ✅ Paginated for performance

### GET /reviews/provider/{providerId}/average-rating
**Description**: Get average rating for a provider

**Request**:
```bash
curl -X GET http://localhost:8080/api/reviews/provider/{PROVIDER_ID}/average-rating \
  -H "Authorization: Bearer PROVIDER_TOKEN"
```

**Response**:
```json
{
  "providerId": "550e8400-e29b-41d4-a716-446655440201",
  "averageRating": 4.8,
  "totalReviews": 150
}
```

**Notes**:
- ✅ Calculates average from all reviews
- ✅ Includes total review count
- ✅ Available from admin instructions.md

---

## Testing Progress by Feature

### ✅ Phase 0: Pre-flight Checks - COMPLETED
- ✅ PostgreSQL database running
- ✅ Backend starts successfully
- ✅ Provider data loaded
- ✅ CORS configured correctly
- ✅ Provider authentication working

### ✅ Phase 1: Authentication - COMPLETED
- ✅ POST /auth/login (provider credentials)
- ✅ GET /auth/me
- ✅ JWT token generation (15-min expiry)
- ✅ Password field hidden

### ✅ Phase 2: Profile Management - COMPLETED
- ✅ GET /providers/profile
- ✅ PUT /providers/profile
- ✅ PATCH /providers/availability
- ✅ Profile returns provider + user data

### ✅ Phase 3: Booking Management - COMPLETED
- ✅ GET /providers/bookings (paginated)
- ✅ GET /providers/recent-bookings
- ✅ GET /providers/schedule
- ✅ POST /bookings/{id}/accept
- ✅ POST /bookings/{id}/reject
- ✅ POST /bookings/{id}/start
- ✅ POST /bookings/{id}/complete

### ✅ Phase 4: Analytics & Stats - COMPLETED
- ✅ GET /providers/stats
- ✅ GET /providers/earnings
- ✅ GET /providers/payments

### ✅ Phase 5: Reviews - COMPLETED
- ✅ GET /providers/reviews
- ✅ GET /reviews/provider/{id}/average-rating

---

## API Testing Examples

### Example 1: Complete Provider Login Flow

```bash
# 1. Login as provider
PROVIDER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "dr.sharma@lucknowhealthcare.com", "password": "password123"}' \
  | jq -r '.token')

echo "Token: $PROVIDER_TOKEN"

# 2. Get provider profile
curl -X GET http://localhost:8080/api/providers/profile \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  | jq

# 3. Get provider stats
curl -X GET "http://localhost:8080/api/providers/stats?period=month" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  | jq
```

### Example 2: Managing Bookings

```bash
# Get provider token
PROVIDER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "dr.sharma@lucknowhealthcare.com", "password": "password123"}' \
  | jq -r '.token')

# 1. Get all bookings
curl -X GET "http://localhost:8080/api/providers/bookings?page=0&size=10" \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  | jq

# 2. Accept a pending booking
curl -X POST http://localhost:8080/api/bookings/BOOKING_ID/accept \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  | jq

# 3. Start the confirmed booking
curl -X POST http://localhost:8080/api/bookings/BOOKING_ID/start \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  | jq

# 4. Complete the in-progress booking
curl -X POST http://localhost:8080/api/bookings/BOOKING_ID/complete \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"notes": "Service completed successfully"}' \
  | jq
```

### Example 3: Update Availability

```bash
# Get provider token
PROVIDER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "dr.sharma@lucknowhealthcare.com", "password": "password123"}' \
  | jq -r '.token')

# Set available
curl -X PATCH http://localhost:8080/api/providers/availability \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"isAvailable": true}' \
  | jq

# Set unavailable
curl -X PATCH http://localhost:8080/api/providers/availability \
  -H "Authorization: Bearer $PROVIDER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"isAvailable": false}' \
  | jq
```

---

## Frontend Integration

### Provider Dashboard (`provider-dashboard/src/services/api.ts`)

**API Base URL**: Configured via environment
```typescript
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';
```

**Key Integration Points**:

1. **Authentication**
   - Login: POST /auth/login
   - Get User: GET /auth/me
   - Token stored in localStorage

2. **Profile Management**
   - Get Profile: GET /providers/profile
   - Update Profile: PUT /providers/profile
   - Update Availability: PATCH /providers/availability

3. **Booking Management**
   - List Bookings: GET /providers/bookings
   - Recent Bookings: GET /providers/recent-bookings
   - Schedule: GET /providers/schedule
   - Accept: POST /bookings/{id}/accept
   - Reject: POST /bookings/{id}/reject
   - Start: POST /bookings/{id}/start
   - Complete: POST /bookings/{id}/complete

4. **Analytics**
   - Stats: GET /providers/stats
   - Earnings: GET /providers/earnings
   - Payments: GET /providers/payments

5. **Reviews**
   - Reviews: GET /providers/reviews
   - Average Rating: GET /reviews/provider/{id}/average-rating

### API Client Structure

```typescript
class ApiClient {
  private client: AxiosInstance;
  
  constructor() {
    this.client = axios.create({
      baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
      timeout: 30000
    });
    
    // Request interceptor adds JWT token
    this.client.interceptors.request.use((config) => {
      const token = localStorage.getItem('authToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });
    
    // Response interceptor handles 401
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }
}
```

---

## UI Feature Testing

### Dashboard Page (`/dashboard`)

**Features to Test**:
- ✅ Stats cards display (Today's Bookings, Active Bookings, Earnings, Rating)
- ✅ Period selector (Today/Week/Month)
- ✅ Quick actions (Update Availability, View Bookings, Earnings)
- ✅ Recent bookings list
- ✅ Alerts and notifications

**Expected Behavior**:
- Stats cards update when period is changed
- Real-time data loading with skeleton loaders
- Responsive layout on mobile/tablet
- Smooth transitions and animations

### Bookings Page (`/bookings`)

**Features to Test**:
- ✅ Bookings table with all columns
- ✅ Search by customer name, service, or ID
- ✅ Filter by status (ALL/PENDING/CONFIRMED/IN_PROGRESS/COMPLETED/CANCELLED)
- ✅ Accept button for PENDING bookings
- ✅ Reject button for PENDING bookings
- ✅ Start button for CONFIRMED bookings
- ✅ Complete button for IN_PROGRESS bookings
- ✅ Action modals for confirmation

**Expected Behavior**:
- Real-time status updates after actions
- Search filters instantly
- Status badges with correct colors
- Modal confirmations before actions
- Success/error toasts after operations

### Profile Page (`/profile`)

**Features to Test**:
- ✅ Profile information display
- ✅ Edit profile button
- ✅ Inline editing form
- ✅ Save/Cancel buttons
- ✅ Availability toggle switch
- ✅ Profile image display/upload (if implemented)
- ✅ Qualification and experience fields

**Expected Behavior**:
- Profile loads on mount
- Edit mode toggles correctly
- Form validation on save
- Success notification after update
- Availability toggle immediate feedback

### Reviews Page (`/reviews`)

**Features to Test**:
- ✅ Reviews list with pagination
- ✅ Star ratings display
- ✅ Customer information
- ✅ Review comments
- ✅ Sorting options (newest/oldest)
- ✅ Filter by rating (if implemented)

**Expected Behavior**:
- Paginated list loads correctly
- Star ratings displayed visually
- Timestamps formatted properly
- Smooth pagination transitions

### Analytics Page (`/analytics`) - If Implemented

**Features to Test**:
- Charts and graphs
- Revenue trends
- Booking trends
- Time period filters
- Export functionality

**Expected Behavior**:
- Interactive charts
- Data updates on filter change
- Responsive chart sizing

---

## Troubleshooting

### Common Issues

#### 1. Provider Profile Not Found
**Error**: `{"error": "Provider profile not found"}`

**Solution**: Ensure provider record exists in database
```bash
psql -U healthcare_user -d lucknow_healthcare
SELECT * FROM providers WHERE email = 'dr.sharma@lucknowhealthcare.com';
```

If empty, insert provider data manually or check data.sql execution.

#### 2. JWT Token Expired
**Error**: `401 Unauthorized`

**Solution**: Token expires after 15 minutes. Get a new token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "dr.sharma@lucknowhealthcare.com", "password": "password123"}'
```

#### 3. Booking Actions Fail
**Error**: `400 Bad Request` - "Only PENDING bookings can be accepted"

**Solution**: Check booking status first. Actions only work on specific statuses:
- Accept: Only PENDING → CONFIRMED
- Reject: Only PENDING → CANCELLED
- Start: Only CONFIRMED → IN_PROGRESS
- Complete: Only IN_PROGRESS → COMPLETED

#### 4. Empty Bookings List
**Issue**: `/providers/bookings` returns empty array

**Solution**: Create test bookings via database or API:
```sql
INSERT INTO bookings (user_id, provider_id, service_id, scheduled_date, scheduled_time, duration, status, total_amount, payment_status)
VALUES (
  '550e8400-e29b-41d4-a716-446655440106',  -- user_id
  '550e8400-e29b-41d4-a716-446655440201',  -- provider_id
  (SELECT id FROM services LIMIT 1),        -- service_id
  '2025-10-16', '10:00:00', 30, 'PENDING', 800.00, 'PENDING'
);
```

#### 5. CORS Issues
**Error**: `CORS policy: No 'Access-Control-Allow-Origin' header`

**Solution**: CORS is configured in `SecurityConfig.java`:
```java
configuration.setAllowedOriginPatterns(Arrays.asList("*"));
```

If still having issues, verify:
- Backend is running on port 8080
- Frontend makes requests to correct URL
- Proper headers in requests

#### 6. Database Connection Failed
**Error**: `Connection refused` or database error

**Solution**:
```bash
# Check PostgreSQL status
brew services list | grep postgresql  # macOS
systemctl status postgresql           # Linux

# Restart if needed
brew services restart postgresql@16
```

#### 7. Port Already in Use
**Error**: Backend won't start - port 8080 in use

**Solution**:
```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or change port in application.yml
server:
  port: 8081
```

---

## Security Best Practices

### Production Checklist

- [ ] Change default provider passwords
- [ ] Use strong JWT secret (512+ bits)
- [ ] Configure proper CORS origins (not *)
- [ ] Enable HTTPS
- [ ] Implement rate limiting
- [ ] Add input validation
- [ ] Enable SQL injection protection
- [ ] Add request logging
- [ ] Set up monitoring
- [ ] Configure backup strategy

### JWT Configuration (application.yml)
```yaml
jwt:
  secret: ${JWT_SECRET:your-super-secret-jwt-key-here}  # Change in production
  expiration: ${JWT_EXPIRATION:900000}  # 15 minutes
```

---

## Performance Optimization

### Database Indexes
Provider-related tables have indexes on:
- Provider ID
- Provider email
- Provider availability status
- Provider rating
- Booking provider_id + status
- Booking scheduled_date

### Pagination
All list endpoints support pagination:
```
?page=0&size=20
```

Default page size: 20
Maximum page size: 100

---

## API Response Format

### Success Response
```json
{
  "data": { /* response data */ },
  "status": "success"
}
```

### Error Response
```json
{
  "error": "Error message",
  "message": "Detailed error description",
  "status": "error"
}
```

### Pagination Response
```json
{
  "content": [ /* array of items */ ],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```

---

## Feature Comparison: Admin vs Provider Dashboard

| Feature | Admin Dashboard | Provider Dashboard |
|---------|----------------|-------------------|
| User Management | ✅ Full CRUD | ❌ No access |
| Provider Management | ✅ Full CRUD | ✅ Own profile only |
| Service Management | ✅ Full CRUD | ❌ View only |
| Booking Management | ✅ View all, assign providers | ✅ Own bookings, accept/reject |
| Payment Management | ✅ View all, process refunds | ✅ View own earnings |
| Review Management | ✅ View all, delete | ✅ View own reviews |
| Analytics | ✅ System-wide analytics | ✅ Personal analytics |
| Dashboard Stats | ✅ All users/providers/bookings | ✅ Personal stats |

---

## Next Steps

### Planned Features
1. **Real-time Notifications**
   - WebSocket integration for live booking updates
   - Push notifications for new bookings

2. **Advanced Analytics**
   - Revenue charts and graphs
   - Booking trend analysis
   - Performance metrics

3. **Profile Enhancements**
   - Profile image upload
   - Document management
   - Availability calendar

4. **Enhanced Booking Management**
   - Bulk actions
   - Advanced filters
   - Export to CSV/PDF

5. **Communication**
   - In-app messaging with customers
   - SMS notifications
   - Email notifications

---

## Support & Contact

### Logs Location
- **Backend**: `backend/logs/application.log`
- **Frontend**: Browser Developer Console

### Debug Mode
Enable Spring Boot debug logging:
```yaml
logging:
  level:
    com.lucknow.healthcare: DEBUG
```

---

## Version History

### v1.0.0 (October 14, 2025) - Current Version
- ✅ Provider authentication
- ✅ Profile management
- ✅ Booking management (accept/reject/start/complete)
- ✅ Analytics and statistics
- ✅ Reviews and ratings
- ✅ Earnings tracking
- ✅ Complete API documentation
- ✅ Frontend integration guide

---

**END OF DOCUMENT**

