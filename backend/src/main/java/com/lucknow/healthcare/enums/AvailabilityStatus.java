package com.lucknow.healthcare.enums;

/**
 * Availability status enumeration for healthcare providers
 * 
 * Defines the different availability states a provider can be in:
 * - AVAILABLE: Provider is available for new bookings
 * - BUSY: Provider is currently busy with existing bookings
 * - OFF_DUTY: Provider is off duty but may be available for emergencies
 * - ON_LEAVE: Provider is on leave and not available
 * 
 * @author Lucknow Healthcare Team
 * @version 1.0.0
 */
public enum AvailabilityStatus {
    AVAILABLE,
    BUSY,
    OFF_DUTY,
    ON_LEAVE
}
