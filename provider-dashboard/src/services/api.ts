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

export interface ProviderStats {
  todayBookings: number;
  activeBookings: number;
  totalEarnings: number;
  rating: number;
  todayBookingsChange: number;
  activeBookingsChange: number;
  earningsChange: number;
  ratingChange: number;
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

  // Provider profile management
  async getProviderProfile(): Promise<Provider> {
    const response: AxiosResponse<Provider> = await this.client.get('/providers/profile');
    return response.data;
  }

  async updateProviderProfile(profile: Partial<Provider>): Promise<Provider> {
    const response: AxiosResponse<Provider> = await this.client.put('/providers/profile', profile);
    return response.data;
  }

  async updateAvailability(isAvailable: boolean): Promise<Provider> {
    const response: AxiosResponse<Provider> = await this.client.patch('/providers/availability', { isAvailable });
    return response.data;
  }

  // Booking management
  async getMyBookings(page = 0, size = 20, status?: Booking['status']): Promise<{ content: Booking[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      ...(status && { status }),
    });
    const response = await this.client.get(`/providers/bookings?${params}`);
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

  async acceptBooking(id: string): Promise<Booking> {
    const response: AxiosResponse<Booking> = await this.client.post(`/bookings/${id}/accept`);
    return response.data;
  }

  async rejectBooking(id: string, reason?: string): Promise<Booking> {
    const response: AxiosResponse<Booking> = await this.client.post(`/bookings/${id}/reject`, { reason });
    return response.data;
  }

  async startService(id: string): Promise<Booking> {
    const response: AxiosResponse<Booking> = await this.client.post(`/bookings/${id}/start`);
    return response.data;
  }

  async completeService(id: string, notes?: string): Promise<Booking> {
    const response: AxiosResponse<Booking> = await this.client.post(`/bookings/${id}/complete`, { notes });
    return response.data;
  }

  // Analytics and stats
  async getProviderStats(period: 'today' | 'week' | 'month'): Promise<ProviderStats> {
    const response: AxiosResponse<ProviderStats> = await this.client.get(`/providers/stats?period=${period}`);
    return response.data;
  }

  async getRecentBookings(limit = 10): Promise<Booking[]> {
    const response: AxiosResponse<Booking[]> = await this.client.get(`/providers/recent-bookings?limit=${limit}`);
    return response.data;
  }

  // Earnings management
  async getEarnings(period: 'today' | 'week' | 'month' | 'year'): Promise<{ total: number; breakdown: any[] }> {
    const response = await this.client.get(`/providers/earnings?period=${period}`);
    return response.data;
  }

  async getPaymentHistory(page = 0, size = 20): Promise<{ content: Payment[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    const response = await this.client.get(`/providers/payments?${params}`);
    return response.data;
  }

  // Reviews management
  async getMyReviews(page = 0, size = 20): Promise<{ content: Review[]; totalElements: number }> {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });
    const response = await this.client.get(`/providers/reviews?${params}`);
    return response.data;
  }

  async getReviewById(id: string): Promise<Review> {
    const response: AxiosResponse<Review> = await this.client.get(`/reviews/${id}`);
    return response.data;
  }

  // Service management (for providers to see available services)
  async getAvailableServices(): Promise<Service[]> {
    const response: AxiosResponse<Service[]> = await this.client.get('/services/available');
    return response.data;
  }

  async getServiceCategories(): Promise<ServiceCategory[]> {
    const response: AxiosResponse<ServiceCategory[]> = await this.client.get('/service-categories');
    return response.data;
  }

  // Schedule management
  async getSchedule(date: string): Promise<Booking[]> {
    const response: AxiosResponse<Booking[]> = await this.client.get(`/providers/schedule?date=${date}`);
    return response.data;
  }

  async updateSchedule(bookings: { id: string; scheduledTime: string }[]): Promise<Booking[]> {
    const response: AxiosResponse<Booking[]> = await this.client.put('/providers/schedule', { bookings });
    return response.data;
  }
}

// Create and export API client instance
const apiClient = new ApiClient();

// Export individual functions for easier use
export const {
  login,
  logout,
  getCurrentUser,
  getProviderProfile,
  updateProviderProfile,
  updateAvailability,
  getMyBookings,
  getBookingById,
  updateBookingStatus,
  acceptBooking,
  rejectBooking,
  startService,
  completeService,
  getProviderStats,
  getRecentBookings,
  getEarnings,
  getPaymentHistory,
  getMyReviews,
  getReviewById,
  getAvailableServices,
  getServiceCategories,
  getSchedule,
  updateSchedule,
} = apiClient;

export default apiClient;
