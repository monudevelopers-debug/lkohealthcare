import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { TestUser } from './test-user-generator.js';

export interface ApiResponse<T = any> {
  data: T;
  status: number;
  message?: string;
}

export default class ApiClient {
  private client: AxiosInstance;
  private baseURL: string;
  private token: string | null = null;

  constructor(baseURL: string = 'http://localhost:8080') {
    this.baseURL = baseURL;
    this.client = axios.create({
      baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor to include auth token
    this.client.interceptors.request.use(
      (config) => {
        if (this.token) {
          config.headers.Authorization = `Bearer ${this.token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // Add response interceptor for error handling
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          this.token = null;
        }
        return Promise.reject(error);
      }
    );
  }

  setToken(token: string): void {
    this.token = token;
  }

  clearToken(): void {
    this.token = null;
  }

  async registerUser(user: TestUser): Promise<ApiResponse> {
    const response = await this.client.post('/auth/register', {
      email: user.email,
      password: user.password,
      firstName: user.firstName,
      lastName: user.lastName,
      phone: user.phone,
      role: user.role,
    });
    return response.data;
  }

  async loginUser(user: TestUser): Promise<ApiResponse> {
    const response = await this.client.post('/auth/login', {
      email: user.email,
      password: user.password,
    });
    
    if (response.data.token) {
      this.setToken(response.data.token);
    }
    
    return response.data;
  }

  async createCustomer(user: TestUser): Promise<ApiResponse> {
    // First register the user
    await this.registerUser(user);
    
    // Then login to get token
    const loginResponse = await this.loginUser(user);
    
    return loginResponse;
  }

  async createProvider(user: TestUser): Promise<ApiResponse> {
    // First register the user
    await this.registerUser(user);
    
    // Then login to get token
    const loginResponse = await this.loginUser(user);
    
    return loginResponse;
  }

  async createAdmin(user: TestUser): Promise<ApiResponse> {
    // First register the user
    await this.registerUser(user);
    
    // Then login to get token
    const loginResponse = await this.loginUser(user);
    
    return loginResponse;
  }

  async getBookings(): Promise<ApiResponse> {
    const response = await this.client.get('/bookings');
    return response.data;
  }

  async getPatients(): Promise<ApiResponse> {
    const response = await this.client.get('/patients');
    return response.data;
  }

  async getServices(): Promise<ApiResponse> {
    const response = await this.client.get('/services');
    return response.data;
  }

  async getProviders(): Promise<ApiResponse> {
    const response = await this.client.get('/providers');
    return response.data;
  }

  async getAdminStats(): Promise<ApiResponse> {
    const response = await this.client.get('/admin/stats');
    return response.data;
  }

  async getProviderStats(): Promise<ApiResponse> {
    const response = await this.client.get('/providers/stats');
    return response.data;
  }

  async createBooking(bookingData: any): Promise<ApiResponse> {
    const response = await this.client.post('/bookings', bookingData);
    return response.data;
  }

  async createPatient(patientData: any): Promise<ApiResponse> {
    const response = await this.client.post('/patients', patientData);
    return response.data;
  }

  async updateBookingStatus(bookingId: string, status: string): Promise<ApiResponse> {
    const response = await this.client.put(`/bookings/${bookingId}/status?status=${status}`);
    return response.data;
  }

  async assignProviderToBooking(bookingId: string, providerId: string): Promise<ApiResponse> {
    const response = await this.client.post(`/bookings/${bookingId}/assign-provider/${providerId}`);
    return response.data;
  }

  async getCurrentUser(): Promise<ApiResponse> {
    const response = await this.client.get('/auth/me');
    return response.data;
  }

  async refreshToken(): Promise<ApiResponse> {
    const response = await this.client.post('/auth/refresh-token');
    if (response.data.token) {
      this.setToken(response.data.token);
    }
    return response.data;
  }

  async logout(): Promise<ApiResponse> {
    const response = await this.client.post('/auth/logout');
    this.clearToken();
    return response.data;
  }
}

export const apiClient = new ApiClient();
