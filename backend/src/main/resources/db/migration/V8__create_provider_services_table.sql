-- Migration V8: Create provider_services join table for many-to-many relationship
-- This enables providers to be tagged with services they can offer

CREATE TABLE IF NOT EXISTS provider_services (
    provider_id UUID NOT NULL,
    service_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (provider_id, service_id),
    CONSTRAINT fk_provider_services_provider 
        FOREIGN KEY (provider_id) 
        REFERENCES providers(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_provider_services_service 
        FOREIGN KEY (service_id) 
        REFERENCES services(id) 
        ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_provider_services_provider ON provider_services(provider_id);
CREATE INDEX idx_provider_services_service ON provider_services(service_id);

-- Seed initial data: Assign ALL services to ALL existing providers
-- This ensures backward compatibility - existing system continues to work
INSERT INTO provider_services (provider_id, service_id)
SELECT p.id, s.id
FROM providers p
CROSS JOIN services s
ON CONFLICT (provider_id, service_id) DO NOTHING;

-- Add comment for documentation
COMMENT ON TABLE provider_services IS 'Many-to-many relationship between providers and services they can offer';

