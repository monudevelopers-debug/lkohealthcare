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

export interface ServiceCategory {
  id: string;
  name: string;
  description: string;
  isActive: boolean;
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

