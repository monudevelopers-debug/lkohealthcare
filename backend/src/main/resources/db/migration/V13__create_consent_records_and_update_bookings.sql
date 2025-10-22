-- V13: Create consent_records table and update bookings table for patient integration
-- Part 1: Consent Records for HIPAA-like compliance

CREATE TABLE IF NOT EXISTS consent_records (
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
CREATE INDEX IF NOT EXISTS idx_consent_records_user_id ON consent_records(user_id);
CREATE INDEX IF NOT EXISTS idx_consent_records_patient_id ON consent_records(patient_id);
CREATE INDEX IF NOT EXISTS idx_consent_records_is_accepted ON consent_records(is_accepted);
CREATE INDEX IF NOT EXISTS idx_consent_records_consent_type ON consent_records(consent_type);
CREATE UNIQUE INDEX IF NOT EXISTS idx_consent_records_unique ON consent_records(user_id, consent_type, consent_version) WHERE patient_id IS NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_consent_records_patient_unique ON consent_records(user_id, patient_id, consent_type, consent_version) WHERE patient_id IS NOT NULL;

-- Part 2: Update bookings table to add patient relationship and payment details

-- Add columns to bookings table (skip if already exists)
ALTER TABLE bookings 
ADD COLUMN IF NOT EXISTS patient_id UUID,
ADD COLUMN IF NOT EXISTS estimated_arrival_time TIMESTAMP,
ADD COLUMN IF NOT EXISTS payment_method VARCHAR(50),
ADD COLUMN IF NOT EXISTS payment_timing VARCHAR(20) DEFAULT 'ADVANCE',
ADD COLUMN IF NOT EXISTS payment_transaction_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS cancellation_reason TEXT,
ADD COLUMN IF NOT EXISTS cancelled_by VARCHAR(20),
ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS refund_status VARCHAR(20) DEFAULT 'NOT_APPLICABLE',
ADD COLUMN IF NOT EXISTS refund_amount DECIMAL(10,2),
ADD COLUMN IF NOT EXISTS refund_processed_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS refund_transaction_id VARCHAR(255),
ADD COLUMN IF NOT EXISTS actual_start_time TIMESTAMP,
ADD COLUMN IF NOT EXISTS actual_end_time TIMESTAMP,
ADD COLUMN IF NOT EXISTS actual_duration INTEGER;

-- Add foreign key constraint
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='fk_booking_patient') THEN
        ALTER TABLE bookings ADD CONSTRAINT fk_booking_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE SET NULL;
    END IF;
END $$;

-- Add indexes for new booking fields
CREATE INDEX IF NOT EXISTS idx_bookings_patient_id ON bookings(patient_id);
CREATE INDEX IF NOT EXISTS idx_bookings_payment_method ON bookings(payment_method);
CREATE INDEX IF NOT EXISTS idx_bookings_payment_timing ON bookings(payment_timing);
CREATE INDEX IF NOT EXISTS idx_bookings_refund_status ON bookings(refund_status);
CREATE INDEX IF NOT EXISTS idx_bookings_cancelled_at ON bookings(cancelled_at);
CREATE INDEX IF NOT EXISTS idx_bookings_estimated_arrival ON bookings(estimated_arrival_time);

-- Part 3: Payments table updates are handled in V15
-- (Payments table already exists from V6, will be updated in V15)

-- Comments for documentation
COMMENT ON TABLE consent_records IS 'Tracks user consents for legal compliance (HIPAA-like, GDPR, etc.)';
COMMENT ON COLUMN consent_records.consent_version IS 'Version number to track when T&C/policies are updated';
COMMENT ON COLUMN consent_records.ip_address IS 'IP address from which consent was given for audit trail';
COMMENT ON COLUMN consent_records.revoked_at IS 'Timestamp when consent was revoked by user';

COMMENT ON COLUMN bookings.patient_id IS 'The patient who will receive the healthcare service';
COMMENT ON COLUMN bookings.payment_transaction_id IS 'Quick reference to payment transaction ID';
COMMENT ON COLUMN bookings.estimated_arrival_time IS 'When provider is expected to arrive at customer location';
COMMENT ON COLUMN bookings.actual_start_time IS 'When service actually started (may differ from scheduled)';
COMMENT ON COLUMN bookings.actual_duration IS 'Actual service duration in minutes (for billing adjustments)';

