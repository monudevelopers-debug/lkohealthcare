/**
 * Privacy utilities for customer contact protection
 * 
 * Providers can only see customer contact details (phone, address, emergency contact)
 * within 24 hours before and after the service date.
 * 
 * Admins and customers themselves always have full access.
 */

export interface PrivacyCheckResult {
  contactDetailsVisible: boolean;
  privacyMessage: string;
  isWithinWindow: boolean;
}

/**
 * Check if customer contact details should be visible based on service date and user role
 * 
 * @param serviceDate - The date of the service (YYYY-MM-DD format)
 * @param userRole - The role of the current user ('PROVIDER', 'ADMIN', 'CUSTOMER')
 * @returns PrivacyCheckResult with visibility status and message
 */
export function checkContactDetailsPrivacy(
  serviceDate: string, 
  userRole: string
): PrivacyCheckResult {
  // Admins and customers always have full access
  if (userRole === 'ADMIN' || userRole === 'CUSTOMER') {
    return {
      contactDetailsVisible: true,
      privacyMessage: 'Contact details available',
      isWithinWindow: true
    };
  }

  // For providers, check the 24-hour window
  if (userRole === 'PROVIDER') {
    const today = new Date();
    const service = new Date(serviceDate);
    
    // Calculate 24 hours before and after service date
    const dayBefore = new Date(service);
    dayBefore.setDate(dayBefore.getDate() - 1);
    
    const dayAfter = new Date(service);
    dayAfter.setDate(dayAfter.getDate() + 1);
    
    // Check if current date is within the privacy window
    const isWithinWindow = today >= dayBefore && today <= dayAfter;
    
    if (isWithinWindow) {
      return {
        contactDetailsVisible: true,
        privacyMessage: `Contact details available for service on ${serviceDate}`,
        isWithinWindow: true
      };
    } else {
      // Outside privacy window
      if (today < dayBefore) {
        return {
          contactDetailsVisible: false,
          privacyMessage: 'Contact details will be available 24 hours before service',
          isWithinWindow: false
        };
      } else {
        return {
          contactDetailsVisible: false,
          privacyMessage: 'Contact details are no longer available (expired 24 hours after service)',
          isWithinWindow: false
        };
      }
    }
  }

  // Default case - hide details
  return {
    contactDetailsVisible: false,
    privacyMessage: 'Contact details not available',
    isWithinWindow: false
  };
}

/**
 * Format a phone number for display (with privacy protection)
 * 
 * @param phone - The phone number to format
 * @param isVisible - Whether the phone number should be visible
 * @returns Formatted phone number or privacy message
 */
export function formatPhoneNumber(phone: string | undefined, isVisible: boolean): string {
  if (!phone) return 'Phone number not available';
  
  if (isVisible) {
    return phone;
  } else {
    return 'Phone number protected';
  }
}

/**
 * Format an address for display (with privacy protection)
 * 
 * @param address - The address to format
 * @param isVisible - Whether the address should be visible
 * @returns Formatted address or privacy message
 */
export function formatAddress(address: string | undefined, isVisible: boolean): string {
  if (!address) return 'Address not available';
  
  if (isVisible) {
    return address;
  } else {
    return 'Address protected';
  }
}

/**
 * Get privacy status for a booking
 * 
 * @param booking - The booking object
 * @param userRole - The role of the current user
 * @returns PrivacyCheckResult for the booking
 */
export function getBookingPrivacyStatus(booking: any, userRole: string): PrivacyCheckResult {
  return checkContactDetailsPrivacy(booking.scheduledDate, userRole);
}
