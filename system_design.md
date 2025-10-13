# System Design Document
## Lucknow Nursing Healthcare Services - MVP Implementation

### 1. MVP Implementation Approach

We will implement a healthcare services platform using a **simplified monolithic architecture** optimized for rapid MVP development:

**Phase 1: Core Setup (Weeks 1-2)**
1. **Database Setup** - PostgreSQL database with 7 core tables and relationships
2. **Authentication System** - JWT-based authentication with email verification
3. **Basic CRUD Operations** - User management, service management, and booking management APIs
4. **Spring Boot Backend** - Single monolithic backend with RESTful APIs

**Phase 2: Authentication + Booking APIs (Weeks 3-4)**
1. **Service Booking System** - Complete booking workflow with provider assignment
2. **Provider Management** - Provider registration and availability tracking
3. **Basic Payment Integration** - Razorpay/Stripe integration (Phase 1.5)
4. **Email Notifications** - Basic email notifications for booking confirmations

**Phase 3: Flutter App UI & Integration (Weeks 5-6)**
1. **Customer Mobile App** - Flutter cross-platform app for customers
2. **Provider Web Dashboard** - Simple web interface for providers
3. **Admin Dashboard** - Basic admin panel for user and booking management
4. **API Integration** - Connect mobile app and web dashboards to backend

**Phase 4: Admin Dashboard + QA (Week 7)**
1. **Admin Dashboard Completion** - User management, booking management, basic analytics
2. **Testing** - End-to-end testing of all workflows
3. **Security Implementation** - HTTPS enforcement, input validation, data encryption

**Phase 5: Launch & Feedback Loop (Week 8)**
1. **Production Deployment** - Deploy on AWS/Render/Railway
2. **Performance Optimization** - Database indexing and API response optimization
3. **Launch Preparation** - Play Store beta release and feedback collection

### 2. Main User-UI Interaction Patterns

**Customer Mobile App Interactions:**
1. **Service Discovery & Booking**
   - Browse service categories (Nursing, Child Care, Elderly Care, Physiotherapy, Equipment, Ambulance)
   - View service details, pricing, and availability
   - Select date/time slots and preferred staff members
   - Add patient details and special instructions
   - Confirm booking and proceed to payment

2. **Order Management**
   - View current and past orders with real-time status updates
   - Track assigned staff location and estimated arrival time
   - Reschedule or cancel appointments with policy-based restrictions
   - Rate services and provide feedback after completion

3. **Emergency Services**
   - Quick access emergency booking with priority handling
   - Ambulance booking with real-time tracking and ETA
   - Direct contact with emergency response team

4. **Profile & Patient Management**
   - Manage multiple patient profiles with medical history
   - Update personal information and preferences
   - View payment history and download invoices
   - Manage notification preferences

**Provider Web Dashboard Interactions:**
1. **Booking Management**
   - View assigned bookings with customer details and contact information
   - Update booking status (accepted, in-progress, completed)
   - Add notes and updates for customer communication
   - View booking history and earnings

2. **Profile Management**
   - Update personal information and qualifications
   - Set availability schedules and working hours
   - View performance metrics and customer ratings
   - Manage account settings and preferences

**Admin Web Dashboard Interactions:**
1. **Operations Management**
   - Monitor dashboard with key metrics (active bookings, revenue, provider availability)
   - Assign providers to bookings based on availability and skills
   - Update booking status and communicate with customers
   - Handle emergency requests with priority routing

2. **User Management**
   - Approve/disable customer and provider accounts
   - Manage user profiles and verification status
   - Track user activity and booking patterns
   - Handle user support requests

3. **Basic Analytics**
   - Generate basic revenue reports
   - Monitor booking trends and service popularity
   - Track provider performance and customer satisfaction
   - Export basic data for analysis

### 3. MVP Architecture

```plantuml
package "Frontend Layer" {
    [Flutter Mobile App] as mobile
    [Provider Web Dashboard] as provider_web
    [Admin Web Dashboard] as admin_web
}

package "Backend Layer" {
    [Spring Boot Application] as backend
    package "Core Modules" {
        [Authentication Module] as auth
        [User Management Module] as user_mgmt
        [Booking Management Module] as booking_mgmt
        [Service Management Module] as service_mgmt
        [Provider Management Module] as provider_mgmt
        [Payment Module] as payment
        [Notification Module] as notification
    }
}

package "External Services" {
    [Razorpay/Stripe API] as payment_gateway
    [Email Service (SMTP)] as email_service
}

package "Data Layer" {
    database "PostgreSQL Database" {
        [users] as users_table
        [service_categories] as categories_table
        [services] as services_table
        [bookings] as bookings_table
        [providers] as providers_table
        [payments] as payments_table
        [reviews] as reviews_table
    }
}

mobile --> backend : HTTPS/REST API
provider_web --> backend : HTTPS/REST API
admin_web --> backend : HTTPS/REST API

backend --> auth : Authentication
backend --> user_mgmt : User Operations
backend --> booking_mgmt : Booking Operations
backend --> service_mgmt : Service Operations
backend --> provider_mgmt : Provider Operations
backend --> payment : Payment Operations
backend --> notification : Notifications

payment --> payment_gateway : Payment Processing
notification --> email_service : Email Notifications

backend --> users_table : User Data
backend --> categories_table : Service Categories
backend --> services_table : Service Data
backend --> bookings_table : Booking Data
backend --> providers_table : Provider Data
backend --> payments_table : Payment Data
backend --> reviews_table : Review Data
```

### 4. UI Navigation Flow

```plantuml
' Customer Mobile App Navigation
state "Customer App" as CustomerApp {
    state "Authentication" as Auth {
        [*] --> Login
        Login --> Register : "New User"
        Register --> Login : "Registration Complete"
        Login --> Dashboard : "Successful Login"
    }
    
    state "Main Dashboard" as Dashboard {
        [*] --> Home
        Home --> ServiceSelection : "Book Service"
        Home --> BookingHistory : "View Bookings"
        Home --> Profile : "Profile Menu"
        
        ServiceSelection --> BookingProcess : "Select Service"
        BookingProcess --> BookingHistory : "Booking Confirmed"
        
        BookingHistory --> Home : "Back to Home"
        Profile --> Home : "Back to Home"
    }
}

' Provider Web Dashboard Navigation
state "Provider Dashboard" as ProviderDashboard {
    state "Provider Auth" as ProviderAuth {
        [*] --> ProviderLogin
        ProviderLogin --> ProviderHome : "Successful Login"
    }
    
    state "Provider Home" as ProviderHome {
        [*] --> Dashboard
        Dashboard --> AssignedBookings : "View Bookings"
        Dashboard --> Earnings : "View Earnings"
        Dashboard --> ProviderProfile : "Profile"
        
        AssignedBookings --> BookingDetails : "View Details"
        BookingDetails --> UpdateStatus : "Update Status"
        UpdateStatus --> AssignedBookings : "Status Updated"
        
        Earnings --> Dashboard : "Back to Dashboard"
        ProviderProfile --> Dashboard : "Back to Dashboard"
    }
}

' Admin Web Dashboard Navigation
state "Admin Dashboard" as AdminDashboard {
    state "Admin Auth" as AdminAuth {
        [*] --> AdminLogin
        AdminLogin --> AdminHome : "Successful Login"
    }
    
    state "Admin Home" as AdminHome {
        [*] --> Dashboard
        Dashboard --> UserManagement : "Manage Users"
        Dashboard --> BookingManagement : "Manage Bookings"
        Dashboard --> ProviderManagement : "Manage Providers"
        Dashboard --> BasicAnalytics : "View Reports"
        
        UserManagement --> Dashboard : "Back to Dashboard"
        BookingManagement --> Dashboard : "Back to Dashboard"
        ProviderManagement --> Dashboard : "Back to Dashboard"
        BasicAnalytics --> Dashboard : "Back to Dashboard"
    }
}
```

### 5. Data Structures and Interfaces Overview

**Core Entity Classes (MVP):**
- `User`: Customer/Admin/Provider profiles with authentication and role-based access
- `ServiceCategory`: Service categories (Nursing, Child Care, Elderly Care, etc.)
- `Service`: Healthcare service definitions with pricing and availability
- `Booking`: Service bookings with scheduling, payment, and status tracking
- `Provider`: Healthcare professionals with qualifications, availability, and ratings
- `Payment`: Transaction records with gateway integration (Phase 1.5)
- `Review`: Customer feedback and ratings (Phase 1.5)

**Service Layer Interfaces:**
- `IUserService`: User registration, authentication, profile management
- `IBookingService`: Booking creation, status updates, provider assignment
- `IProviderService`: Provider management, availability tracking, performance metrics
- `IPaymentService`: Payment processing, refunds (Phase 1.5)
- `INotificationService`: Email notifications for booking confirmations

**Repository Pattern:**
- Abstract data access through repository interfaces
- PostgreSQL-specific implementations with optimized queries
- Simple data access without caching for MVP

### 6. Program Call Flow Overview

**Service Booking Flow:**
1. Customer selects service category and views available options
2. System checks provider availability for selected date/time
3. Customer confirms booking with patient details and special instructions
4. Booking is created with PENDING status and provider assignment
5. Payment gateway integration initiates secure transaction (Phase 1.5)
6. Upon payment success, booking status updates to CONFIRMED
7. Automated email notifications sent to customer and assigned provider
8. Provider can update booking status throughout service delivery

**Provider Operations Flow:**
1. Provider authenticates with JWT token and role-based permissions
2. Dashboard displays assigned bookings and customer details
3. Provider can update booking status (accepted, in-progress, completed)
4. Provider can add notes and communicate with customers
5. Provider can view earnings and performance metrics

**Admin Operations Flow:**
1. Admin authenticates with JWT token and role-based permissions
2. Dashboard displays basic metrics and pending operations
3. Admin can assign providers to bookings manually
4. Booking status updates trigger automated customer notifications
5. Basic analytics provide insights into booking trends and performance
6. Admin can manage user accounts and provider verification

### 7. Database ER Diagram Overview

**Core Tables and Relationships (MVP):**
- `users` (1:N) → `bookings`: Customers can place multiple bookings
- `bookings` (1:1) → `payments`: Each booking has one payment record (Phase 1.5)
- `bookings` (1:1) → `providers`: Each booking assigned to one provider
- `providers` (1:N) → `bookings`: Providers can be assigned to multiple bookings
- `services` (1:N) → `bookings`: Services can be booked multiple times
- `service_categories` (1:N) → `services`: Categories contain multiple services
- `bookings` (1:N) → `reviews`: Bookings can have multiple reviews (Phase 1.5)

**Key Indexes for Performance:**
- User email (unique index for authentication)
- Booking status and date (for dashboard queries)
- Provider availability and role (for assignment optimization)
- Payment status and transaction ID (for financial tracking)

### 8. Security Architecture

**Authentication & Authorization:**
- JWT tokens with configurable expiration (15 minutes access, 7 days refresh)
- Role-based access control (CUSTOMER, ADMIN, PROVIDER)
- Email verification for account activation
- Secure password reset with time-limited tokens

**Data Protection:**
- HTTPS enforcement for all API communications
- Password hashing using bcrypt with salt rounds
- Sensitive data encryption at rest (payment information, medical records)
- Input validation and sanitization to prevent injection attacks

**API Security:**
- Rate limiting to prevent abuse (100 requests/minute per user)
- CORS configuration for cross-origin requests
- Request/response logging for audit trails
- SQL injection prevention through parameterized queries

### 9. Payment Integration Architecture (Phase 1.5)

**Supported Gateways:**
- Razorpay for Indian market with UPI, cards, and net banking
- Stripe for international transactions and card processing
- Webhook handling for real-time payment status updates

**Payment Flow:**
1. Booking creation generates payment intent with gateway
2. Secure payment page redirects customer to gateway
3. Gateway processes payment and sends webhook notification
4. System validates webhook signature and updates payment status
5. Booking status automatically updates upon successful payment
6. Email notification sent to customer

### 10. Notification System Architecture

**Multi-Channel Delivery:**
- Email notifications via SMTP (booking confirmations, reminders)
- Basic SMS notifications (Phase 1.5)
- In-app notifications for immediate user attention

**Notification Types:**
- Booking confirmations and status updates
- Payment confirmations (Phase 1.5)
- Appointment reminders (24 hours before)
- Provider assignment notifications
- Emergency alerts for urgent bookings

### 11. File Management System (Simplified for MVP)

**Document Types:**
- Provider verification documents (Aadhar, certificates, licenses)
- Basic image uploads for provider profiles

**Storage Strategy:**
- Local file system for development environment
- Simple file storage for production (no cloud storage for MVP)
- Basic file upload and access

### 12. Monitoring and Analytics (Basic for MVP)

**Key Performance Indicators:**
- Booking completion rates and customer satisfaction scores
- Provider utilization and performance metrics
- Revenue tracking by service category and time period
- Basic system performance monitoring (API response times, error rates)

**Business Intelligence (Basic):**
- Peak booking time analysis for resource planning
- Customer behavior patterns and service preferences
- Provider performance evaluation
- Basic financial reporting with revenue analysis by service category

### 13. Scalability Considerations (Post-MVP)

**Horizontal Scaling (Future):**
- Microservices architecture for independent service scaling
- Database read replicas for improved query performance
- Load balancing across multiple application instances
- CDN integration for static file delivery

**Performance Optimization (MVP):**
- Database indexing strategy for frequently queried fields
- Simple data access without caching for MVP
- Basic asynchronous processing for notifications
- API response pagination for large dataset queries

### 14. Deployment Architecture (Simplified for MVP)

**Environment Strategy:**
- Development: Local development with PostgreSQL
- Staging: Cloud-based environment (Railway/Render)
- Production: Simple cloud deployment (Railway/Render/AWS)

**CI/CD Pipeline (Basic):**
- Automated testing (unit, integration)
- Basic code quality checks
- Simple deployment with rollback capabilities
- Database migration management with version control

### 15. MVP Clarifications Needed

**Business Logic Clarifications Needed:**
1. **Service Area Coverage**: Exact geographical boundaries within Lucknow and surrounding areas for service delivery
2. **Emergency Response Protocol**: Specific SLA requirements for emergency bookings and escalation procedures
3. **Provider Certification Requirements**: Detailed verification process and mandatory certifications for different healthcare roles
4. **Payment Terms**: Cancellation policies, refund processing timelines, and partial payment options for long-term care
5. **Admin Dashboard Technology**: Should we use React or Thymeleaf for the admin dashboard?

**Technical Clarifications Needed:**
1. **Data Retention Policy**: Legal requirements for healthcare data retention and patient privacy compliance
2. **Regulatory Compliance**: Specific healthcare regulations in Uttar Pradesh and required licensing
3. **Quality Assurance Metrics**: Specific KPIs for service quality monitoring and provider performance evaluation

**Assumptions Made for MVP:**
1. Initial deployment will serve Lucknow metropolitan area only
2. Providers will use web dashboard for booking updates
3. Customers primarily access services through mobile applications
4. Payment processing will comply with Indian RBI guidelines
5. English language support will be sufficient for initial launch
6. Simple monolithic architecture will be sufficient for MVP
7. Basic analytics will be sufficient for initial launch