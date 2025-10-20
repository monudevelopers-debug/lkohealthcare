import { useQuery } from '@tanstack/react-query';
import { servicesApi } from '../api/services.api';

export const useCategories = () => {
  return useQuery({
    queryKey: ['categories'],
    queryFn: servicesApi.getCategories,
  });
};

export const useServices = () => {
  return useQuery({
    queryKey: ['services'],
    queryFn: servicesApi.getAllServices,
  });
};

export const useServicesByCategory = (categoryId: string | null) => {
  return useQuery({
    queryKey: ['services', 'category', categoryId],
    queryFn: () => categoryId ? servicesApi.getServicesByCategory(categoryId) : servicesApi.getAllServices(),
    enabled: !!categoryId || categoryId === null,
  });
};

export const useSearchServices = (query: string) => {
  return useQuery({
    queryKey: ['services', 'search', query],
    queryFn: () => servicesApi.searchServices(query),
    enabled: query.length > 2,
  });
};

export const useService = (id: string) => {
  return useQuery({
    queryKey: ['service', id],
    queryFn: () => servicesApi.getServiceById(id),
    enabled: !!id,
  });
};
