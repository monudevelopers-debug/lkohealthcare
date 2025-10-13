package com.lucknow.healthcare.enums;

/**
 * Booking status enumeration for service bookings
 * 
 * Defines the different states a booking can be in:
 * - PENDING: Booking created but not yet confirmed
 * - CONFIRMED: Booking confirmed and payment processed
 * - IN_PROGRESS: Service is currently being provided
 * - COMPLETED: Service has been completed successfully
 * - CANCELLED: Booking was cancelled
 * - RESCHEDULED: Booking was rescheduled to a different time
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    RESCHEDULED
}
