# Test Data Information

## Login Credentials

### Admin User
- **Email**: admin@lucknowhealthcare.com
- **Password**: password123
- **Role**: ADMIN

### Provider Users
1. **Dr. Rajesh Sharma (Cardiologist)**
   - **Email**: dr.sharma@lucknowhealthcare.com
   - **Password**: password123
   - **Role**: PROVIDER
   - **Specialization**: Cardiology
   - **Rating**: 4.8/5

2. **Dr. Priya Patel (Dermatologist)**
   - **Email**: dr.patel@lucknowhealthcare.com
   - **Password**: password123
   - **Role**: PROVIDER
   - **Specialization**: Dermatology
   - **Rating**: 4.6/5

3. **Dr. Amit Singh (Orthopedist)**
   - **Email**: dr.singh@lucknowhealthcare.com
   - **Password**: password123
   - **Role**: PROVIDER
   - **Specialization**: Orthopedics
   - **Rating**: 4.7/5

4. **Dr. Sunita Gupta (Pediatrician)**
   - **Email**: dr.gupta@lucknowhealthcare.com
   - **Password**: password123
   - **Role**: PROVIDER
   - **Specialization**: Pediatrics
   - **Rating**: 4.9/5

### Regular Users
1. **Rahul Kumar**
   - **Email**: user1@example.com
   - **Password**: password123
   - **Role**: USER

2. **Priya Sharma**
   - **Email**: user2@example.com
   - **Password**: password123
   - **Role**: USER

3. **Amit Verma**
   - **Email**: user3@example.com
   - **Password**: password123
   - **Role**: USER

4. **Sneha Agarwal**
   - **Email**: user4@example.com
   - **Password**: password123
   - **Role**: USER

5. **Vikram Yadav**
   - **Email**: user5@example.com
   - **Password**: password123
   - **Role**: USER

## Sample Data Created

### Service Categories
- General Medicine
- Cardiology
- Dermatology
- Pediatrics
- Gynecology
- Orthopedics
- Neurology
- Psychiatry

### Healthcare Providers
- 4 verified providers with different specializations
- Each provider has multiple services
- Realistic consultation fees and ratings

### Services
- 12 different healthcare services
- Various price ranges (₹300 - ₹1500)
- Different consultation durations

### Bookings
- 5 sample bookings with different statuses
- Mix of confirmed, pending, and completed bookings

### Payments
- Payment records for completed bookings
- Different payment methods (Credit Card, UPI, Debit Card, Net Banking)

### Reviews
- 4 sample reviews with ratings
- Realistic feedback from patients

## Testing Scenarios

### Admin Dashboard
- View all users, providers, bookings, and payments
- Manage system settings and analytics

### Provider Dashboard
- Manage services and availability
- View bookings and patient reviews
- Update profile and consultation fees

### User Dashboard
- Browse healthcare providers and services
- Book appointments
- View booking history and make payments
- Leave reviews for completed services

## API Endpoints for Testing

### Authentication
- POST `/api/auth/login` - Login with any of the above credentials
- POST `/api/auth/register` - Register new users

### Public Endpoints
- GET `/api/services` - Browse all services
- GET `/api/providers` - Browse all providers
- GET `/api/categories` - Browse service categories

### Protected Endpoints (require authentication)
- GET `/api/users/profile` - Get user profile
- POST `/api/bookings` - Create new booking
- GET `/api/bookings` - Get user bookings
- POST `/api/reviews` - Submit review
- GET `/api/payments` - Get payment history
