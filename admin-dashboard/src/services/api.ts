import axios, { AxiosInstance, AxiosResponse } from 'axios';

// Types
export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: 'CUSTOMER' | 'PROVIDER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  createdAt: string;
  updatedAt: string;
}

export interface Service {
  id: string;
  name: string;
  description: string;
  price: number;
  duration: number;
  category: ServiceCategory;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ServiceCategory {
  id: string;
  name: string;
  description: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Provider {
  id: string;
  name: string;
  email: string;
  phone: string;
  qualifications: string;
  experience: number;
  rating: number;
  isAvailable: boolean;
  services: Service[];
  createdAt: string;
  updatedAt: string;
}

export interface Booking {
  id: string;
  user: User;
  service: Service;
  provider?: Provider;
  scheduledDate: string;
  scheduledTime: string;
  status: 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  totalAmount: number;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Payment {
  id: string;
  booking: Booking;
  amount: number;
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  method: 'CASH' | 'ONLINE' | 'CARD';
  transactionId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Review {
  id: string;
  user: User;
  provider: Provider;
  booking: Booking;
  rating: number;
  comment?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AdminStats {
  totalUsers: number;
  totalProviders: number;
  totalBookings: number;
  activeBookings: number;
  totalRevenue: number;
  usersChange: number;
  providersChange: number;
  bookingsChange: number;
  revenueChange: number;
  usersTrend: 'up' | 'down' | 'stable';
  providersTrend: 'up' | 'down' | 'stable';
  bookingsTrend: 'up' | 'down' | 'stable';
  revenueTrend: 'up' | 'down' | 'stable';
}

export interface SystemHealth {
  database: 'healthy' | 'unhealthy';
  redis: 'healthy' | 'unhealthy';
  email: 'healthy' | 'unhealthy';
  overall: 'healthy' | 'unhealthy';
  lastChecked: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
  expiresIn: number;
}

// API Client
class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Auth endpoints
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.client.post('/auth/login', credentials);
    return response.data;
  }

  async logout(): Promise<void> {
    await this.client.post('/auth/logout');
    localStorage.removeItem('authToken');
  }

  async getCurrentUser(): Promise<User> {
    const response: AxiosResponse<User> = await this.client.get('/auth/me');
    return response.data;
  }

  // User management
  async getUsers(page = 0, size = 20, search?: string): Promise<{ content: User[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(search && { search }),
    });
    const response = await this.client.get(`/users?${params}`);
    return response.data;
  }

  async getUserById(id: string): Promise<User> {
    const response: AxiosResponse<User> = await this.client.get(`/users/${id}`);
    return response.data;
  }

  async updateUserStatus(id: string, status: User['status']): Promise<User> {
    const response: AxiosResponse<User> = await this.client.patch(`/users/${id}/status`, { status });
    return response.data;
  }

  // Service management
  async getServices(page = 0, size = 20, categoryId?: string): Promise<{ content: Service[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(categoryId && { categoryId }),
    });
    const response = await this.client.get(`/services?${params}`);
    return response.data;
  }

  async getServiceById(id: string): Promise<Service> {
    const response: AxiosResponse<Service> = await this.client.get(`/services/${id}`);
    return response.data;
  }

  async createService(service: Omit<Service, 'id' | 'createdAt' | 'updatedAt'>): Promise<Service> {
    const response: AxiosResponse<Service> = await this.client.post('/services', service);
    return response.data;
  }

  async updateService(id: string, service: Partial<Service>): Promise<Service> {
    const response: AxiosResponse<Service> = await this.client.put(`/services/${id}`, service);
    return response.data;
  }

  async deleteService(id: string): Promise<void> {
    await this.client.delete(`/services/${id}`);
  }

  // Service categories
  async getServiceCategories(): Promise<ServiceCategory[]> {
    const response: AxiosResponse<ServiceCategory[]> = await this.client.get('/service-categories');
    return response.data;
  }

  async createServiceCategory(category: Omit<ServiceCategory, 'id' | 'createdAt' | 'updatedAt'>): Promise<ServiceCategory> {
    const response: AxiosResponse<ServiceCategory> = await this.client.post('/service-categories', category);
    return response.data;
  }

  // Provider management
  async getProviders(page = 0, size = 20, search?: string): Promise<{ content: Provider[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(search && { search }),
    });
    const response = await this.client.get(`/providers?${params}`);
    return response.data;
  }

  async getProviderById(id: string): Promise<Provider> {
    const response: AxiosResponse<Provider> = await this.client.get(`/providers/${id}`);
    return response.data;
  }

  async updateProviderStatus(id: string, isAvailable: boolean): Promise<Provider> {
    const response: AxiosResponse<Provider> = await this.client.patch(`/providers/${id}/availability`, { isAvailable });
    return response.data;
  }

  // Booking management
  async getBookings(page = 0, size = 20, status?: Booking['status']): Promise<{ content: Booking[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(status && { status }),
    });
    const response = await this.client.get(`/bookings?${params}`);
    return response.data;
  }

  async getBookingById(id: string): Promise<Booking> {
    const response: AxiosResponse<Booking> = await this.client.get(`/bookings/${id}`);
    return response.data;
  }

  async updateBookingStatus(id: string, status: Booking['status']): Promise<Booking> {
    const response: AxiosResponse<Booking> = await this.client.patch(`/bookings/${id}/status`, { status });
    return response.data;
  }

  async assignProvider(bookingId: string, providerId: string): Promise<Booking> {
    const response: AxiosResponse<Booking> = await this.client.patch(`/bookings/${bookingId}/assign`, { providerId });
    return response.data;
  }

  // Analytics and stats
  async getAdminStats(period: 'today' | 'week' | 'month' | 'year'): Promise<AdminStats> {
    const response: AxiosResponse<AdminStats> = await this.client.get(`/admin/stats?period=${period}`);
    return response.data;
  }

  async getSystemHealth(): Promise<SystemHealth> {
    const response: AxiosResponse<SystemHealth> = await this.client.get('/admin/health');
    return response.data;
  }

  // Payment management
  async getPayments(page = 0, size = 20, status?: Payment['status']): Promise<{ content: Payment[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(status && { status }),
    });
    const response = await this.client.get(`/payments?${params}`);
    return response.data;
  }

  async processRefund(paymentId: string, amount?: number): Promise<Payment> {
    const response: AxiosResponse<Payment> = await this.client.post(`/payments/${paymentId}/refund`, { amount });
    return response.data;
  }

  // Review management
  async getReviews(page = 0, size = 20, providerId?: string): Promise<{ content: Review[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(providerId && { providerId }),
    });
    const response = await this.client.get(`/reviews?${params}`);
    return response.data;
  }

  async deleteReview(id: string): Promise<void> {
    await this.client.delete(`/reviews/${id}`);
  }
}

// Create and export API client instance
const apiClient = new ApiClient();

// Export individual functions for easier use
export const {
  login,
  logout,
  getCurrentUser,
  getUsers,
  getUserById,
  updateUserStatus,
  getServices,
  getServiceById,
  createService,
  updateService,
  deleteService,
  getServiceCategories,
  createServiceCategory,
  getProviders,
  getProviderById,
  updateProviderStatus,
  getBookings,
  getBookingById,
  updateBookingStatus,
  assignProvider,
  getAdminStats,
  getSystemHealth,
  getPayments,
  processRefund,
  getReviews,
  deleteReview,
} = apiClient;

export default apiClient;
