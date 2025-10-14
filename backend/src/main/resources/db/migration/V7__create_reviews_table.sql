-- Create reviews table for customer feedback (Phase 1.5)
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider_id UUID NOT NULL REFERENCES providers(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_review_booking_id ON reviews(booking_id);
CREATE INDEX idx_review_user_id ON reviews(user_id);
CREATE INDEX idx_review_provider_id ON reviews(provider_id);
CREATE INDEX idx_review_rating ON reviews(rating);
CREATE INDEX idx_review_created_at ON reviews(created_at);

-- Create composite indexes for common queries
CREATE INDEX idx_review_provider_rating ON reviews(provider_id, rating);

-- Create unique constraint to ensure one review per booking
CREATE UNIQUE INDEX idx_review_booking_unique ON reviews(booking_id);
