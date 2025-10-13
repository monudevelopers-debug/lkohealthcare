package com.lucknow.healthcare.enums;

/**
 * Payment status enumeration for booking payments
 * 
 * Defines the different states a payment can be in:
 * - PENDING: Payment initiated but not yet processed
 * - PAID: Payment successfully processed
 * - FAILED: Payment processing failed
 * - REFUNDED: Payment was refunded
 * - PARTIAL_REFUND: Partial refund was processed
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED,
    PARTIAL_REFUND
}
