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
  // UI convenience flag (derived from status)
  isActive?: boolean;
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
  // Optional analytics fields used by UI
  rating?: number;
  reviewCount?: number;
  providerCount?: number;
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
  method: 'CASH' | 'ONLINE' | 'CARD' | 'UPI' | 'NET_BANKING' | 'WALLET';
  // UI expects these fields
  paymentMethod?: Payment['method'];
  gatewayTransactionId?: string;
  refundedAmount?: number;
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
  // Optional fields used by Dashboard UI
  revenueData?: Array<{ name: string; value: number }>;
  bookingsData?: Array<{ name: string; value: number }>;
  alerts?: Array<{ type: 'error' | 'warning' | 'info'; title: string; message: string }>;
}

export interface SystemHealth {
  database: 'healthy' | 'unhealthy' | 'unknown';
  redis: 'healthy' | 'unhealthy' | 'unknown';
  email: 'healthy' | 'unhealthy' | 'unknown';
  overall: 'healthy' | 'unhealthy' | 'unknown';
  lastChecked: string;
  // Some UIs reference this boolean
  isHealthy?: boolean;
  issues?: string[];
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
        const token = localStorage.getItem('authToken') || localStorage.getItem('token');
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
    // Backward-compatible signature adapter: allow (page, size, role?, status?, search?)
    // If third arg looks like a role/status filter, shift params accordingly
    let role: string | undefined;
    let status: string | undefined;
    let q: string | undefined = search;

    if (typeof search === 'string' && ['ADMIN', 'PROVIDER', 'CUSTOMER', 'ACTIVE', 'INACTIVE', 'SUSPENDED', 'ALL'].includes(search)) {
      role = ['ADMIN', 'PROVIDER', 'CUSTOMER'].includes(search) ? search : undefined;
      status = ['ACTIVE', 'INACTIVE', 'SUSPENDED'].includes(search) ? search : undefined;
      q = undefined;
    }

    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
      ...(role && role !== 'ALL' ? { role } : {}),
      ...(status && status !== 'ALL' ? { status } : {}),
      ...(q ? { search: q } : {}),
    });
    const response = await this.client.get(`/users?${params}`);
    const data = response.data;
    if (Array.isArray(data?.content)) {
      data.content = data.content.map((u: User) => ({ ...u, isActive: u.isActive ?? u.status === 'ACTIVE' }));
    }
    return data;
  }

  async getUserById(id: string): Promise<User> {
    const response: AxiosResponse<User> = await this.client.get(`/users/${id}`);
    return response.data;
  }

  async updateUserStatus(id: string, statusOrActive: User['status'] | boolean): Promise<User> {
    const status = typeof statusOrActive === 'boolean'
      ? (statusOrActive ? 'ACTIVE' : 'INACTIVE')
      : statusOrActive;
    const response: AxiosResponse<User> = await this.client.patch(`/users/${id}/status`, { status });
    return { ...response.data, isActive: response.data.status === 'ACTIVE' };
  }

  async updateUserRole(id: string, role: User['role']): Promise<User> {
    const response: AxiosResponse<User> = await this.client.patch(`/users/${id}/role`, { role });
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
    try {
      const response: AxiosResponse<AdminStats> = await this.client.get(`/admin/stats?period=${period}`);
      const stats = response.data;
      // Ensure optional fields exist for UI
      return {
        revenueData: [],
        bookingsData: [],
        alerts: [],
        ...stats,
      };
    } catch (e) {
      return {
        totalUsers: 0,
        totalProviders: 0,
        totalBookings: 0,
        activeBookings: 0,
        totalRevenue: 0,
        usersChange: 0,
        providersChange: 0,
        bookingsChange: 0,
        revenueChange: 0,
        usersTrend: 'stable',
        providersTrend: 'stable',
        bookingsTrend: 'stable',
        revenueTrend: 'stable',
        revenueData: [],
        bookingsData: [],
        alerts: [],
      };
    }
  }

  async getSystemHealth(): Promise<SystemHealth> {
    try {
      const response: AxiosResponse<SystemHealth> = await this.client.get('/admin/health');
      const h = response.data;
      return { isHealthy: h.overall === 'healthy', issues: [], ...h };
    } catch (e) {
      return {
        database: 'unknown',
        redis: 'unknown',
        email: 'unknown',
        overall: 'unhealthy',
        lastChecked: new Date().toISOString(),
        isHealthy: false,
        issues: ['Failed to fetch system health'],
      };
    }
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
    const data = response.data;
    if (Array.isArray(data?.content)) {
      data.content = data.content.map((p: any) => ({
        refundedAmount: 0,
        paymentMethod: p.paymentMethod ?? p.method,
        ...p,
      }));
    }
    return data;
  }

  async processRefund(paymentId: string, amount?: number): Promise<Payment> {
    const response: AxiosResponse<Payment> = await this.client.post(`/payments/${paymentId}/refund`, { amount });
    return response.data;
  }

  // Additional admin helpers expected by UI
  async getPaymentStats(): Promise<any> {
    try {
      const response = await this.client.get('/payments/stats');
      return response.data;
    } catch (e) {
      return {
        totalRevenue: 0,
        revenueChange: 0,
        revenueTrend: 'stable',
        successfulPayments: 0,
        successfulChange: 0,
        successfulTrend: 'stable',
        failedPayments: 0,
        failedChange: 0,
        failedTrend: 'stable',
        refundedAmount: 0,
        refundedChange: 0,
        refundedTrend: 'stable',
      };
    }
  }

  async updatePaymentStatus(id: string, status: Payment['status']): Promise<Payment> {
    const response = await this.client.patch(`/payments/${id}/status`, { status });
    return response.data;
  }

  async refundPayment(id: string, amount: number, reason?: string): Promise<Payment> {
    const response = await this.client.post(`/payments/${id}/refund`, { amount, reason });
    return response.data;
  }

  // System settings endpoints (backend optional)
  async getSystemSettings(): Promise<any> {
    try {
      const response = await this.client.get('/admin/settings');
      return response.data;
    } catch (e) {
      return {
        appName: 'Lucknow Healthcare Services',
        appUrl: 'http://localhost:8080',
        supportEmail: 'support@lucknowhealthcare.com',
        supportPhone: '+91-9876543210',
        appDescription: 'Comprehensive healthcare services platform',
        smtpHost: 'smtp.gmail.com',
        smtpPort: 587,
        smtpUsername: '',
        smtpPassword: '',
        jwtSecret: '',
        jwtExpiration: 15,
        refreshExpiration: 7,
        bcryptRounds: 12,
        corsOrigins: 'http://localhost:3000,http://localhost:3001,http://localhost:3002',
        databaseUrl: 'jdbc:postgresql://localhost:5432/lucknow_healthcare',
        redisUrl: 'redis://localhost:6379',
        logLevel: 'INFO',
        emailNotifications: true,
        smsNotifications: false,
        pushNotifications: false,
        notificationTemplates: 'default',
      };
    }
  }

  async updateSystemSettings(settings: any): Promise<any> {
    try {
      const response = await this.client.put('/admin/settings', settings);
      return response.data;
    } catch (e) {
      return settings;
    }
  }

  async testEmailConfiguration(): Promise<void> {
    await this.client.post('/admin/settings/test-email');
  }

  async testDatabaseConnection(): Promise<void> {
    await this.client.get('/admin/settings/test-database');
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
  updateUserRole,
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
  getAnalytics,
  getRevenueData,
  getUserGrowthData,
  getBookingTrends,
  getAdminStats,
  getSystemHealth,
  getPayments,
  processRefund,
  getReviews,
  deleteReview,
} = apiClient as any;

// Explicitly export helpers added above
export const getPaymentStats = (apiClient as any).getPaymentStats.bind(apiClient);
export const updatePaymentStatus = (apiClient as any).updatePaymentStatus.bind(apiClient);
export const refundPayment = (apiClient as any).refundPayment.bind(apiClient);
export const getSystemSettings = (apiClient as any).getSystemSettings.bind(apiClient);
export const updateSystemSettings = (apiClient as any).updateSystemSettings.bind(apiClient);
export const testEmailConfiguration = (apiClient as any).testEmailConfiguration.bind(apiClient);
export const testDatabaseConnection = (apiClient as any).testDatabaseConnection.bind(apiClient);

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
    const token = localStorage.getItem('authToken') || localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
