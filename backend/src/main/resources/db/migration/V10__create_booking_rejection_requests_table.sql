-- Migration V10: Create booking_rejection_requests table
-- Enables provider booking rejection workflow with admin approval

CREATE TABLE IF NOT EXISTS booking_rejection_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL,
    provider_id UUID NOT NULL,
    rejection_reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_brr_booking 
        FOREIGN KEY (booking_id) 
        REFERENCES bookings(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_brr_provider 
        FOREIGN KEY (provider_id) 
        REFERENCES providers(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_brr_reviewer 
        FOREIGN KEY (reviewed_by) 
        REFERENCES users(id) 
        ON DELETE SET NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_brr_booking ON booking_rejection_requests(booking_id);
CREATE INDEX idx_brr_provider ON booking_rejection_requests(provider_id);
CREATE INDEX idx_brr_status ON booking_rejection_requests(status);
CREATE INDEX idx_brr_requested_at ON booking_rejection_requests(requested_at DESC);

-- Unique constraint to prevent duplicate pending requests for same booking
CREATE UNIQUE INDEX idx_brr_unique_pending 
ON booking_rejection_requests(booking_id) 
WHERE status = 'PENDING';

-- Add comments for documentation
COMMENT ON TABLE booking_rejection_requests IS 'Stores provider booking rejection requests requiring admin approval for customer protection';
COMMENT ON COLUMN booking_rejection_requests.rejection_reason IS 'Provider must explain why they cannot fulfill this booking';
COMMENT ON COLUMN booking_rejection_requests.status IS 'PENDING - Awaiting admin review, APPROVED - Admin allows rejection, REJECTED - Provider must complete booking';

