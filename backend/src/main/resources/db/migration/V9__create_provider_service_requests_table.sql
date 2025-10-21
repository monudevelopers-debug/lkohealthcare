-- Migration V9: Create provider_service_requests table for approval workflow
-- This enables safe provider service management with admin approval

CREATE TABLE IF NOT EXISTS provider_service_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id UUID NOT NULL,
    service_id UUID NOT NULL,
    request_type VARCHAR(20) NOT NULL CHECK (request_type IN ('ADD', 'REMOVE')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    requested_by VARCHAR(10) NOT NULL CHECK (requested_by IN ('PROVIDER', 'ADMIN')),
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    rejection_reason TEXT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_psr_provider 
        FOREIGN KEY (provider_id) 
        REFERENCES providers(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_psr_service 
        FOREIGN KEY (service_id) 
        REFERENCES services(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_psr_reviewer 
        FOREIGN KEY (reviewed_by) 
        REFERENCES users(id) 
        ON DELETE SET NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_psr_provider ON provider_service_requests(provider_id);
CREATE INDEX idx_psr_service ON provider_service_requests(service_id);
CREATE INDEX idx_psr_status ON provider_service_requests(status);
CREATE INDEX idx_psr_requested_by ON provider_service_requests(requested_by);
CREATE INDEX idx_psr_requested_at ON provider_service_requests(requested_at DESC);

-- Create unique constraint to prevent duplicate pending requests
CREATE UNIQUE INDEX idx_psr_unique_pending 
ON provider_service_requests(provider_id, service_id, request_type) 
WHERE status = 'PENDING';

-- Add comments for documentation
COMMENT ON TABLE provider_service_requests IS 'Stores service addition/removal requests from providers requiring admin approval';
COMMENT ON COLUMN provider_service_requests.request_type IS 'ADD - Provider requests to add service, REMOVE - Provider requests to remove service';
COMMENT ON COLUMN provider_service_requests.status IS 'PENDING - Awaiting review, APPROVED - Accepted by admin, REJECTED - Denied by admin';
COMMENT ON COLUMN provider_service_requests.requested_by IS 'PROVIDER - Self-service request (needs approval), ADMIN - Direct assignment (pre-approved)';

