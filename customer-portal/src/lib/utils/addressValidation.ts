export interface AddressValidationResult {
  valid: boolean;
  error?: string;
}

export function validateLucknowAddress(address: string): AddressValidationResult {
  if (!address || address.trim().length < 10) {
    return {
      valid: false,
      error: "Please enter a complete address with at least 10 characters."
    };
  }

  const addressLower = address.toLowerCase();
  const lucknowKeywords = ['lucknow', 'lko', '226'];
  
  const isLucknow = lucknowKeywords.some(keyword => addressLower.includes(keyword));
  
  if (!isLucknow) {
    return {
      valid: false,
      error: "Sorry, we only serve in Lucknow area. Please enter a valid Lucknow address with pin code (226xxx)."
    };
  }
  
  return { valid: true };
}

export function formatAddress(address: string): string {
  return address.trim().replace(/\s+/g, ' ');
}
