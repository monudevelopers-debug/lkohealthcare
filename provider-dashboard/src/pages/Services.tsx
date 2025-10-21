import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  Plus, 
  Search,
  CheckCircle,
  XCircle,
  Clock,
  AlertCircle,
  Tag,
  DollarSign,
  History,
  FileText,
  Trash2,
  Eye
} from 'lucide-react';

import { getCurrentProvider } from '../services/api';

interface Service {
  id: string;
  name: string;
  description: string;
  price: number;
  duration: number;
  category: {
    id: string;
    name: string;
  };
}

interface ServiceRequest {
  id: string;
  service: Service;
  requestType: 'ADD' | 'REMOVE';
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  requestedAt: string;
  reviewedAt?: string;
  reviewedBy?: {
    id: string;
    name: string;
  };
  rejectionReason?: string;
  notes?: string;
}

const Services: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState<'approved' | 'pending' | 'available'>('approved');
  const [showRequestModal, setShowRequestModal] = useState(false);
  const [selectedService, setSelectedService] = useState<Service | null>(null);
  const [requestNotes, setRequestNotes] = useState('');
  const [showHistoryModal, setShowHistoryModal] = useState(false);
  
  const queryClient = useQueryClient();

  // Get current provider
  const { data: profileData } = useQuery('current-provider', getCurrentProvider);
  // Backend now returns Provider directly (not nested in { provider: {...} })
  const providerId = profileData?.id;

  // Fetch provider's current approved services
  const { data: approvedServices = [], isLoading: loadingApproved } = useQuery(
    ['provider-services', providerId],
    async () => {
      if (!providerId) return [];
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/providers/${providerId}/services`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) throw new Error('Failed to fetch services');
      return response.json();
    },
    {
      enabled: !!providerId,
      refetchInterval: 30000,
    }
  );

  // Fetch all available services
  const { data: allServices = [] } = useQuery(
    'all-services',
    async () => {
      const response = await fetch('http://localhost:8080/api/services/active');
      if (!response.ok) throw new Error('Failed to fetch all services');
      return response.json();
    }
  );

  // Fetch service requests (pending + history)
  const { data: serviceRequests = [], isLoading: loadingRequests } = useQuery(
    ['service-requests', providerId],
    async () => {
      if (!providerId) return [];
      const token = localStorage.getItem('token');
      const response = await fetch(`http://localhost:8080/api/service-requests/providers/${providerId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      if (!response.ok) throw new Error('Failed to fetch requests');
      return response.json();
    },
    {
      enabled: !!providerId,
      refetchInterval: 15000,
    }
  );

  // Request service addition mutation
  const requestAddMutation = useMutation(
    async ({ serviceId, notes }: { serviceId: string; notes: string }) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/service-requests/providers/${providerId}/services/${serviceId}/request-add`,
        {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ notes }),
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to request service');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['service-requests', providerId]);
        setShowRequestModal(false);
        setRequestNotes('');
        alert('✅ Service request submitted! Awaiting admin approval.');
      },
      onError: (error: any) => {
        alert('❌ ' + (error.message || 'Failed to submit request'));
      },
    }
  );

  // Request service removal mutation
  const requestRemoveMutation = useMutation(
    async ({ serviceId, notes }: { serviceId: string; notes: string }) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/service-requests/providers/${providerId}/services/${serviceId}/request-remove`,
        {
          method: 'POST',
          headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ notes }),
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to request service removal');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['service-requests', providerId]);
        alert('✅ Removal request submitted! Awaiting admin approval.');
      },
      onError: (error: any) => {
        alert('❌ ' + (error.message || 'Failed to submit request'));
      },
    }
  );

  // Filter logic
  const pendingRequests = serviceRequests.filter((r: ServiceRequest) => r.status === 'PENDING');
  const approvedServiceIds = approvedServices.map((s: Service) => s.id);
  const pendingServiceIds = pendingRequests.map((r: ServiceRequest) => r.service.id);
  
  const availableToRequest = allServices.filter((s: Service) => 
    !approvedServiceIds.includes(s.id) && 
    !pendingServiceIds.includes(s.id)
  );

  const filteredApproved = approvedServices.filter((s: Service) =>
    s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    s.description.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const filteredAvailable = availableToRequest.filter((s: Service) =>
    s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    s.description.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleRequestService = (service: Service) => {
    setSelectedService(service);
    setShowRequestModal(true);
  };

  const handleConfirmRequest = () => {
    if (selectedService) {
      requestAddMutation.mutate({ serviceId: selectedService.id, notes: requestNotes });
    }
  };

  const handleRequestRemoval = (service: Service) => {
    if (window.confirm(`Request to remove "${service.name}"?\n\nThis will require admin approval.`)) {
      const notes = prompt('Optional: Reason for removal');
      requestRemoveMutation.mutate({ serviceId: service.id, notes: notes || '' });
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Services</h1>
          <p className="text-gray-600">Manage services you offer (subject to admin approval)</p>
        </div>
        <button
          onClick={() => setShowHistoryModal(true)}
          className="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 flex items-center gap-2"
        >
          <History className="w-4 h-4" />
          Request History
        </button>
      </div>

      {/* Tabs */}
      <div className="bg-white rounded-lg shadow-sm border">
        <div className="flex border-b">
          <button
            onClick={() => setActiveTab('approved')}
            className={`flex-1 px-6 py-3 text-sm font-medium ${
              activeTab === 'approved'
                ? 'border-b-2 border-blue-600 text-blue-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            <div className="flex items-center justify-center gap-2">
              <CheckCircle className="w-4 h-4" />
              My Approved Services ({approvedServices.length})
            </div>
          </button>
          <button
            onClick={() => setActiveTab('pending')}
            className={`flex-1 px-6 py-3 text-sm font-medium ${
              activeTab === 'pending'
                ? 'border-b-2 border-yellow-600 text-yellow-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            <div className="flex items-center justify-center gap-2">
              <Clock className="w-4 h-4" />
              Pending Approval ({pendingRequests.length})
            </div>
          </button>
          <button
            onClick={() => setActiveTab('available')}
            className={`flex-1 px-6 py-3 text-sm font-medium ${
              activeTab === 'available'
                ? 'border-b-2 border-green-600 text-green-600'
                : 'text-gray-600 hover:text-gray-900'
            }`}
          >
            <div className="flex items-center justify-center gap-2">
              <Plus className="w-4 h-4" />
              Available to Request ({availableToRequest.length})
            </div>
          </button>
        </div>

        {/* Search Bar */}
        <div className="p-4 border-b">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            <input
              type="text"
              placeholder="Search services..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>

        {/* Tab Content */}
        <div className="p-6">
          {/* Approved Services Tab */}
          {activeTab === 'approved' && (
            <div className="space-y-4">
              {loadingApproved ? (
                <div className="text-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-2"></div>
                  <p className="text-sm text-gray-500">Loading services...</p>
                </div>
              ) : filteredApproved.length === 0 ? (
                <div className="text-center py-12">
                  <Tag className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500 mb-2">No approved services yet</p>
                  <p className="text-sm text-gray-400">Request services from the "Available to Request" tab</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {filteredApproved.map((service: Service) => (
                    <div key={service.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                      <div className="flex items-start justify-between mb-3">
                        <div className="flex items-center gap-3">
                          <div className="h-10 w-10 rounded-lg bg-green-100 flex items-center justify-center">
                            <CheckCircle className="h-5 w-5 text-green-600" />
                          </div>
                          <div>
                            <h3 className="font-semibold text-gray-900">{service.name}</h3>
                            <p className="text-xs text-gray-500">{service.category.name}</p>
                          </div>
                        </div>
                      </div>

                      <p className="text-sm text-gray-600 mb-3 line-clamp-2">{service.description}</p>

                      <div className="flex items-center justify-between text-sm mb-3">
                        <div className="flex items-center text-gray-700">
                          <DollarSign className="w-4 h-4 mr-1" />
                          <span className="font-medium">₹{service.price}</span>
                        </div>
                        <div className="flex items-center text-gray-600">
                          <Clock className="w-4 h-4 mr-1" />
                          <span>{service.duration} min</span>
                        </div>
                      </div>

                      <button
                        onClick={() => handleRequestRemoval(service)}
                        className="w-full px-3 py-2 text-sm font-medium text-red-600 bg-red-50 rounded-md hover:bg-red-100 flex items-center justify-center gap-2"
                      >
                        <Trash2 className="w-4 h-4" />
                        Request Removal
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Pending Requests Tab */}
          {activeTab === 'pending' && (
            <div className="space-y-4">
              {loadingRequests ? (
                <div className="text-center py-8">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-yellow-600 mx-auto mb-2"></div>
                  <p className="text-sm text-gray-500">Loading requests...</p>
                </div>
              ) : pendingRequests.length === 0 ? (
                <div className="text-center py-12">
                  <Clock className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500 mb-2">No pending requests</p>
                  <p className="text-sm text-gray-400">Your service requests will appear here</p>
                </div>
              ) : (
                <div className="space-y-3">
                  {pendingRequests.map((request: ServiceRequest) => (
                    <div key={request.id} className="border-2 border-yellow-300 bg-yellow-50 rounded-lg p-4">
                      <div className="flex items-start justify-between">
                        <div className="flex items-start gap-3 flex-1">
                          <div className="h-10 w-10 rounded-lg bg-yellow-200 flex items-center justify-center flex-shrink-0">
                            <Clock className="h-5 w-5 text-yellow-700" />
                          </div>
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-1">
                              <h3 className="font-semibold text-gray-900">{request.service.name}</h3>
                              <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                                request.requestType === 'ADD' ? 'bg-blue-100 text-blue-800' : 'bg-orange-100 text-orange-800'
                              }`}>
                                {request.requestType}
                              </span>
                            </div>
                            <p className="text-sm text-gray-600 mb-2">{request.service.description}</p>
                            <div className="flex items-center gap-4 text-xs text-gray-500">
                              <span>Requested: {new Date(request.requestedAt).toLocaleDateString('en-IN')}</span>
                              <span className="flex items-center gap-1">
                                <DollarSign className="w-3 h-3" />
                                ₹{request.service.price}
                              </span>
                            </div>
                            {request.notes && (
                              <div className="mt-2 p-2 bg-white rounded border border-yellow-200">
                                <p className="text-xs text-gray-600">
                                  <strong>Your notes:</strong> {request.notes}
                                </p>
                              </div>
                            )}
                          </div>
                        </div>
                        <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 whitespace-nowrap">
                          ⏳ Pending Review
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Available to Request Tab */}
          {activeTab === 'available' && (
            <div className="space-y-4">
              {filteredAvailable.length === 0 ? (
                <div className="text-center py-12">
                  <Tag className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500 mb-2">
                    {searchTerm ? 'No matching services found' : 'All services requested or approved'}
                  </p>
                  <p className="text-sm text-gray-400">
                    {searchTerm ? 'Try a different search term' : 'You can request removal from the approved tab'}
                  </p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {filteredAvailable.map((service: Service) => (
                    <div key={service.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                      <div className="flex items-start gap-3 mb-3">
                        <div className="h-10 w-10 rounded-lg bg-blue-100 flex items-center justify-center flex-shrink-0">
                          <Tag className="h-5 w-5 text-blue-600" />
                        </div>
                        <div>
                          <h3 className="font-semibold text-gray-900">{service.name}</h3>
                          <p className="text-xs text-gray-500">{service.category.name}</p>
                        </div>
                      </div>

                      <p className="text-sm text-gray-600 mb-3 line-clamp-2">{service.description}</p>

                      <div className="flex items-center justify-between text-sm mb-3">
                        <div className="flex items-center text-gray-700">
                          <DollarSign className="w-4 h-4 mr-1" />
                          <span className="font-medium">₹{service.price}</span>
                        </div>
                        <div className="flex items-center text-gray-600">
                          <Clock className="w-4 h-4 mr-1" />
                          <span>{service.duration} min</span>
                        </div>
                      </div>

                      <button
                        onClick={() => handleRequestService(service)}
                        className="w-full px-3 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 flex items-center justify-center gap-2"
                      >
                        <Plus className="w-4 h-4" />
                        Request Service
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Request Service Modal */}
      {showRequestModal && selectedService && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Request Service Addition
            </h3>

            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
              <h4 className="font-semibold text-gray-900 mb-2">{selectedService.name}</h4>
              <p className="text-sm text-gray-600 mb-2">{selectedService.description}</p>
              <div className="flex items-center justify-between text-sm">
                <span className="text-gray-700">Price: ₹{selectedService.price}</span>
                <span className="text-gray-700">Duration: {selectedService.duration} min</span>
              </div>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Why should you be approved for this service?
                <span className="text-xs text-gray-500 ml-1">(Optional but recommended)</span>
              </label>
              <textarea
                value={requestNotes}
                onChange={(e) => setRequestNotes(e.target.value)}
                placeholder="E.g., I have 5 years of experience in home nursing care with RN certification..."
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                rows={4}
              />
              <p className="text-xs text-gray-500 mt-1">
                💡 Tip: Mention your relevant qualifications, certifications, and experience
              </p>
            </div>

            <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3 mb-4">
              <div className="flex items-start gap-2">
                <AlertCircle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
                <div className="text-sm text-yellow-800">
                  <p className="font-medium mb-1">Admin Approval Required</p>
                  <p className="text-xs">
                    Your request will be reviewed by an admin. You'll be notified once it's approved or if additional information is needed.
                  </p>
                </div>
              </div>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                onClick={() => {
                  setShowRequestModal(false);
                  setRequestNotes('');
                  setSelectedService(null);
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmRequest}
                disabled={requestAddMutation.isLoading}
                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              >
                {requestAddMutation.isLoading ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    Submitting...
                  </>
                ) : (
                  <>
                    <FileText className="w-4 h-4" />
                    Submit Request
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Request History Modal */}
      {showHistoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Service Request History
            </h3>

            <div className="space-y-3">
              {serviceRequests.length === 0 ? (
                <div className="text-center py-8">
                  <History className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                  <p className="text-gray-500">No request history</p>
                </div>
              ) : (
                serviceRequests.map((request: ServiceRequest) => (
                  <div 
                    key={request.id}
                    className={`border-2 rounded-lg p-4 ${
                      request.status === 'APPROVED' ? 'border-green-300 bg-green-50' :
                      request.status === 'REJECTED' ? 'border-red-300 bg-red-50' :
                      'border-yellow-300 bg-yellow-50'
                    }`}
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex items-start gap-3 flex-1">
                        <div className={`h-10 w-10 rounded-lg flex items-center justify-center flex-shrink-0 ${
                          request.status === 'APPROVED' ? 'bg-green-200' :
                          request.status === 'REJECTED' ? 'bg-red-200' :
                          'bg-yellow-200'
                        }`}>
                          {request.status === 'APPROVED' ? <CheckCircle className="h-5 w-5 text-green-700" /> :
                           request.status === 'REJECTED' ? <XCircle className="h-5 w-5 text-red-700" /> :
                           <Clock className="h-5 w-5 text-yellow-700" />}
                        </div>
                        <div className="flex-1">
                          <div className="flex items-center gap-2 mb-1">
                            <h3 className="font-semibold text-gray-900">{request.service.name}</h3>
                            <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                              request.requestType === 'ADD' ? 'bg-blue-100 text-blue-800' : 'bg-orange-100 text-orange-800'
                            }`}>
                              {request.requestType}
                            </span>
                          </div>
                          
                          <div className="space-y-1 text-xs text-gray-600">
                            <p>Requested: {new Date(request.requestedAt).toLocaleString('en-IN')}</p>
                            {request.reviewedAt && (
                              <p>Reviewed: {new Date(request.reviewedAt).toLocaleString('en-IN')}</p>
                            )}
                            {request.reviewedBy && (
                              <p>Reviewed by: {request.reviewedBy.name}</p>
                            )}
                          </div>

                          {request.notes && (
                            <div className="mt-2 p-2 bg-white rounded border border-gray-200">
                              <p className="text-xs text-gray-600">
                                <strong>Your notes:</strong> {request.notes}
                              </p>
                            </div>
                          )}

                          {request.rejectionReason && (
                            <div className="mt-2 p-3 bg-red-100 border border-red-300 rounded-lg">
                              <p className="text-xs font-medium text-red-800 mb-1">
                                ❌ Rejection Reason:
                              </p>
                              <p className="text-sm text-red-700">{request.rejectionReason}</p>
                            </div>
                          )}
                        </div>
                      </div>
                      <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium whitespace-nowrap ${
                        request.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                        request.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                        'bg-yellow-100 text-yellow-800'
                      }`}>
                        {request.status === 'APPROVED' ? '✅ Approved' :
                         request.status === 'REJECTED' ? '❌ Rejected' :
                         '⏳ Pending'}
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowHistoryModal(false)}
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
