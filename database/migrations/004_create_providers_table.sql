-- Create providers table for healthcare service providers
CREATE TABLE providers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    qualification VARCHAR(255) NOT NULL,
    experience INTEGER NOT NULL DEFAULT 0, -- Experience in years
    availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE' CHECK (availability_status IN ('AVAILABLE', 'BUSY', 'OFF_DUTY', 'ON_LEAVE')),
    rating DECIMAL(3,2) NOT NULL DEFAULT 0.00 CHECK (rating >= 0.00 AND rating <= 5.00),
    total_ratings INTEGER NOT NULL DEFAULT 0,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    documents JSONB, -- Store verification documents
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_provider_email ON providers(email);
CREATE INDEX idx_provider_availability ON providers(availability_status);
CREATE INDEX idx_provider_rating ON providers(rating);
CREATE INDEX idx_provider_verified ON providers(is_verified);
CREATE INDEX idx_provider_experience ON providers(experience);

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_providers_updated_at 
    BEFORE UPDATE ON providers 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
