package com.lucknow.healthcare.enums;

/**
 * Payment method enumeration for booking payments
 * 
 * Defines the different payment methods available:
 * - ONLINE: Online payment through gateway (Razorpay/Stripe)
 * - CASH: Cash payment on service delivery
 * - CARD: Card payment (credit/debit)
 * - UPI: UPI payment method
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public enum PaymentMethod {
    ONLINE,
    CASH,
    CARD,
    UPI
}
