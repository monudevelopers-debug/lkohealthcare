-- V15: Upgrade payments table from V6 to support customer portal requirements
-- This migration safely transforms the existing payments table structure

-- Step 1: Rename columns to match entity naming
DO $$ 
BEGIN
    -- Rename 'method' to 'payment_method'
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='payments' AND column_name='method') THEN
        ALTER TABLE payments RENAME COLUMN method TO payment_method;
    END IF;
    
    -- Rename 'status' to 'payment_status'
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name='payments' AND column_name='status') THEN
        ALTER TABLE payments RENAME COLUMN status TO payment_status;
    END IF;
END $$;

-- Step 2: Add new required columns
ALTER TABLE payments
    ADD COLUMN IF NOT EXISTS customer_id UUID,
    ADD COLUMN IF NOT EXISTS gateway_order_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS payment_timing VARCHAR(20) DEFAULT 'POST_SERVICE' NOT NULL,
    ADD COLUMN IF NOT EXISTS invoice_number VARCHAR(50) UNIQUE,
    ADD COLUMN IF NOT EXISTS invoice_url TEXT,
    ADD COLUMN IF NOT EXISTS failure_reason TEXT,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Step 3: Populate customer_id from bookings (data migration)
-- Assume all existing payments belong to the customer who made the booking
UPDATE payments p
SET customer_id = b.user_id
FROM bookings b
WHERE p.booking_id = b.id
  AND p.customer_id IS NULL;

-- Step 4: Make customer_id NOT NULL now that it's populated
ALTER TABLE payments
    ALTER COLUMN customer_id SET NOT NULL;

-- Step 5: Add foreign key constraint (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='fk_payment_customer') THEN
        ALTER TABLE payments ADD CONSTRAINT fk_payment_customer 
            FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE;
    END IF;
END $$;

-- Step 6: Add check constraint for payment_timing (if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name='chk_payment_timing') THEN
        ALTER TABLE payments ADD CONSTRAINT chk_payment_timing 
            CHECK (payment_timing IN ('ADVANCE', 'POST_SERVICE'));
    END IF;
END $$;

-- Step 7: Update existing CHECK constraints to include new enum values
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_payment_method_check;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_method_check;
ALTER TABLE payments
    ADD CONSTRAINT chk_payment_method 
    CHECK (payment_method IN ('ONLINE', 'CASH', 'CARD', 'UPI', 'WALLET', 'NET_BANKING'));

ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_payment_status_check;
ALTER TABLE payments DROP CONSTRAINT IF EXISTS payments_status_check;
ALTER TABLE payments
    ADD CONSTRAINT chk_payment_status 
    CHECK (payment_status IN ('PENDING', 'PROCESSING', 'SUCCESS', 'PAID', 'FAILED', 'REFUNDED', 'PARTIAL_REFUND', 'PARTIALLY_REFUNDED'));

-- Step 8: Create new indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_payments_customer_id ON payments(customer_id);
CREATE INDEX IF NOT EXISTS idx_payments_gateway_order_id ON payments(gateway_order_id);
CREATE INDEX IF NOT EXISTS idx_payments_invoice_number ON payments(invoice_number) WHERE invoice_number IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_payments_customer_status ON payments(customer_id, payment_status);
CREATE INDEX IF NOT EXISTS idx_payments_updated_at ON payments(updated_at);

-- Step 9: Rename existing indexes to match new naming convention
DO $$
BEGIN
    -- Rename status index if it exists
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_payment_status') THEN
        ALTER INDEX idx_payment_status RENAME TO idx_payments_payment_status;
    END IF;
END $$;

-- Step 10: Add column comments for documentation
COMMENT ON COLUMN payments.customer_id IS 'Reference to the customer who made the payment';
COMMENT ON COLUMN payments.payment_timing IS 'When payment is collected: ADVANCE (before service) or POST_SERVICE (after completion)';
COMMENT ON COLUMN payments.invoice_number IS 'Unique invoice identifier for successful payments';
COMMENT ON COLUMN payments.gateway_response IS 'Full JSON response from payment gateway (JSONB for queryability)';
COMMENT ON COLUMN payments.failure_reason IS 'Reason for payment failure if applicable';

-- Step 11: Update table comment
COMMENT ON TABLE payments IS 'Payment transactions for healthcare service bookings with gateway integration';

