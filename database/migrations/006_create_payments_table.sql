-- Create payments table for payment records (Phase 1.5)
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(20) NOT NULL CHECK (method IN ('ONLINE', 'CASH', 'CARD', 'UPI')),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED', 'PARTIAL_REFUND')),
    transaction_id VARCHAR(255),
    payment_gateway VARCHAR(50),
    gateway_response JSONB, -- Store gateway response data
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_payment_booking_id ON payments(booking_id);
CREATE INDEX idx_payment_status ON payments(status);
CREATE INDEX idx_payment_transaction_id ON payments(transaction_id);
CREATE INDEX idx_payment_gateway ON payments(payment_gateway);
CREATE INDEX idx_payment_created_at ON payments(created_at);
CREATE INDEX idx_payment_paid_at ON payments(paid_at);

-- Create unique index for transaction_id to prevent duplicates
CREATE UNIQUE INDEX idx_payment_transaction_unique ON payments(transaction_id) WHERE transaction_id IS NOT NULL;
