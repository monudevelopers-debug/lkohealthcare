import apiClient from './client';
import type { Service, ServiceCategory } from '../../types/service.types';

export const servicesApi = {
  // Get all active service categories
  getCategories: async (): Promise<ServiceCategory[]> => {
    const response = await apiClient.get('/service-categories/active');
    return response.data;
  },

  // Get all active services
  getAllServices: async (): Promise<Service[]> => {
    const response = await apiClient.get('/services/active');
    return response.data;
  },

  // Get paginated services
  getServicesPaginated: async (page: number = 0, size: number = 20): Promise<{content: Service[], totalPages: number, totalElements: number}> => {
    const response = await apiClient.get(`/services/active/page?page=${page}&size=${size}`);
    return response.data;
  },

  // Get services by category
  getServicesByCategory: async (categoryId: string): Promise<Service[]> => {
    const response = await apiClient.get(`/services/active/category/${categoryId}`);
    return response.data;
  },

  // Search services by name
  searchServices: async (query: string): Promise<Service[]> => {
    const response = await apiClient.get(`/services/active/search?name=${encodeURIComponent(query)}`);
    return response.data;
  },

  // Get service by ID
  getServiceById: async (id: string): Promise<Service> => {
    const response = await apiClient.get(`/services/${id}`);
    return response.data;
  },
};
