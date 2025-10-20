import type { Service } from './service.types';
import type { User } from './auth.types';

export interface Booking {
  id: string;
  user: User;
  service: Service;
  scheduledDate: string; // YYYY-MM-DD
  scheduledTime: string; // HH:MM:SS
  duration: number; // in hours
  specialInstructions?: string; // Contains address and notes
  notes?: string;
  status: 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  totalAmount: number;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  createdAt: string;
  updatedAt: string;
}

export interface CreateBookingRequest {
  userId: string;
  serviceId: string;
  scheduledDate: string; // YYYY-MM-DD
  scheduledTime: string; // HH:MM:SS
  duration: number; // in hours
  totalAmount: number;
  specialInstructions?: string; // Will include address + notes
}
