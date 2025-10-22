import type { Service } from './service.types';
import type { User } from './auth.types';
import type { Patient } from './patient.types';

export interface Provider {
  id: string;
  name: string;
  email: string;
  phone: string;
  qualifications: string;
  experience: number;
  rating: number;
  isAvailable: boolean;
}

export interface Review {
  id: string;
  rating: number;
  comment: string;
  createdAt: string;
  updatedAt: string;
}

export interface Booking {
  id: string;
  user: User;
  service: Service;
  provider?: Provider;
  patient?: Patient;
  scheduledDate: string; // YYYY-MM-DD
  scheduledTime: string; // HH:MM:SS
  duration: number; // in hours
  specialInstructions?: string; // Contains address and notes
  notes?: string;
  status: 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  totalAmount: number;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  review?: Review; // Review for this booking (if exists)
  createdAt: string;
  updatedAt: string;
}

export interface CreateBookingRequest {
  userId: string;
  serviceId: string;
  patientId?: string; // Patient for whom the service is booked
  scheduledDate: string; // YYYY-MM-DD
  scheduledTime: string; // HH:MM:SS
  duration: number; // in hours
  totalAmount: number;
  specialInstructions?: string; // Will include address + notes
}
