import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  Plus, 
  Search,
  Filter,
  MoreVertical,
  Edit,
  Trash2,
  Eye,
  Star,
  DollarSign,
  Clock,
  Tag,
  CheckCircle,
  XCircle
} from 'lucide-react';

import { getProviderServices, getServiceCategories, addServiceToProvider, removeServiceFromProvider, updateServiceAvailability } from '../services/api';
import { Service } from '../types/service';

const Services: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedService, setSelectedService] = useState<Service | null>(null);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  
  const queryClient = useQueryClient();

  // Fetch provider services
  const { data: servicesData, isLoading } = useQuery(
    ['provider-services', categoryFilter],
    () => getProviderServices(0, 50, categoryFilter === 'ALL' ? undefined : categoryFilter),
    {
      refetchInterval: 60000, // Refetch every minute
    }
  );

  // Fetch service categories
  const { data: categoriesData } = useQuery(
    'service-categories',
    () => getServiceCategories(),
  );

  // Add service to provider mutation
  const addServiceMutation = useMutation(
    (serviceId: string) => addServiceToProvider(serviceId),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['provider-services']);
        setShowAddModal(false);
      },
    }
  );

  // Remove service from provider mutation
  const removeServiceMutation = useMutation(
    (serviceId: string) => removeServiceFromProvider(serviceId),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['provider-services']);
      },
    }
  );

  // Update service availability mutation
  const updateAvailabilityMutation = useMutation(
    ({ serviceId, isAvailable }: { serviceId: string; isAvailable: boolean }) => 
      updateServiceAvailability(serviceId, isAvailable),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['provider-services']);
      },
    }
  );

  const services = servicesData?.content || [];
  const categories = categoriesData || [];
  
  const filteredServices = services.filter(service =>
    service.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    service.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
    service.category.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleViewDetails = (service: Service) => {
    setSelectedService(service);
    setShowDetailsModal(true);
  };

  const handleRemoveService = (serviceId: string) => {
    if (window.confirm('Are you sure you want to remove this service from your offerings?')) {
      removeServiceMutation.mutate(serviceId);
    }
  };

  const handleAvailabilityToggle = (serviceId: string, currentStatus: boolean) => {
    updateAvailabilityMutation.mutate({ serviceId, isAvailable: !currentStatus });
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Services</h1>
          <p className="text-gray-600">Manage your service offerings and availability</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Add Service
        </button>
      </div>

      {/* Filters and Search */}
      <div className="bg-white p-6 rounded-lg shadow-sm border">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search services by name, description, or category..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
          <div className="flex gap-2">
            <select
              value={categoryFilter}
              onChange={(e) => setCategoryFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Categories</option>
              {categories.map((category: any) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
            <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
              <Filter className="w-4 h-4" />
              Filters
            </button>
          </div>
        </div>
      </div>

      {/* Services Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {isLoading ? (
          <div className="col-span-full p-8 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-2 text-gray-600">Loading services...</p>
          </div>
        ) : filteredServices.length === 0 ? (
          <div className="col-span-full p-8 text-center">
            <Tag className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No services found</p>
          </div>
        ) : (
          filteredServices.map((service) => (
            <div key={service.id} className="bg-white rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className="h-12 w-12 rounded-full bg-blue-100 flex items-center justify-center">
                    <Tag className="h-6 w-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="text-lg font-semibold text-gray-900">{service.name}</h3>
                    <p className="text-sm text-gray-500">{service.category.name}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleViewDetails(service)}
                    className="text-blue-600 hover:text-blue-900"
                    title="View Details"
                  >
                    <Eye className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => handleRemoveService(service.id)}
                    className="text-red-600 hover:text-red-900"
                    title="Remove Service"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                  <button className="text-gray-400 hover:text-gray-600">
                    <MoreVertical className="w-4 h-4" />
                  </button>
                </div>
              </div>

              <div className="space-y-3">
                <p className="text-sm text-gray-600 line-clamp-2">
                  {service.description}
                </p>
                
                <div className="flex items-center justify-between">
                  <div className="flex items-center text-sm text-gray-600">
                    <DollarSign className="w-4 h-4 mr-1" />
                    <span className="font-medium">₹{service.price}</span>
                  </div>
                  <div className="flex items-center text-sm text-gray-600">
                    <Clock className="w-4 h-4 mr-1" />
                    <span>{service.duration} min</span>
                  </div>
                </div>

                <div className="flex items-center text-sm text-gray-600">
                  <Star className="w-4 h-4 mr-1 text-yellow-500" />
                  <span className="font-medium">{service.rating}</span>
                  <span className="text-gray-400 ml-1">/ 5.0</span>
                  <span className="text-gray-400 ml-2">({service.reviewCount} reviews)</span>
                </div>

                <div className="flex items-center justify-between">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    service.isAvailable 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {service.isAvailable ? 'Available' : 'Unavailable'}
                  </span>
                  <button
                    onClick={() => handleAvailabilityToggle(service.id, service.isAvailable)}
                    className={`px-3 py-1 text-xs font-medium rounded-full ${
                      service.isAvailable
                        ? 'bg-red-100 text-red-700 hover:bg-red-200'
                        : 'bg-green-100 text-green-700 hover:bg-green-200'
                    }`}
                  >
                    {service.isAvailable ? 'Set Unavailable' : 'Set Available'}
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Add Service Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Add Service to Your Offerings
            </h3>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Select Service Category
                </label>
                <select
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onChange={(e) => {
                    // Filter services by category
                  }}
                >
                  <option value="">Select a category</option>
                  {categories.map((category: any) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Available Services
                </label>
                <div className="max-h-60 overflow-y-auto border border-gray-300 rounded-md">
                  <div className="p-4 space-y-2">
                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div>
                        <h4 className="text-sm font-medium text-gray-900">Nursing Care</h4>
                        <p className="text-xs text-gray-500">₹500/hour • 60 min</p>
                      </div>
                      <button
                        onClick={() => addServiceMutation.mutate('service-id')}
                        className="px-3 py-1 text-xs font-medium text-blue-600 bg-blue-100 rounded-md hover:bg-blue-200"
                      >
                        Add
                      </button>
                    </div>
                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div>
                        <h4 className="text-sm font-medium text-gray-900">Physiotherapy</h4>
                        <p className="text-xs text-gray-500">₹800/session • 45 min</p>
                      </div>
                      <button
                        onClick={() => addServiceMutation.mutate('service-id')}
                        className="px-3 py-1 text-xs font-medium text-blue-600 bg-blue-100 rounded-md hover:bg-blue-200"
                      >
                        Add
                      </button>
                    </div>
                    <div className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div>
                        <h4 className="text-sm font-medium text-gray-900">Elderly Care</h4>
                        <p className="text-xs text-gray-500">₹600/day • 8 hours</p>
                      </div>
                      <button
                        onClick={() => addServiceMutation.mutate('service-id')}
                        className="px-3 py-1 text-xs font-medium text-blue-600 bg-blue-100 rounded-md hover:bg-blue-200"
                      >
                        Add
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Service Details Modal */}
      {showDetailsModal && selectedService && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Service Details
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Service Name</label>
                  <p className="text-sm text-gray-900">{selectedService.name}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Category</label>
                  <p className="text-sm text-gray-900">{selectedService.category.name}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Price</label>
                  <p className="text-sm text-gray-900">₹{selectedService.price}</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Duration</label>
                  <p className="text-sm text-gray-900">{selectedService.duration} minutes</p>
                </div>
              </div>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Rating</label>
                  <div className="flex items-center">
                    <Star className="w-4 h-4 text-yellow-500 mr-1" />
                    <span className="text-sm text-gray-900">{selectedService.rating}/5.0</span>
                    <span className="text-sm text-gray-500 ml-2">({selectedService.reviewCount} reviews)</span>
                  </div>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Status</label>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    selectedService.isAvailable 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {selectedService.isAvailable ? 'Available' : 'Unavailable'}
                  </span>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">Description</label>
                  <p className="text-sm text-gray-900">{selectedService.description}</p>
                </div>
              </div>
            </div>
            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowDetailsModal(false)}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Services;
