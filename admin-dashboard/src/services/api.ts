import axios, { AxiosInstance, AxiosResponse } from 'axios';

// Resolve API base URL in Vite (browser) environments
const API_BASE_URL: string =
  (typeof import.meta !== 'undefined' && (import.meta as any)?.env?.VITE_API_URL)
    ? (import.meta as any).env.VITE_API_URL
    : 'http://localhost:8080/api';

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
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token') || localStorage.getItem('authToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
          console.log(`[API] Request to ${config.url} with token: ${token.substring(0, 20)}...`);
        } else {
          console.warn(`[API] Request to ${config.url} without token`);
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        // Log 401 errors for debugging
        if (error.response?.status === 401) {
          console.error(`[API] 401 Unauthorized for ${error.config?.url}`);
          console.error('[API] Token in localStorage:', localStorage.getItem('token') ? 'EXISTS' : 'MISSING');
          
          const requestUrl = error.config?.url || '';
          
          // Skip auto-logout for login endpoint (to avoid redirect loops)
          if (!requestUrl.includes('/auth/login')) {
            console.warn('[API] Token expired or invalid - logging out');
            localStorage.removeItem('token');
            localStorage.removeItem('authToken');
            localStorage.removeItem('user');
            
            // Only redirect if not already on login page
            if (window.location.pathname !== '/login') {
              window.location.href = '/login';
            }
          }
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
    localStorage.removeItem('token');
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
    const response: AxiosResponse<User> = await this.client.put(`/users/${id}/status?status=${status}`);
    return response.data;
  }

  async updateUserRole(id: string, role: User['role']): Promise<User> {
    const response: AxiosResponse<User> = await this.client.put(`/users/${id}/role?role=${role}`);
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

  // Detailed analytics (frontend expects these helpers)
  async getAnalytics(period: 'week' | 'month' | 'quarter' | 'year'): Promise<any> {
    try {
      const response: AxiosResponse<any> = await this.client.get(`/admin/analytics?period=${period}`);
      return response.data;
    } catch (e) {
      // Fallback shape expected by UI
      return {
        totalRevenue: 0,
        totalBookings: 0,
        activeUsers: 0,
        activeProviders: 0,
        revenueChange: 0,
        bookingsChange: 0,
        usersChange: 0,
        providersChange: 0,
        revenueTrend: 'up',
        bookingsTrend: 'up',
        usersTrend: 'up',
        providersTrend: 'up',
        topServices: [],
        topProviders: [],
        bookingStatusDistribution: {},
      };
    }
  }

  async getRevenueData(period: 'week' | 'month' | 'quarter' | 'year'): Promise<any[]> {
    try {
      const response: AxiosResponse<any[]> = await this.client.get(`/admin/analytics/revenue?period=${period}`);
      return Array.isArray(response.data) ? response.data : [];
    } catch (e) {
      return [];
    }
  }

  async getUserGrowthData(period: 'week' | 'month' | 'quarter' | 'year'): Promise<any[]> {
    try {
      const response: AxiosResponse<any[]> = await this.client.get(`/admin/analytics/user-growth?period=${period}`);
      return Array.isArray(response.data) ? response.data : [];
    } catch (e) {
      return [];
    }
  }

  async getBookingTrends(period: 'week' | 'month' | 'quarter' | 'year'): Promise<any[]> {
    try {
      const response: AxiosResponse<any[]> = await this.client.get(`/admin/analytics/booking-trends?period=${period}`);
      return Array.isArray(response.data) ? response.data : [];
    } catch (e) {
      return [];
    }
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

  // Admin endpoints
  async getAdminStats(period: 'today' | 'week' | 'month' | 'year'): Promise<AdminStats> {
    const response: AxiosResponse<AdminStats> = await this.client.get(`/admin/stats?period=${period}`);
    return response.data;
  }

  async getSystemHealth(): Promise<SystemHealth> {
    const response: AxiosResponse<SystemHealth> = await this.client.get('/admin/health');
    return response.data;
  }
}

// Create and export API client instance
const apiClient = new ApiClient();

// Analytics and stats functions - use apiClient instance methods
export const getAdminStats = async (period: 'today' | 'week' | 'month' | 'year'): Promise<AdminStats> => {
  return apiClient.getAdminStats(period);
};

export const getSystemHealth = async (): Promise<SystemHealth> => {
  return apiClient.getSystemHealth();
};

// Export individual functions for easier use (with proper binding)
export const login = apiClient.login.bind(apiClient);
export const logout = apiClient.logout.bind(apiClient);
export const getCurrentUser = apiClient.getCurrentUser.bind(apiClient);
export const getUsers = apiClient.getUsers.bind(apiClient);
export const getUserById = apiClient.getUserById.bind(apiClient);
export const updateUserStatus = apiClient.updateUserStatus.bind(apiClient);
export const updateUserRole = apiClient.updateUserRole.bind(apiClient);
export const getServices = apiClient.getServices.bind(apiClient);
export const getServiceById = apiClient.getServiceById.bind(apiClient);
export const createService = apiClient.createService.bind(apiClient);
export const updateService = apiClient.updateService.bind(apiClient);
export const deleteService = apiClient.deleteService.bind(apiClient);
export const getServiceCategories = apiClient.getServiceCategories.bind(apiClient);
export const createServiceCategory = apiClient.createServiceCategory.bind(apiClient);
export const getProviders = apiClient.getProviders.bind(apiClient);
export const getProviderById = apiClient.getProviderById.bind(apiClient);
export const updateProviderStatus = apiClient.updateProviderStatus.bind(apiClient);
export const getBookings = apiClient.getBookings.bind(apiClient);
export const getBookingById = apiClient.getBookingById.bind(apiClient);
export const updateBookingStatus = apiClient.updateBookingStatus.bind(apiClient);
export const assignProvider = apiClient.assignProvider.bind(apiClient);
export const getAnalytics = apiClient.getAnalytics.bind(apiClient);
export const getRevenueData = apiClient.getRevenueData.bind(apiClient);
export const getUserGrowthData = apiClient.getUserGrowthData.bind(apiClient);
export const getBookingTrends = apiClient.getBookingTrends.bind(apiClient);
export const getPayments = apiClient.getPayments.bind(apiClient);
export const processRefund = apiClient.processRefund.bind(apiClient);
export const getReviews = apiClient.getReviews.bind(apiClient);
export const deleteReview = apiClient.deleteReview.bind(apiClient);

// Export axios instance for direct use
export const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token') || localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log(`[API-Direct] Request to ${config.url} with token: ${token.substring(0, 20)}...`);
    } else {
      console.warn(`[API-Direct] Request to ${config.url} without token`);
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Log 401 errors for debugging
    if (error.response?.status === 401) {
      console.error(`[API-Direct] 401 Unauthorized for ${error.config?.url}`);
      console.error('[API-Direct] Token in localStorage:', localStorage.getItem('token') ? 'EXISTS' : 'MISSING');
      
      const requestUrl = error.config?.url || '';
      
      // Only auto-logout for auth-related endpoints
      if (requestUrl.includes('/auth/me') || requestUrl.includes('/auth/logout')) {
        console.warn('[API-Direct] Logging out due to 401 on auth endpoint');
        localStorage.removeItem('token');
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
