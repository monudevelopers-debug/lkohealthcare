-- V13: Create consent_records table and update bookings table for patient integration
-- Part 1: Consent Records for HIPAA-like compliance

CREATE TABLE consent_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    patient_id UUID, -- NULL for user-level consents (T&C, Privacy), populated for patient-specific consents
    consent_type VARCHAR(50) NOT NULL CHECK (consent_type IN (
        'TERMS_AND_CONDITIONS', 
        'PRIVACY_POLICY', 
        'MEDICAL_DATA_SHARING', 
        'HIPAA_COMPLIANCE',
        'EMERGENCY_TREATMENT',
        'DATA_RETENTION'
    )),
    consent_version VARCHAR(20) NOT NULL, -- Track version changes (e.g., "1.0", "2.1")
    is_accepted BOOLEAN NOT NULL DEFAULT FALSE,
    accepted_at TIMESTAMP,
    ip_address VARCHAR(45), -- Support IPv4 and IPv6
    user_agent TEXT, -- Browser/device information
    revoked_at TIMESTAMP,
    revocation_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_consent_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_consent_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Indexes for consent_records
CREATE INDEX idx_consent_records_user_id ON consent_records(user_id);
CREATE INDEX idx_consent_records_patient_id ON consent_records(patient_id);
CREATE INDEX idx_consent_records_is_accepted ON consent_records(is_accepted);
CREATE INDEX idx_consent_records_consent_type ON consent_records(consent_type);
CREATE UNIQUE INDEX idx_consent_records_unique ON consent_records(user_id, consent_type, consent_version) WHERE patient_id IS NULL;
CREATE UNIQUE INDEX idx_consent_records_patient_unique ON consent_records(user_id, patient_id, consent_type, consent_version) WHERE patient_id IS NOT NULL;

-- Part 2: Update bookings table to add patient relationship and payment details

-- Add patient_id to bookings
ALTER TABLE bookings 
ADD COLUMN patient_id UUID,
ADD CONSTRAINT fk_booking_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE SET NULL;

-- Add payment-related fields
ALTER TABLE bookings 
ADD COLUMN estimated_arrival_time TIMESTAMP,
ADD COLUMN payment_method VARCHAR(50) CHECK (payment_method IN ('ONLINE', 'CASH', 'UPI', 'CARD', 'WALLET')),
ADD COLUMN payment_timing VARCHAR(20) DEFAULT 'ADVANCE' CHECK (payment_timing IN ('ADVANCE', 'POST_SERVICE')),
ADD COLUMN payment_transaction_id VARCHAR(255),
ADD COLUMN cancellation_reason TEXT,
ADD COLUMN cancelled_by VARCHAR(20) CHECK (cancelled_by IN ('CUSTOMER', 'PROVIDER', 'ADMIN', 'SYSTEM')),
ADD COLUMN cancelled_at TIMESTAMP,
ADD COLUMN refund_status VARCHAR(20) DEFAULT 'NOT_APPLICABLE' CHECK (refund_status IN ('NOT_APPLICABLE', 'PENDING', 'PROCESSING', 'COMPLETED', 'REJECTED')),
ADD COLUMN refund_amount DECIMAL(10,2),
ADD COLUMN refund_processed_at TIMESTAMP,
ADD COLUMN refund_transaction_id VARCHAR(255),
ADD COLUMN actual_start_time TIMESTAMP,
ADD COLUMN actual_end_time TIMESTAMP,
ADD COLUMN actual_duration INTEGER; -- in minutes

-- Add indexes for new booking fields
CREATE INDEX idx_bookings_patient_id ON bookings(patient_id);
CREATE INDEX idx_bookings_payment_method ON bookings(payment_method);
CREATE INDEX idx_bookings_payment_timing ON bookings(payment_timing);
CREATE INDEX idx_bookings_refund_status ON bookings(refund_status);
CREATE INDEX idx_bookings_cancelled_at ON bookings(cancelled_at);
CREATE INDEX idx_bookings_estimated_arrival ON bookings(estimated_arrival_time);

-- Part 3: Create payments table for comprehensive payment tracking

CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    payment_method VARCHAR(50) NOT NULL,
    payment_gateway VARCHAR(50), -- DUMMY, RAZORPAY, STRIPE, etc.
    transaction_id VARCHAR(255),
    gateway_order_id VARCHAR(255), -- Razorpay order_id, Stripe payment_intent
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN (
        'PENDING', 'PROCESSING', 'SUCCESS', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED'
    )),
    payment_timing VARCHAR(20) NOT NULL CHECK (payment_timing IN ('ADVANCE', 'POST_SERVICE')),
    invoice_number VARCHAR(50) UNIQUE,
    invoice_url TEXT,
    paid_at TIMESTAMP,
    failure_reason TEXT,
    gateway_response TEXT, -- Store full gateway response for debugging
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for payments
CREATE INDEX idx_payments_booking_id ON payments(booking_id);
CREATE INDEX idx_payments_customer_id ON payments(customer_id);
CREATE INDEX idx_payments_payment_status ON payments(payment_status);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payments_invoice_number ON payments(invoice_number);
CREATE INDEX idx_payments_created_at ON payments(created_at);
CREATE INDEX idx_payments_customer_status ON payments(customer_id, payment_status);

-- Comments for documentation
COMMENT ON TABLE consent_records IS 'Tracks user consents for legal compliance (HIPAA-like, GDPR, etc.)';
COMMENT ON COLUMN consent_records.consent_version IS 'Version number to track when T&C/policies are updated';
COMMENT ON COLUMN consent_records.ip_address IS 'IP address from which consent was given for audit trail';
COMMENT ON COLUMN consent_records.revoked_at IS 'Timestamp when consent was revoked by user';

COMMENT ON TABLE payments IS 'Comprehensive payment tracking with gateway integration support';
COMMENT ON COLUMN payments.gateway_order_id IS 'Payment gateway specific order/intent ID';
COMMENT ON COLUMN payments.invoice_number IS 'Unique invoice number generated for the transaction';
COMMENT ON COLUMN payments.gateway_response IS 'Full JSON response from payment gateway for debugging';

COMMENT ON COLUMN bookings.patient_id IS 'The patient who will receive the healthcare service';
COMMENT ON COLUMN bookings.payment_transaction_id IS 'Quick reference to payment transaction ID';
COMMENT ON COLUMN bookings.estimated_arrival_time IS 'When provider is expected to arrive at customer location';
COMMENT ON COLUMN bookings.actual_start_time IS 'When service actually started (may differ from scheduled)';
COMMENT ON COLUMN bookings.actual_duration IS 'Actual service duration in minutes (for billing adjustments)';

