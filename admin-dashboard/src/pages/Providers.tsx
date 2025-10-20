import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  Users, 
  Star, 
  Phone, 
  Mail, 
  MapPin, 
  Search,
  Filter,
  Edit,
  CheckCircle,
  XCircle,
  AlertCircle,
  UserPlus
} from 'lucide-react';

import { getProviders, updateProviderStatus, updateProvider } from '../services/api';
import { Provider } from '../services/api';

const Providers: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [availabilityFilter, setAvailabilityFilter] = useState<string>('ALL');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [selectedProvider, setSelectedProvider] = useState<any>(null);
  
  const queryClient = useQueryClient();

  // Fetch providers
  const { data: providersData, isLoading } = useQuery(
    ['providers'],
    () => getProviders(0, 1000), // Get all providers
    {
      refetchInterval: 60000, // Refetch every minute
    }
  );

  // Update provider status mutation
  const updateStatusMutation = useMutation(
    ({ id, isAvailable, status }: { id: string; isAvailable: boolean; status?: 'AVAILABLE' | 'BUSY' | 'OFF_DUTY' | 'ON_LEAVE' }) => 
      updateProviderStatus(id, isAvailable, status),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['providers']);
        console.log('Provider status updated successfully');
      },
      onError: (error: any) => {
        console.error('Error updating provider status:', error);
        alert(`Failed to update provider status: ${error.response?.data?.message || error.message}`);
      },
    }
  );

  // Update provider mutation
  const updateProviderMutation = useMutation(
    (providerData: Provider) => updateProvider(providerData.id, providerData),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['providers']);
        setShowEditModal(false);
        setSelectedProvider(null);
      },
    }
  );

  const providers = providersData?.content || [];
  
  console.log('Total providers:', providers.length);
  console.log('Search term:', searchTerm);
  console.log('Availability filter:', availabilityFilter);
  
  const filteredProviders = providers.filter(provider => {
    // Apply search filter
    const matchesSearch = searchTerm === '' ||
      provider.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      provider.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      provider.qualifications?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      provider.phone?.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Apply availability filter based on availabilityStatus enum
    const providerStatus = provider.availabilityStatus || (provider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY');
    const matchesAvailability = availabilityFilter === 'ALL' || providerStatus === availabilityFilter;
    
    return matchesSearch && matchesAvailability;
  });
  
  console.log('Filtered providers:', filteredProviders.length);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'AVAILABLE': return 'bg-green-100 text-green-800';
      case 'BUSY': return 'bg-yellow-100 text-yellow-800';
      case 'OFF_DUTY': return 'bg-red-100 text-red-800';
      case 'ON_LEAVE': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'AVAILABLE': return <CheckCircle className="w-4 h-4" />;
      case 'BUSY': return <AlertCircle className="w-4 h-4" />;
      case 'OFF_DUTY': return <XCircle className="w-4 h-4" />;
      case 'ON_LEAVE': return <XCircle className="w-4 h-4" />;
      default: return <AlertCircle className="w-4 h-4" />;
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'AVAILABLE': return 'Available';
      case 'BUSY': return 'Busy';
      case 'OFF_DUTY': return 'Off Duty';
      case 'ON_LEAVE': return 'On Leave';
      default: return status;
    }
  };

  const handleAvailabilityToggle = (provider: Provider) => {
    setSelectedProvider(provider);
    setShowStatusModal(true);
  };

  const handleStatusChange = (status: 'AVAILABLE' | 'BUSY' | 'OFF_DUTY' | 'ON_LEAVE') => {
    if (!selectedProvider) return;
    
    console.log('Changing status for provider:', selectedProvider.id, 'to:', status);
    
    const isAvailable = status === 'AVAILABLE';
    updateStatusMutation.mutate({ 
      id: selectedProvider.id, 
      isAvailable: isAvailable,
      status: status
    });
    
    setShowStatusModal(false);
    setSelectedProvider(null);
  };

  const handleEditProvider = (provider: Provider) => {
    setSelectedProvider(provider);
    setShowEditModal(true);
  };

  const handleUpdateProvider = (formData: FormData) => {
    if (!selectedProvider) return;
    
    const updatedProvider: Provider = {
      ...selectedProvider,
      name: formData.get('name') as string,
      email: formData.get('email') as string,
      phone: formData.get('phone') as string,
      qualifications: formData.get('qualifications') as string,
      experience: parseInt(formData.get('experience') as string) || 0,
    };
    
    updateProviderMutation.mutate(updatedProvider);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Healthcare Providers</h1>
          <p className="text-gray-600">Manage and monitor healthcare service providers</p>
        </div>
        <button
          onClick={() => setShowAddModal(true)}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
        >
          <UserPlus className="w-4 h-4" />
          Add Provider
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
                placeholder="Search providers by name, email, or qualifications..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
          <div className="flex gap-2">
            <select
              value={availabilityFilter}
              onChange={(e) => setAvailabilityFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Providers</option>
              <option value="AVAILABLE">✓ Available</option>
              <option value="BUSY">⚠ Busy</option>
              <option value="OFF_DUTY">✗ Off Duty</option>
              <option value="ON_LEAVE">✗ On Leave</option>
            </select>
            <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
              <Filter className="w-4 h-4" />
              Filters
            </button>
          </div>
        </div>
      </div>

      {/* Providers Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {isLoading ? (
          <div className="col-span-full p-8 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-2 text-gray-600">Loading providers...</p>
          </div>
        ) : filteredProviders.length === 0 ? (
          <div className="col-span-full p-8 text-center">
            <Users className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No providers found</p>
          </div>
        ) : (
          filteredProviders.map((provider) => (
            <div key={provider.id} className="bg-white rounded-lg shadow-sm border p-6 hover:shadow-md transition-shadow overflow-hidden">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center space-x-3 flex-1 min-w-0">
                  <div className="h-12 w-12 rounded-full bg-blue-100 flex items-center justify-center flex-shrink-0">
                    <Users className="h-6 w-6 text-blue-600" />
                  </div>
                  <div className="min-w-0 flex-1">
                    <h3 className="text-lg font-semibold text-gray-900 truncate">{provider.name}</h3>
                    <p className="text-sm text-gray-500 truncate">{provider.email}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-2 flex-shrink-0">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium whitespace-nowrap ${getStatusColor(provider.availabilityStatus || (provider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}`}>
                    {getStatusIcon(provider.availabilityStatus || (provider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}
                    <span className="ml-1">{getStatusText(provider.availabilityStatus || (provider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}</span>
                  </span>
                  <button 
                    onClick={() => handleEditProvider(provider)}
                    className="text-blue-600 hover:text-blue-900"
                    title="Edit Provider"
                  >
                    <Edit className="w-4 h-4" />
                  </button>
                </div>
              </div>

              <div className="space-y-3">
                <div className="flex items-center text-sm text-gray-600">
                  <Phone className="w-4 h-4 mr-2" />
                  {provider.phone}
                </div>
                
                <div className="flex items-center text-sm text-gray-600">
                  <Star className="w-4 h-4 mr-2 text-yellow-500" />
                  <span className="font-medium">{provider.rating}</span>
                  <span className="text-gray-400 ml-1">/ 5.0</span>
                </div>

                <div className="text-sm text-gray-600">
                  <strong>Experience:</strong> {provider.experience} years
                </div>

                <div className="text-sm text-gray-600">
                  <strong>Qualifications:</strong> {provider.qualifications}
                </div>

                <div className="text-sm text-gray-600">
                  <strong>Services:</strong> {provider.services?.length || 0} available
                </div>
              </div>

              <div className="mt-4 pt-4 border-t border-gray-200">
                <div className="flex justify-between items-center gap-2">
                  <div className="text-sm text-gray-500 flex-shrink-0">
                    Joined {new Date(provider.createdAt).toLocaleDateString()}
                  </div>
                  <button
                    onClick={() => handleAvailabilityToggle(provider)}
                    className="px-3 py-1.5 text-xs font-medium rounded-lg whitespace-nowrap flex-shrink-0 transition-colors bg-blue-100 text-blue-700 hover:bg-blue-200"
                  >
                    Change Status
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Add Provider Modal */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Add New Provider
            </h3>
            <form className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Full Name
                </label>
                <input
                  type="text"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter provider name"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email
                </label>
                <input
                  type="email"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter email address"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Phone
                </label>
                <input
                  type="tel"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter phone number"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Qualifications
                </label>
                <textarea
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  rows={3}
                  placeholder="Enter qualifications and certifications"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Experience (years)
                </label>
                <input
                  type="number"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter years of experience"
                />
              </div>
              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                >
                  Add Provider
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Provider Modal */}
      {showEditModal && selectedProvider && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Edit Provider Details
            </h3>
            <form 
              onSubmit={(e) => {
                e.preventDefault();
                handleUpdateProvider(new FormData(e.currentTarget));
              }}
              className="space-y-4"
            >
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Full Name
                  </label>
                  <input
                    type="text"
                    name="name"
                    required
                    defaultValue={selectedProvider.name}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Email
                  </label>
                  <input
                    type="email"
                    name="email"
                    required
                    defaultValue={selectedProvider.email}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Phone
                  </label>
                  <input
                    type="tel"
                    name="phone"
                    required
                    defaultValue={selectedProvider.phone}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Experience (years)
                  </label>
                  <input
                    type="number"
                    name="experience"
                    required
                    defaultValue={selectedProvider.experience}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Qualifications
                  </label>
                  <textarea
                    name="qualifications"
                    required
                    defaultValue={selectedProvider.qualifications}
                    rows={3}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Rating
                  </label>
                  <div className="flex items-center">
                    <Star className="w-5 h-5 text-yellow-500 mr-1" />
                    <span className="text-lg font-semibold">{selectedProvider.rating}</span>
                    <span className="text-gray-500 ml-1">/ 5.0</span>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">Rating is managed by the system</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Current Status
                  </label>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    getStatusColor(selectedProvider.availabilityStatus || (selectedProvider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))
                  }`}>
                    {getStatusIcon(selectedProvider.availabilityStatus || (selectedProvider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}
                    <span className="ml-1">{getStatusText(selectedProvider.availabilityStatus || (selectedProvider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}</span>
                  </span>
                  <p className="text-xs text-gray-500 mt-1">Use "Change Status" button on the card to modify</p>
                </div>
              </div>

              <div className="bg-blue-50 border border-blue-200 rounded-md p-3">
                <p className="text-sm text-blue-800">
                  <strong>Note:</strong> Only basic details can be edited here. Use "Change Status" button on the provider card to modify availability status.
                </p>
              </div>

              <div className="flex justify-end space-x-3">
                <button
                  type="button"
                  onClick={() => setShowEditModal(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={updateProviderMutation.isLoading}
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
                >
                  {updateProviderMutation.isLoading ? 'Updating...' : 'Update Provider'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Change Status Modal */}
      {showStatusModal && selectedProvider && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Change Provider Status
            </h3>
            <p className="text-sm text-gray-600 mb-4">
              Provider: <span className="font-medium">{selectedProvider.name}</span>
            </p>
            <p className="text-sm text-gray-600 mb-4">
              Current Status: <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(selectedProvider.availabilityStatus || (selectedProvider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}`}>
                {getStatusIcon(selectedProvider.availabilityStatus || (selectedProvider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}
                <span className="ml-1">{getStatusText(selectedProvider.availabilityStatus || (selectedProvider.isAvailable ? 'AVAILABLE' : 'OFF_DUTY'))}</span>
              </span>
            </p>
            
            <div className="space-y-3 mb-6">
              <button
                onClick={() => handleStatusChange('AVAILABLE')}
                disabled={updateStatusMutation.isLoading}
                className="w-full px-4 py-3 text-left border-2 border-green-200 rounded-lg hover:bg-green-50 transition-colors disabled:opacity-50"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <div className="font-medium text-gray-900">Available</div>
                    <div className="text-sm text-gray-500">Ready for new bookings</div>
                  </div>
                  <CheckCircle className="w-5 h-5 text-green-600" />
                </div>
              </button>
              
              <button
                onClick={() => handleStatusChange('BUSY')}
                disabled={updateStatusMutation.isLoading}
                className="w-full px-4 py-3 text-left border-2 border-yellow-200 rounded-lg hover:bg-yellow-50 transition-colors disabled:opacity-50"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <div className="font-medium text-gray-900">Busy</div>
                    <div className="text-sm text-gray-500">Currently occupied with bookings</div>
                  </div>
                  <AlertCircle className="w-5 h-5 text-yellow-600" />
                </div>
              </button>
              
              <button
                onClick={() => handleStatusChange('OFF_DUTY')}
                disabled={updateStatusMutation.isLoading}
                className="w-full px-4 py-3 text-left border-2 border-red-200 rounded-lg hover:bg-red-50 transition-colors disabled:opacity-50"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <div className="font-medium text-gray-900">Off Duty</div>
                    <div className="text-sm text-gray-500">Not working currently</div>
                  </div>
                  <XCircle className="w-5 h-5 text-red-600" />
                </div>
              </button>
              
              <button
                onClick={() => handleStatusChange('ON_LEAVE')}
                disabled={updateStatusMutation.isLoading}
                className="w-full px-4 py-3 text-left border-2 border-gray-200 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <div className="font-medium text-gray-900">On Leave</div>
                    <div className="text-sm text-gray-500">On vacation or extended leave</div>
                  </div>
                  <XCircle className="w-5 h-5 text-gray-600" />
                </div>
              </button>
            </div>

            <div className="flex justify-end">
              <button
                type="button"
                onClick={() => {
                  setShowStatusModal(false);
                  setSelectedProvider(null);
                }}
                disabled={updateStatusMutation.isLoading}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 disabled:opacity-50"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Providers;
