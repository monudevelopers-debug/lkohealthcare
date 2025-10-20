export interface ServiceCategory {
  id: string;
  name: string;
  description: string;
  icon?: string;
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;
  updatedAt: string;
}

export interface Service {
  id: string;
  name: string;
  description: string;
  shortDescription?: string;
  price: number;
  duration: number; // in minutes
  category: ServiceCategory;
  status: 'ACTIVE' | 'INACTIVE';
  features?: string[];
  requirements?: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ServiceFilters {
  categoryId?: string;
  search?: string;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: 'name' | 'price' | 'duration';
  sortOrder?: 'asc' | 'desc';
}
