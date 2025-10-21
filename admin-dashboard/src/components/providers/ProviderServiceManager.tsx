import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { Plus, X, Tag, CheckCircle, AlertCircle } from 'lucide-react';

interface Service {
  id: string;
  name: string;
  description: string;
  price: number;
  category: {
    id: string;
    name: string;
  };
}

interface ProviderServiceManagerProps {
  providerId: string;
}

const ProviderServiceManager: React.FC<ProviderServiceManagerProps> = ({ providerId }) => {
  const [selectedServices, setSelectedServices] = useState<Set<string>>(new Set());
  const queryClient = useQueryClient();

  // Fetch all available services
  const { data: allServices = [] } = useQuery(
    'all-services',
    async () => {
      const response = await fetch('http://localhost:8080/api/services/active');
      if (!response.ok) throw new Error('Failed to fetch services');
      return response.json();
    }
  );

  // Fetch provider's current services
  const { data: providerServices = [], isLoading } = useQuery(
    ['provider-services', providerId],
    async () => {
      const response = await fetch(`http://localhost:8080/api/providers/${providerId}/services`);
      if (!response.ok) throw new Error('Failed to fetch provider services');
      return response.json();
    },
    {
      enabled: !!providerId,
      onSuccess: (data) => {
        // Initialize selected services
        setSelectedServices(new Set(data.map((s: Service) => s.id)));
      },
    }
  );

  // Add service mutation (admin direct assignment - no approval needed)
  const addServiceMutation = useMutation(
    async (serviceId: string) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/providers/${providerId}/services/${serviceId}`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to add service');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['provider-services', providerId]);
        queryClient.invalidateQueries(['providers']);
      },
      onError: (error: any) => {
        alert('❌ ' + (error.message || 'Failed to add service'));
      },
    }
  );

  // Remove service mutation (admin direct removal - no approval needed)
  const removeServiceMutation = useMutation(
    async (serviceId: string) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/providers/${providerId}/services/${serviceId}`,
        {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to remove service');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['provider-services', providerId]);
        queryClient.invalidateQueries(['providers']);
      },
      onError: (error: any) => {
        alert('❌ ' + (error.message || 'Failed to remove service'));
      },
    }
  );

  const handleToggleService = (serviceId: string, isSelected: boolean) => {
    const newSelected = new Set(selectedServices);
    
    if (isSelected) {
      newSelected.add(serviceId);
      // Add service (admin direct assignment)
      addServiceMutation.mutate(serviceId);
    } else {
      newSelected.delete(serviceId);
      // Remove service (admin direct removal)
      if (window.confirm('Remove this service from provider?\n\nThis is immediate and does not require approval.')) {
        removeServiceMutation.mutate(serviceId);
      } else {
        return; // User cancelled
      }
    }
    
    setSelectedServices(newSelected);
  };

  const providerServiceIds = providerServices.map((s: Service) => s.id);

  return (
    <div className="space-y-4">
      <div className="bg-yellow-50 border border-yellow-300 rounded-lg p-3">
        <div className="flex items-start gap-2">
          <AlertCircle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
          <div className="text-sm text-yellow-800">
            <p className="font-medium mb-1">Admin Direct Assignment</p>
            <p className="text-xs">
              Changes here are immediate and do not require approval. Use this to assign/remove services after verifying provider qualifications.
            </p>
          </div>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-4">
          <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto mb-2"></div>
          <p className="text-sm text-gray-500">Loading services...</p>
        </div>
      ) : (
        <>
          {/* Current Services */}
          {providerServices.length > 0 && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Currently Assigned Services ({providerServices.length})
              </label>
              <div className="flex flex-wrap gap-2 mb-3">
                {providerServices.map((service: Service) => (
                  <div
                    key={service.id}
                    className="inline-flex items-center gap-2 px-3 py-1.5 bg-green-100 text-green-800 rounded-full text-sm"
                  >
                    <CheckCircle className="w-4 h-4" />
                    <span>{service.name}</span>
                    <button
                      onClick={() => handleToggleService(service.id, false)}
                      className="hover:bg-green-200 rounded-full p-0.5"
                      title="Remove service"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* All Services Checklist */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              All Available Services
            </label>
            <div className="max-h-64 overflow-y-auto border border-gray-200 rounded-lg">
              {allServices.length === 0 ? (
                <div className="p-4 text-center text-gray-500 text-sm">
                  No services available
                </div>
              ) : (
                <div className="divide-y divide-gray-200">
                  {allServices.map((service: Service) => {
                    const isAssigned = providerServiceIds.includes(service.id);
                    return (
                      <label
                        key={service.id}
                        className={`flex items-center gap-3 p-3 cursor-pointer hover:bg-gray-50 ${
                          isAssigned ? 'bg-green-50' : ''
                        }`}
                      >
                        <input
                          type="checkbox"
                          checked={isAssigned}
                          onChange={(e) => handleToggleService(service.id, e.target.checked)}
                          className="w-4 h-4 text-blue-600 rounded focus:ring-2 focus:ring-blue-500"
                        />
                        <div className="flex-1">
                          <div className="flex items-center gap-2">
                            <span className="font-medium text-gray-900">{service.name}</span>
                            {isAssigned && (
                              <CheckCircle className="w-4 h-4 text-green-600" />
                            )}
                          </div>
                          <p className="text-xs text-gray-500">{service.category.name} • ₹{service.price}</p>
                        </div>
                      </label>
                    );
                  })}
                </div>
              )}
            </div>
            <p className="text-xs text-gray-500 mt-2">
              ✅ Checked services are immediately assigned (no approval needed for admin)
            </p>
          </div>
        </>
      )}
    </div>
  );
};

export default ProviderServiceManager;

