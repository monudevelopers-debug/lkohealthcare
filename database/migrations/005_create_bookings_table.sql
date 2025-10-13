-- Create bookings table for service bookings
CREATE TABLE bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    service_id UUID NOT NULL REFERENCES services(id) ON DELETE RESTRICT,
    provider_id UUID REFERENCES providers(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'RESCHEDULED')),
    scheduled_date DATE NOT NULL,
    scheduled_time TIME NOT NULL,
    duration INTEGER NOT NULL, -- Duration in hours
    total_amount DECIMAL(10,2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED', 'PARTIAL_REFUND')),
    special_instructions TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_booking_user_id ON bookings(user_id);
CREATE INDEX idx_booking_service_id ON bookings(service_id);
CREATE INDEX idx_booking_provider_id ON bookings(provider_id);
CREATE INDEX idx_booking_status ON bookings(status);
CREATE INDEX idx_booking_scheduled_date ON bookings(scheduled_date);
CREATE INDEX idx_booking_payment_status ON bookings(payment_status);
CREATE INDEX idx_booking_created_at ON bookings(created_at);

-- Create composite indexes for common queries
CREATE INDEX idx_booking_user_status ON bookings(user_id, status);
CREATE INDEX idx_booking_provider_status ON bookings(provider_id, status);
CREATE INDEX idx_booking_date_status ON bookings(scheduled_date, status);

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_bookings_updated_at 
    BEFORE UPDATE ON bookings 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();
