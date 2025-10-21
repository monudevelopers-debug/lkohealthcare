# 🏥 Customer Portal - Complete Implementation Plan

## 📋 Table of Contents
1. [Core Features Overview](#core-features-overview)
2. [Database Schema](#database-schema)
3. [Backend API Endpoints](#backend-api-endpoints)
4. [Frontend Structure](#frontend-structure)
5. [Implementation Phases](#implementation-phases)
6. [Security & Compliance](#security--compliance)
7. [Notifications System](#notifications-system)
8. [Payment Integration](#payment-integration)

---

## 🎯 Core Features Overview

### ✅ Must Have (Phase 1)
- [ ] Sidebar Navigation (post-login)
- [ ] Patient Management (CRUD)
- [ ] Patient Selection in Booking Flow
- [ ] Terms & Conditions + Privacy Consent
- [ ] Email Notifications
- [ ] Payment History
- [ ] Invoice Download
- [ ] Booking Status with Provider Details
- [ ] Post-Service Feedback/Review
- [ ] Contact Page (always visible)

### 🚀 Should Have (Phase 2)
- [ ] SMS Notifications (Twilio)
- [ ] Reminder Notifications (24h before appointment)
- [ ] Advance Payment vs Pay-After-Service
- [ ] Cancellation Policy Display
- [ ] FAQ Page
- [ ] Blogs Page
- [ ] Patient Medical History View
- [ ] Prescription Upload & Management

### 💎 Nice to Have (Phase 3)
- [ ] In-app Chat (Customer ↔ Provider)
- [ ] Referral Program
- [ ] Quick Re-booking
- [ ] Service Package Deals (Contact-based)
- [ ] Real-time Provider Tracking (Future)

---

## 🗄️ Database Schema

### 1. **patients** Table
```sql
CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL CHECK (age > 0 AND age <= 150),
    gender VARCHAR(20) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    weight DECIMAL(5,2) CHECK (weight > 0), -- in kg
    height DECIMAL(5,2) CHECK (height > 0), -- in cm
    blood_group VARCHAR(10) CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')),
    is_diabetic BOOLEAN DEFAULT FALSE,
    bp_status VARCHAR(20) DEFAULT 'NORMAL' CHECK (bp_status IN ('NORMAL', 'HIGH', 'LOW')),
    allergies TEXT,
    chronic_conditions TEXT,
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    relationship_to_customer VARCHAR(50) NOT NULL, -- Self, Parent, Child, Spouse, Sibling, Other
    is_sensitive_data BOOLEAN DEFAULT FALSE, -- Extra privacy flag
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_created_at (created_at)
);
```

### 2. **patient_medications** Table
```sql
CREATE TABLE patient_medications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100), -- e.g., "Twice daily", "As needed"
    purpose TEXT,
    start_date DATE,
    is_ongoing BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_patient_id (patient_id)
);
```

### 3. **patient_documents** Table
```sql
CREATE TABLE patient_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    document_type VARCHAR(50) NOT NULL CHECK (document_type IN ('PRESCRIPTION', 'MEDICAL_REPORT', 'LAB_RESULT', 'XRAY', 'OTHER')),
    file_name VARCHAR(255) NOT NULL,
    file_url TEXT NOT NULL,
    file_size BIGINT, -- in bytes
    description TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_patient_id (patient_id),
    INDEX idx_uploaded_at (uploaded_at)
);
```

### 4. **bookings** Table (Modified)
```sql
ALTER TABLE bookings 
ADD COLUMN patient_id UUID REFERENCES patients(id) ON DELETE SET NULL,
ADD COLUMN estimated_arrival_time TIMESTAMP,
ADD COLUMN payment_method VARCHAR(50) CHECK (payment_method IN ('ONLINE', 'CASH', 'UPI')),
ADD COLUMN payment_timing VARCHAR(20) DEFAULT 'ADVANCE' CHECK (payment_timing IN ('ADVANCE', 'POST_SERVICE')),
ADD COLUMN cancellation_reason TEXT,
ADD COLUMN cancelled_by VARCHAR(20) CHECK (cancelled_by IN ('CUSTOMER', 'PROVIDER', 'ADMIN')),
ADD COLUMN refund_status VARCHAR(20) CHECK (refund_status IN ('NOT_APPLICABLE', 'PENDING', 'PROCESSED', 'REJECTED')),
ADD COLUMN refund_amount DECIMAL(10,2),
ADD COLUMN refund_processed_at TIMESTAMP;

-- Add index for patient lookups
CREATE INDEX idx_bookings_patient_id ON bookings(patient_id);
```

### 5. **payments** Table (Enhanced)
```sql
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES users(id),
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_gateway VARCHAR(50), -- Razorpay, Stripe, etc.
    transaction_id VARCHAR(255),
    payment_status VARCHAR(20) NOT NULL CHECK (payment_status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    payment_timing VARCHAR(20) NOT NULL CHECK (payment_timing IN ('ADVANCE', 'POST_SERVICE')),
    invoice_number VARCHAR(50) UNIQUE,
    invoice_url TEXT,
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_booking_id (booking_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
);
```

### 6. **notifications** Table
```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    booking_id UUID REFERENCES bookings(id) ON DELETE SET NULL,
    notification_type VARCHAR(50) NOT NULL CHECK (notification_type IN (
        'BOOKING_CONFIRMED', 'PROVIDER_ASSIGNED', 'SERVICE_STARTED', 
        'SERVICE_COMPLETED', 'PAYMENT_RECEIVED', 'REMINDER', 
        'CANCELLATION', 'REFUND_PROCESSED'
    )),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    email_sent BOOLEAN DEFAULT FALSE,
    email_sent_at TIMESTAMP,
    sms_sent BOOLEAN DEFAULT FALSE,
    sms_sent_at TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);
```

### 7. **consent_records** Table
```sql
CREATE TABLE consent_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    patient_id UUID REFERENCES patients(id) ON DELETE CASCADE,
    consent_type VARCHAR(50) NOT NULL CHECK (consent_type IN (
        'TERMS_AND_CONDITIONS', 'PRIVACY_POLICY', 'MEDICAL_DATA_SHARING', 'HIPAA_COMPLIANCE'
    )),
    consent_version VARCHAR(20) NOT NULL, -- Track version changes
    is_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    accepted_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    revoked_at TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_patient_id (patient_id),
    UNIQUE KEY unique_consent (user_id, consent_type, consent_version)
);
```

### 8. **referrals** Table
```sql
CREATE TABLE referrals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referrer_id UUID NOT NULL REFERENCES users(id), -- Who referred
    referee_id UUID REFERENCES users(id), -- Who was referred (NULL until signup)
    referral_code VARCHAR(20) UNIQUE NOT NULL,
    referee_email VARCHAR(255),
    referee_phone VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'EXPIRED')),
    reward_type VARCHAR(50), -- DISCOUNT, CASHBACK, FREE_SERVICE
    reward_amount DECIMAL(10,2),
    reward_redeemed BOOLEAN DEFAULT FALSE,
    redeemed_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_referrer_id (referrer_id),
    INDEX idx_referral_code (referral_code),
    INDEX idx_status (status)
);
```

### 9. **blogs** Table
```sql
CREATE TABLE blogs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    content TEXT NOT NULL,
    excerpt TEXT,
    featured_image_url TEXT,
    author_id UUID REFERENCES users(id),
    category VARCHAR(100),
    tags TEXT[], -- PostgreSQL array
    is_published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP,
    view_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_slug (slug),
    INDEX idx_is_published (is_published),
    INDEX idx_category (category)
);
```

### 10. **faqs** Table
```sql
CREATE TABLE faqs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    view_count INTEGER DEFAULT 0,
    helpful_count INTEGER DEFAULT 0,
    not_helpful_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_category (category),
    INDEX idx_is_active (is_active),
    INDEX idx_display_order (display_order)
);
```

---

## 🔌 Backend API Endpoints

### Patient Management
```
POST   /api/patients                    - Create patient
GET    /api/patients                    - Get all patients for customer
GET    /api/patients/{id}               - Get patient by ID
PUT    /api/patients/{id}               - Update patient
DELETE /api/patients/{id}               - Delete patient
GET    /api/patients/{id}/history       - Get patient's service history
```

### Patient Medications
```
POST   /api/patients/{id}/medications              - Add medication
GET    /api/patients/{id}/medications              - Get all medications
PUT    /api/patients/{id}/medications/{medId}      - Update medication
DELETE /api/patients/{id}/medications/{medId}      - Delete medication
```

### Patient Documents
```
POST   /api/patients/{id}/documents                - Upload document
GET    /api/patients/{id}/documents                - Get all documents
DELETE /api/patients/{id}/documents/{docId}        - Delete document
GET    /api/patients/{id}/documents/{docId}/download - Download document
```

### Enhanced Booking Flow
```
POST   /api/bookings/estimate                      - Get cost estimate with patient
POST   /api/bookings                               - Create booking (requires patient_id)
GET    /api/bookings/{id}/tracking                 - Get booking status & provider info
POST   /api/bookings/{id}/cancel                   - Cancel booking
GET    /api/bookings/{id}/cancellation-policy      - Get cancellation policy
```

### Payments & Invoices
```
GET    /api/payments                               - Get payment history
GET    /api/payments/{id}                          - Get payment details
GET    /api/payments/{id}/invoice                  - Download invoice PDF
POST   /api/payments/initiate                      - Initiate payment
POST   /api/payments/verify                        - Verify payment status
POST   /api/payments/{id}/refund                   - Request refund
```

### Notifications
```
GET    /api/notifications                          - Get all notifications
GET    /api/notifications/unread                   - Get unread notifications
PUT    /api/notifications/{id}/read                - Mark as read
PUT    /api/notifications/read-all                 - Mark all as read
DELETE /api/notifications/{id}                     - Delete notification
```

### Consent Management
```
POST   /api/consents                               - Record consent
GET    /api/consents                               - Get user's consents
GET    /api/consents/required                      - Get required consents
PUT    /api/consents/{id}/revoke                   - Revoke consent
```

### Referrals
```
GET    /api/referrals/my-code                      - Get user's referral code
POST   /api/referrals/send                         - Send referral to friend
GET    /api/referrals/my-referrals                 - Get all referrals
POST   /api/referrals/apply                        - Apply referral code
GET    /api/referrals/rewards                      - Get rewards earned
```

### Blogs & FAQs
```
GET    /api/blogs                                  - Get published blogs
GET    /api/blogs/{slug}                           - Get blog by slug
POST   /api/blogs/{id}/view                        - Increment view count

GET    /api/faqs                                   - Get all FAQs
GET    /api/faqs?category={category}               - Get FAQs by category
POST   /api/faqs/{id}/helpful                      - Mark as helpful
```

### Reviews (Customer Side)
```
POST   /api/bookings/{id}/review                   - Submit review after service
GET    /api/reviews/my-reviews                     - Get my reviews
PUT    /api/reviews/{id}                           - Edit review (within 24h)
```

---

## 🎨 Frontend Structure

### Navigation (Post-Login)
```
Sidebar Menu:
├── 🏠 Dashboard
├── 🩺 Services (Browse & Book)
├── 👥 My Patients
├── 📋 My Bookings
├── 💳 Payment History
├── ⭐ My Reviews
├── 📝 Blogs
├── ❓ FAQ
├── 👤 Profile
├── ⚙️ Settings
└── 📞 Contact (Always visible)
```

### Key Pages/Components

#### 1. **Dashboard** (`/dashboard`)
- Welcome message
- Upcoming appointments
- Recent bookings
- Quick actions (Book Service, Add Patient)
- Notifications summary

#### 2. **Patients Page** (`/patients`)
- Patient list (cards/table)
- Add Patient button → Modal/Page
- Each patient card shows:
  - Name, Age, Relationship
  - Quick actions: View, Edit, Delete
  - Service history count
- Patient Details Modal:
  - Basic info
  - Medical history
  - Medications
  - Documents
  - Past bookings

#### 3. **Add/Edit Patient** (`/patients/new`, `/patients/{id}/edit`)
**Required Consents Before Proceeding:**
- ✅ Terms & Conditions
- ✅ Privacy Policy
- ✅ Medical Data Sharing Consent
- ✅ HIPAA-like Compliance Agreement

**Form Sections:**
- Basic Information (Name, Age, Gender, Relationship)
- Physical Attributes (Weight, Height, Blood Group)
- Medical Conditions (Diabetic, BP, Allergies, Chronic Conditions)
- Emergency Contact
- 🔒 Mark as Sensitive Data (checkbox)

#### 4. **Services Page** (`/services`)
**New Flow:**
1. **Patient Selection First** (if customer has patients)
   - Show patient selector at top
   - "Select Patient" → Modal/Dropdown
   - Can also click "Book for someone new" → Add Patient
2. Service browsing with filters
3. Service details with pricing
4. "Book Now" → Pre-fills selected patient

#### 5. **Booking Flow** (`/services/{id}/book`)
**Steps:**
1. **Patient Selection** (if not pre-selected)
2. **Service Details Review**
3. **Date & Time Selection**
4. **Special Instructions** (for provider)
5. **Payment Method Selection**
   - Online Payment (Advance)
   - Cash on Service (Post-Service)
6. **Cancellation Policy Display** (must read & accept)
7. **Review & Confirm**
8. **Payment Processing** (if advance payment)
9. **Booking Confirmation** with details

#### 6. **My Bookings** (`/bookings`)
**Tabs:**
- Upcoming
- In Progress
- Completed
- Cancelled

**Booking Card Shows:**
- Service name & icon
- Patient name
- Date & Time
- Status badge
- **Provider Details (once assigned):**
  - Name
  - Photo
  - Qualifications
  - Rating ⭐
  - Experience
  - Estimated Arrival Time
- Action buttons (View Details, Cancel, Review if completed)

#### 7. **Booking Details** (`/bookings/{id}`)
- Full booking information
- Patient details
- Service details
- Provider details (if assigned)
- Status timeline
- Payment information
- Cancel button (with policy reminder)
- Review button (if completed & not reviewed)

#### 8. **Payment History** (`/payments`)
- List of all payments
- Filters: Date range, Status, Method
- Each payment shows:
  - Booking reference
  - Service & Patient
  - Amount
  - Date
  - Status
  - Payment method
  - Download Invoice button 📄

#### 9. **Invoice Modal/Page**
**Invoice Details:**
- Invoice Number
- Date
- Customer Details
- Patient Details
- Service Details
- Provider Details
- Amount Breakdown:
  - Service Charge
  - GST/Taxes
  - Discount (if any)
  - Total Amount
- Payment Method
- Transaction ID
- Download PDF button

#### 10. **Post-Service Review** (`/bookings/{id}/review`)
- Only accessible after service completion
- Rating (1-5 stars) for:
  - Overall Experience
  - Provider Professionalism
  - Service Quality
  - Timeliness
- Written Review (optional)
- Photo upload (optional)
- Submit button

#### 11. **Referrals Page** (`/referrals`)
- My Referral Code (big display with copy button)
- Share options (Email, WhatsApp, SMS)
- Referral form (enter friend's email/phone)
- My Referrals list:
  - Friend's name/email
  - Status (Pending, Completed)
  - Reward earned
  - Date
- Total Rewards Summary
- Redeem/Apply rewards

#### 12. **Blogs** (`/blogs`)
- Blog list with featured images
- Categories filter
- Search
- Each blog card:
  - Featured image
  - Title
  - Excerpt
  - Author & Date
  - Read time
  - Read More button

#### 13. **Blog Detail** (`/blogs/{slug}`)
- Full blog content
- Author info
- Related blogs
- Share buttons
- Comments (optional future feature)

#### 14. **FAQ** (`/faq`)
- Categories sidebar
- Search bar
- Accordion-style Q&A
- "Was this helpful?" buttons
- Contact support link

#### 15. **Profile** (`/profile`)
- Personal information (Name, Email, Phone)
- Address
- **No "Role" display** ✅
- Edit button
- Change password

---

## 📅 Implementation Phases

### **Phase 1: Core Foundation** (Week 1-2)
**Backend:**
1. Create database migrations for all tables
2. Create entities: Patient, PatientMedication, PatientDocument, ConsentRecord
3. Implement Patient CRUD APIs
4. Modify Booking entity & APIs to include patient_id
5. Implement Consent Management APIs
6. Set up email service (Spring Mail with Gmail SMTP)

**Frontend:**
1. Set up sidebar navigation (post-login)
2. Remove role display from profile
3. Create Patients page (list view)
4. Create Add/Edit Patient forms
5. Implement Consent modals (T&C, Privacy, Medical Data)
6. Modify booking flow to select patient first

**Testing:**
- Patient CRUD operations
- Consent recording
- Updated booking flow with patient selection

### **Phase 2: Bookings & Notifications** (Week 3-4)
**Backend:**
1. Enhance Booking APIs:
   - Cancellation with policy
   - Provider details in response
   - Estimated arrival time calculation
2. Create Payment entity & APIs
3. Create Notification entity & APIs
4. Implement Email notification service:
   - Booking confirmation
   - Provider assigned
   - Service started/completed
   - Payment received
5. Implement scheduled notifications (Spring Scheduler):
   - 24h reminder before appointment

**Frontend:**
1. My Bookings page with all tabs
2. Booking Details page with provider info
3. Cancellation flow with policy display
4. Notification bell icon in header
5. Notifications dropdown/page
6. Mark as read functionality

**Testing:**
- Booking cancellation
- Email notifications delivery
- Reminder scheduling

### **Phase 3: Payments & Invoices** (Week 5-6)
**Backend:**
1. Payment entity & repository
2. Payment APIs (list, detail, invoice)
3. Invoice generation (PDF using iText/Flying Saucer)
4. Payment gateway integration preparation:
   - Razorpay/Stripe setup
   - Webhook handling
5. Refund processing API

**Frontend:**
1. Payment History page
2. Payment method selection in booking
3. Advance vs Post-Service payment option
4. Invoice view/download
5. Payment gateway integration:
   - Razorpay modal
   - Payment verification
   - Success/failure handling

**Testing:**
- Payment flow end-to-end
- Invoice generation
- Refund requests

### **Phase 4: Reviews & Medical Records** (Week 7)
**Backend:**
1. Review APIs for customers
2. Patient medication CRUD APIs
3. Patient document upload API:
   - File storage (AWS S3 or local with proper security)
   - File type validation
   - File size limits

**Frontend:**
1. Post-service review page
2. Patient medications management
3. Document upload (prescriptions, reports)
4. Document viewer
5. Patient medical history view

**Testing:**
- Review submission
- Document upload & download
- Medical history display

### **Phase 5: Referrals & Content** (Week 8)
**Backend:**
1. Referral entity & APIs
2. Referral code generation (unique)
3. Referral tracking & rewards
4. Blog entity & APIs (admin creates blogs)
5. FAQ entity & APIs

**Frontend:**
1. Referrals page
2. Referral code sharing
3. Referral tracking
4. Blogs list & detail pages
5. FAQ page with search

**Testing:**
- Referral code generation
- Referral tracking
- Content display

### **Phase 6: SMS & Enhanced Notifications** (Week 9)
**Backend:**
1. Twilio integration for SMS
2. SMS notification service
3. Update notification service to send both email & SMS
4. Notification preferences (email only, SMS only, both)

**Frontend:**
1. Notification preferences in settings
2. SMS verification (optional)

**Testing:**
- SMS delivery
- Notification preferences

### **Phase 7: Polish & Security** (Week 10)
**Backend:**
1. Audit sensitive data access
2. Implement data encryption for sensitive fields
3. Rate limiting for APIs
4. Security headers
5. CORS configuration refinement

**Frontend:**
1. Loading states & error handling
2. Responsive design polish
3. Accessibility improvements
4. Performance optimization
5. Progressive Web App (PWA) features

**Testing:**
- Security audit
- Performance testing
- Accessibility testing
- Cross-browser testing

---

## 🔒 Security & Compliance

### Data Encryption
- Sensitive patient data encrypted at rest
- TLS/SSL for data in transit
- Secure file upload with virus scanning

### Access Control
- Customers can only access their own patients
- Providers can only see patient data for assigned bookings
- Admin has full access with audit logging

### Audit Logging
```java
@Entity
public class AuditLog {
    private UUID id;
    private UUID userId;
    private String action; // VIEW, CREATE, UPDATE, DELETE
    private String entityType; // PATIENT, BOOKING, etc.
    private UUID entityId;
    private String ipAddress;
    private LocalDateTime timestamp;
}
```

### Consent Versioning
- Track consent versions
- Re-prompt users when T&C/Privacy Policy updates
- Allow consent revocation

### Data Retention
- Define retention policy for medical data
- Automatic anonymization after X years
- Right to be forgotten (GDPR compliance)

---

## 📧 Notifications System

### Email Configuration
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: monudevelopers@gmail.com
    password: SHIvam@7426
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
```

### Email Templates

#### 1. Booking Confirmation
```
Subject: Booking Confirmed - [Service Name]

Dear [Customer Name],

Your booking has been confirmed!

Booking ID: [ID]
Service: [Service Name]
Patient: [Patient Name]
Date: [Date]
Time: [Time]

We will notify you once a provider is assigned.

[View Booking Details Button]
```

#### 2. Provider Assigned
```
Subject: Provider Assigned - [Service Name]

Dear [Customer Name],

Good news! A provider has been assigned for your booking.

Provider: Dr. [Provider Name]
Qualifications: [Qualifications]
Rating: ⭐ [Rating]
Experience: [X] years
Estimated Arrival: [Time]

[View Provider Profile] [Track Booking]
```

#### 3. Reminder (24h before)
```
Subject: Reminder - Appointment Tomorrow

Dear [Customer Name],

This is a reminder for your upcoming appointment tomorrow.

Service: [Service Name]
Patient: [Patient Name]
Date: [Date]
Time: [Time]
Provider: Dr. [Provider Name]

Please ensure the patient is available at the scheduled time.

[View Details] [Reschedule] [Cancel]
```

#### 4. Service Completed
```
Subject: Service Completed - Please Review

Dear [Customer Name],

Your service has been completed successfully!

We hope [Patient Name] received excellent care.

Please take a moment to review your experience:
[Leave Review Button]

Payment: [Paid/Pending]
[Download Invoice]
```

### SMS Templates (Twilio)

#### 1. Booking Confirmation
```
LKO Healthcare: Booking confirmed for [Patient] on [Date] at [Time]. 
Booking ID: [ID]. Track: [Short Link]
```

#### 2. Provider Assigned
```
LKO Healthcare: Dr. [Provider] assigned for your booking. 
Rating: [X]⭐. ETA: [Time]. Details: [Short Link]
```

#### 3. Reminder
```
LKO Healthcare: Reminder - Appointment tomorrow at [Time] with Dr. [Provider] 
for [Patient]. Confirm: [Short Link]
```

### Notification Triggers
```java
// Booking created
notificationService.sendBookingConfirmation(booking);

// Provider assigned
notificationService.sendProviderAssigned(booking, provider);

// 24h before appointment
@Scheduled(cron = "0 0 9 * * *") // 9 AM daily
public void sendReminders() {
    List<Booking> tomorrow = bookingService.getBookingsForDate(LocalDate.now().plusDays(1));
    tomorrow.forEach(notificationService::sendReminder);
}

// Service completed
notificationService.sendServiceCompleted(booking);

// Payment received
notificationService.sendPaymentReceived(payment);
```

---

## 💳 Payment Integration

### Payment Gateway: Razorpay (Recommended for India)

#### Setup
```yaml
razorpay:
  key_id: [YOUR_KEY_ID]
  key_secret: [YOUR_KEY_SECRET]
  webhook_secret: [YOUR_WEBHOOK_SECRET]
```

#### Payment Flow

**1. Advance Payment:**
```
Customer selects service → Chooses patient → Selects date/time 
→ Reviews booking → Selects "Pay Now" → Razorpay modal opens 
→ Payment successful → Booking confirmed → Notification sent
```

**2. Post-Service Payment:**
```
Customer selects service → Chooses patient → Selects date/time 
→ Reviews booking → Selects "Pay After Service" → Booking confirmed 
→ Service completed → Payment link sent → Customer pays → Invoice generated
```

#### Cancellation & Refund Policy

**Rules:**
- Cancellation >24h before appointment: 100% refund
- Cancellation 12-24h before: 50% refund
- Cancellation <12h before: No refund
- Provider cancellation: 100% refund + 10% compensation

**Refund Processing:**
```java
if (refundEligible) {
    double refundPercent = calculateRefundPercent(booking);
    double refundAmount = booking.getTotalAmount() * refundPercent;
    
    // Process refund via Razorpay
    razorpayService.processRefund(payment.getTransactionId(), refundAmount);
    
    // Update booking
    booking.setRefundStatus(RefundStatus.PROCESSED);
    booking.setRefundAmount(refundAmount);
    
    // Notify customer
    notificationService.sendRefundProcessed(booking);
}
```

### Invoice Generation

**Using Flying Saucer + Thymeleaf:**

```java
@Service
public class InvoiceService {
    
    @Autowired
    private TemplateEngine templateEngine;
    
    public byte[] generateInvoicePdf(Payment payment) {
        Context context = new Context();
        context.setVariable("payment", payment);
        context.setVariable("booking", payment.getBooking());
        context.setVariable("customer", payment.getCustomer());
        
        String html = templateEngine.process("invoice", context);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        
        return outputStream.toByteArray();
    }
}
```

**Invoice Template (Thymeleaf):**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <style>
        /* Professional invoice styling */
    </style>
</head>
<body>
    <div class="invoice">
        <div class="header">
            <h1>INVOICE</h1>
            <div>Invoice #: [[${payment.invoiceNumber}]]</div>
            <div>Date: [[${#temporals.format(payment.createdAt, 'dd-MM-yyyy')}]]</div>
        </div>
        
        <div class="details">
            <div class="customer">
                <h3>Bill To:</h3>
                <p>[[${customer.name}]]</p>
                <p>[[${customer.email}]]</p>
                <p>[[${customer.phone}]]</p>
            </div>
            
            <div class="provider">
                <h3>Service Provider:</h3>
                <p>[[${booking.provider.name}]]</p>
            </div>
        </div>
        
        <table class="items">
            <thead>
                <tr>
                    <th>Service</th>
                    <th>Patient</th>
                    <th>Date</th>
                    <th>Amount</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>[[${booking.service.name}]]</td>
                    <td>[[${booking.patient.name}]]</td>
                    <td>[[${booking.scheduledDate}]]</td>
                    <td>₹[[${booking.totalAmount}]]</td>
                </tr>
            </tbody>
            <tfoot>
                <tr>
                    <td colspan="3">Subtotal:</td>
                    <td>₹[[${booking.totalAmount}]]</td>
                </tr>
                <tr>
                    <td colspan="3">GST (18%):</td>
                    <td>₹[[${booking.totalAmount * 0.18}]]</td>
                </tr>
                <tr class="total">
                    <td colspan="3"><strong>Total:</strong></td>
                    <td><strong>₹[[${payment.amount}]]</strong></td>
                </tr>
            </tfoot>
        </table>
        
        <div class="footer">
            <p>Payment Method: [[${payment.paymentMethod}]]</p>
            <p>Transaction ID: [[${payment.transactionId}]]</p>
            <p>Status: [[${payment.paymentStatus}]]</p>
        </div>
    </div>
</body>
</html>
```

---

## 🎁 Referral Program Design

### How It Works

**1. Customer gets unique referral code:**
```
Format: LKO-[CUSTOMER_INITIALS]-[4_DIGIT_RANDOM]
Example: LKO-JD-8423
```

**2. Shares code with friends via:**
- Email (send invitation form)
- WhatsApp (share link)
- Copy & paste manually

**3. Friend signs up using referral code**
- Gets 10% discount on first booking
- Referrer gets ₹200 credit after friend completes first booking

**4. Rewards:**
```
Referrer Rewards:
- Friend signs up: ₹50 credit
- Friend completes 1st booking: ₹200 credit
- Friend completes 3rd booking: ₹100 bonus

Referee (Friend) Rewards:
- First booking: 10% discount (up to ₹500)
- Second booking: 5% discount
```

**5. Redemption:**
- Credits auto-applied to next booking
- Can be combined with other offers
- Valid for 6 months

### Implementation

```java
@Service
public class ReferralService {
    
    public String generateReferralCode(User user) {
        String initials = getInitials(user.getName());
        String random = RandomStringUtils.randomNumeric(4);
        return "LKO-" + initials + "-" + random;
    }
    
    public void applyReferral(User referee, String referralCode) {
        Referral referral = referralRepo.findByCode(referralCode)
            .orElseThrow(() -> new InvalidReferralException());
        
        // Update referral
        referral.setRefereeId(referee.getId());
        referral.setStatus(ReferralStatus.COMPLETED);
        
        // Give signup bonus to referrer
        creditService.addCredit(referral.getReferrerId(), 50.0, "Referral Signup Bonus");
        
        // Give discount to referee
        discountService.addDiscount(referee.getId(), 10.0, "FIRST_BOOKING_REFERRAL");
        
        // Send notifications
        notificationService.sendReferralSuccess(referral);
    }
    
    @EventListener
    public void onBookingCompleted(BookingCompletedEvent event) {
        // Check if this is referee's first booking
        if (isFirstBooking(event.getBooking())) {
            Referral referral = getReferralForUser(event.getCustomer());
            if (referral != null) {
                // Give referrer the main reward
                creditService.addCredit(referral.getReferrerId(), 200.0, 
                    "Referral - Friend Completed First Booking");
                referral.setRewardRedeemed(true);
            }
        }
    }
}
```

---

## ✅ Success Criteria

### Phase 1 Complete When:
- [ ] Customer can create/edit/delete patients
- [ ] Consent modals work and are recorded
- [ ] Booking flow requires patient selection
- [ ] Sidebar navigation implemented
- [ ] Role not visible in profile

### Phase 2 Complete When:
- [ ] Email notifications working for all events
- [ ] 24h reminders scheduled and delivered
- [ ] Booking status shows provider details
- [ ] Cancellation with policy works

### Phase 3 Complete When:
- [ ] Payment history displays correctly
- [ ] Invoices can be downloaded as PDF
- [ ] Payment gateway integration complete
- [ ] Refunds can be processed

### Phase 4 Complete When:
- [ ] Reviews can be submitted post-service
- [ ] Patient medications can be managed
- [ ] Documents can be uploaded & viewed
- [ ] Medical history displays correctly

### Phase 5 Complete When:
- [ ] Referral codes generated and shareable
- [ ] Referral tracking works
- [ ] Blogs display correctly
- [ ] FAQ page functional

### Phase 6 Complete When:
- [ ] SMS notifications delivered
- [ ] Notification preferences work

### Phase 7 Complete When:
- [ ] Security audit passed
- [ ] Performance benchmarks met
- [ ] Accessibility score >90
- [ ] All major browsers tested

---

## 🚀 Ready to Start?

This plan covers all discussed features in a structured manner. 

**Next Steps:**
1. Review and confirm the plan
2. Start with Phase 1 implementation
3. Set up project tracking (GitHub Projects/Jira)
4. Begin backend database migrations
5. Create patient management UI

**Questions to Finalize:**
1. Which payment gateway? (Razorpay recommended for India)
2. File storage for documents? (AWS S3 vs local storage)
3. SMS provider preference? (Twilio recommended)
4. Referral reward amounts - confirm values
5. Blog management - admin panel or separate CMS?

Ready to begin? 🎯

