import apiClient from './client';
import type { User } from '../../types/auth.types';

export const usersApi = {
  // Get user by ID
  getUserById: async (id: string): Promise<User> => {
    const response = await apiClient.get(`/users/${id}`);
    return response.data;
  },

  // Update user profile
  updateUser: async (id: string, data: Partial<User>): Promise<User> => {
    const response = await apiClient.put(`/users/${id}`, data);
    return response.data;
  },

  // Change password
  changePassword: async (id: string, newPassword: string): Promise<void> => {
    await apiClient.put(`/users/${id}/password?newPassword=${encodeURIComponent(newPassword)}`);
  },
};
