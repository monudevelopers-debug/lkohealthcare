import apiClient from './client';
import type { Booking, CreateBookingRequest } from '../../types/booking.types';

export const bookingsApi = {
  // Create new booking
  createBooking: async (data: CreateBookingRequest): Promise<Booking> => {
    // Format the request to match backend Booking entity
    const bookingRequest = {
      user: { id: data.userId },
      service: { id: data.serviceId },
      scheduledDate: data.scheduledDate,
      scheduledTime: data.scheduledTime,
      duration: data.duration,
      totalAmount: data.totalAmount,
      specialInstructions: data.specialInstructions,
      status: 'PENDING',
      paymentStatus: 'PENDING',
    };
    
    const response = await apiClient.post('/bookings', bookingRequest);
    return response.data;
  },

  // Get user's bookings
  getUserBookings: async (userId: string): Promise<Booking[]> => {
    const response = await apiClient.get(`/bookings/user/${userId}`);
    return response.data;
  },

  // Get booking by ID
  getBookingById: async (id: string): Promise<Booking> => {
    const response = await apiClient.get(`/bookings/${id}`);
    return response.data;
  },

  // Cancel booking
  cancelBooking: async (id: string): Promise<Booking> => {
    const response = await apiClient.put(`/bookings/${id}/cancel`);
    return response.data;
  },
};
