import axios, { AxiosInstance, AxiosResponse } from 'axios';

// Types
export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  address?: string;
  role: 'CUSTOMER' | 'PROVIDER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  emailVerified: boolean;
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
  qualification: string;
  experience: number;
  availabilityStatus: 'AVAILABLE' | 'OFF_DUTY' | 'BUSY';
  rating: number;
  totalRatings: number;
  isVerified: boolean;
  documents?: string[];
  services?: Service[];
  createdAt: string;
  updatedAt: string;
}

export interface Patient {
  id: string;
  customerId: string;
  name: string;
  age: number;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  weight?: number;
  height?: number;
  bloodGroup?: string;
  isDiabetic?: boolean;
  bpStatus: 'NORMAL' | 'HIGH' | 'LOW' | 'UNKNOWN';
  allergies?: string;
  chronicConditions?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  relationshipToCustomer: 'SELF' | 'PARENT' | 'CHILD' | 'SPOUSE' | 'SIBLING' | 'GRANDPARENT' | 'GRANDCHILD' | 'OTHER';
  isSensitiveData?: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PrivacyAwareUser {
  id: string;
  name: string;
  email: string;
  phone?: string; // May be null if outside privacy window
  address?: string; // May be null if outside privacy window
  role: 'CUSTOMER' | 'PROVIDER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
  contactDetailsAvailable: boolean;
  privacyMessage: string;
}

export interface PrivacyAwarePatient {
  id: string;
  customerId: string;
  name: string;
  age: number;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  weight?: number;
  height?: number;
  bloodGroup?: string;
  isDiabetic?: boolean;
  bpStatus: 'NORMAL' | 'HIGH' | 'LOW' | 'UNKNOWN';
  allergies?: string;
  chronicConditions?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string; // May be null if outside privacy window
  emergencyContactRelation?: string;
  relationshipToCustomer: 'SELF' | 'SPOUSE' | 'CHILD' | 'PARENT' | 'SIBLING' | 'OTHER';
  isSensitiveData: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  emergencyContactAvailable: boolean;
  privacyMessage: string;
}

export interface PrivacyAwareBooking {
  id: string;
  user: PrivacyAwareUser;
  service: Service;
  provider?: Provider;
  patient?: PrivacyAwarePatient;
  scheduledDate: string;
  scheduledTime: string;
  duration: number;
  status: 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  totalAmount: number;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  specialInstructions?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  customerContactAvailable: boolean;
  privacyMessage: string;
}

export interface Booking {
  id: string;
  user: User;
  service: Service;
  provider?: Provider;
  patient?: Patient;
  scheduledDate: string;
  scheduledTime: string;
  duration: number;
  status: 'PENDING' | 'CONFIRMED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  totalAmount: number;
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  specialInstructions?: string;
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

export interface ProviderRegistrationRequest {
  name: string;
  email: string;
  password: string;
  phone: string;
  qualification: string;
  experience: number;
  documents?: string[];
}

export interface ProviderRegistrationResponse {
  token: string;
  user: User;
  provider: Provider;
  expiresIn: number;
  message: string;
}

// API Client
class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Request interceptor to add auth token
    this.client.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('token');
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
          const requestUrl = error.config?.url || '';
          
          // Handle 401 for all authenticated endpoints (except login/register)
          if (!requestUrl.includes('/auth/login') && !requestUrl.includes('/auth/register')) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.dispatchEvent(new CustomEvent('auth:logout'));
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
    localStorage.removeItem('user');
  }

  async getCurrentUser(): Promise<User> {
    const response: AxiosResponse<User> = await this.client.get('/auth/me');
    return response.data;
  }

  async refreshToken(): Promise<{ token: string }> {
    const response: AxiosResponse<{ token: string }> = await this.client.post('/auth/refresh-token');
    return response.data;
  }

  async registerProvider(registrationData: ProviderRegistrationRequest): Promise<ProviderRegistrationResponse> {
    const response: AxiosResponse<ProviderRegistrationResponse> = await this.client.post('/auth/register-provider', registrationData);
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
    const response: AxiosResponse<Booking[]> = await this.client.get(`/providers/bookings?page=0&size=${limit}`);
    return response.data.content || [];
  }

  // Calendar API - fetch provider bookings by date range
  async getProviderBookings(startDate: string, endDate: string): Promise<Booking[]> {
    const params = new URLSearchParams({
      startDate,
      endDate,
    });
    const response: AxiosResponse<Booking[]> = await this.client.get(`/providers/bookings/calendar?${params}`);
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


// Export individual functions as arrow functions to maintain 'this' context
export const login = (credentials: LoginRequest) => apiClient.login(credentials);
export const logout = () => apiClient.logout();
export const getCurrentUser = () => apiClient.getCurrentUser();
export const refreshToken = () => apiClient.refreshToken();
export const registerProvider = (registrationData: ProviderRegistrationRequest) => apiClient.registerProvider(registrationData);
export const getProviderProfile = () => apiClient.getProviderProfile();
export const getCurrentProvider = () => apiClient.getProviderProfile(); // Alias for clarity
export const updateProviderProfile = (profile: Partial<Provider>) => apiClient.updateProviderProfile(profile);
export const updateAvailability = (isAvailable: boolean) => apiClient.updateAvailability(isAvailable);
export const getMyBookings = (page = 0, size = 20, status?: Booking['status']) => apiClient.getMyBookings(page, size, status);
export const getBookingById = (id: string) => apiClient.getBookingById(id);
export const updateBookingStatus = (id: string, status: Booking['status']) => apiClient.updateBookingStatus(id, status);
export const acceptBooking = (id: string) => apiClient.acceptBooking(id);
export const rejectBooking = (id: string, reason?: string) => apiClient.rejectBooking(id, reason);
export const startService = (id: string) => apiClient.startService(id);
export const completeService = (id: string, notes?: string) => apiClient.completeService(id, notes);
export const getProviderStats = (period: 'today' | 'week' | 'month') => apiClient.getProviderStats(period);
export const getRecentBookings = (limit = 10) => apiClient.getRecentBookings(limit);
export const getProviderBookings = (startDate: string, endDate: string) => apiClient.getProviderBookings(startDate, endDate);
export const getEarnings = (period: 'today' | 'week' | 'month' | 'year') => apiClient.getEarnings(period);
export const getPaymentHistory = (page = 0, size = 20) => apiClient.getPaymentHistory(page, size);
export const getMyReviews = (page = 0, size = 20) => apiClient.getMyReviews(page, size);
export const getReviewById = (id: string) => apiClient.getReviewById(id);
export const getAvailableServices = () => apiClient.getAvailableServices();
export const getServiceCategories = () => apiClient.getServiceCategories();
export const getSchedule = (date: string) => apiClient.getSchedule(date);
export const updateSchedule = (bookings: { id: string; scheduledTime: string }[]) => apiClient.updateSchedule(bookings);

export default apiClient;
