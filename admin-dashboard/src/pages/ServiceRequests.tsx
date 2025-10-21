import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  CheckCircle,
  XCircle,
  Clock,
  User,
  Tag,
  Star,
  FileText,
  AlertCircle,
  Filter,
  Search,
  Eye
} from 'lucide-react';

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

interface Provider {
  id: string;
  name: string;
  email: string;
  phone: string;
  qualification: string;
  experience: number;
  rating: number;
  totalRatings: number;
  isVerified: boolean;
}

interface ServiceRequest {
  id: string;
  provider: Provider;
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

const ServiceRequests: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'ADD' | 'REMOVE'>('ALL');
  const [selectedRequest, setSelectedRequest] = useState<ServiceRequest | null>(null);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [rejectionReason, setRejectionReason] = useState('');
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  
  const queryClient = useQueryClient();

  // Fetch pending service requests
  const { data: pendingRequests = [], isLoading } = useQuery(
    'pending-service-requests',
    async () => {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/service-requests/pending', {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      if (!response.ok) throw new Error('Failed to fetch pending requests');
      return response.json();
    },
    {
      refetchInterval: 10000, // Refetch every 10 seconds
    }
  );

  // Approve request mutation
  const approveMutation = useMutation(
    async (requestId: string) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/service-requests/${requestId}/approve`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to approve request');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries('pending-service-requests');
        alert('✅ Service request approved successfully!');
      },
      onError: (error: any) => {
        alert('❌ ' + (error.message || 'Failed to approve request'));
      },
    }
  );

  // Reject request mutation
  const rejectMutation = useMutation(
    async ({ requestId, reason }: { requestId: string; reason: string }) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/service-requests/${requestId}/reject`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ reason }),
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to reject request');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries('pending-service-requests');
        setShowRejectModal(false);
        setRejectionReason('');
        setSelectedRequest(null);
        alert('✅ Service request rejected.');
      },
      onError: (error: any) => {
        alert('❌ ' + (error.message || 'Failed to reject request'));
      },
    }
  );

  // Filter requests
  const filteredRequests = pendingRequests.filter((request: ServiceRequest) => {
    const matchesSearch = 
      request.provider.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      request.service.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      request.provider.email.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesType = typeFilter === 'ALL' || request.requestType === typeFilter;
    
    return matchesSearch && matchesType;
  });

  const handleApprove = (request: ServiceRequest) => {
    if (window.confirm(
      `Approve this request?\n\n` +
      `Provider: ${request.provider.name}\n` +
      `Service: ${request.service.name}\n` +
      `Action: ${request.requestType}\n\n` +
      `This will immediately ${request.requestType === 'ADD' ? 'add' : 'remove'} the service.`
    )) {
      approveMutation.mutate(request.id);
    }
  };

  const handleReject = (request: ServiceRequest) => {
    setSelectedRequest(request);
    setShowRejectModal(true);
  };

  const handleConfirmReject = () => {
    if (!rejectionReason.trim()) {
      alert('Please provide a reason for rejection');
      return;
    }
    if (selectedRequest) {
      rejectMutation.mutate({ requestId: selectedRequest.id, reason: rejectionReason });
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Service Requests</h1>
          <p className="text-gray-600">Review and approve provider service requests</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="bg-yellow-100 border border-yellow-300 px-4 py-2 rounded-lg">
            <span className="text-sm font-medium text-yellow-800">
              {pendingRequests.length} Pending Request{pendingRequests.length !== 1 ? 's' : ''}
            </span>
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="bg-white p-4 rounded-lg shadow-sm border">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search by provider name, service, or email..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
          <div className="flex gap-2">
            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value as any)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Types</option>
              <option value="ADD">Add Requests</option>
              <option value="REMOVE">Remove Requests</option>
            </select>
          </div>
        </div>
      </div>

      {/* Requests List */}
      <div className="space-y-4">
        {isLoading ? (
          <div className="bg-white rounded-lg shadow-sm border p-8 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-2"></div>
            <p className="text-sm text-gray-500">Loading requests...</p>
          </div>
        ) : filteredRequests.length === 0 ? (
          <div className="bg-white rounded-lg shadow-sm border p-12 text-center">
            <CheckCircle className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500 text-lg mb-2">
              {searchTerm || typeFilter !== 'ALL' ? 'No matching requests found' : 'No pending requests'}
            </p>
            <p className="text-sm text-gray-400">
              {searchTerm || typeFilter !== 'ALL' ? 'Try adjusting your filters' : 'All service requests have been reviewed'}
            </p>
          </div>
        ) : (
          filteredRequests.map((request: ServiceRequest) => (
            <div key={request.id} className="bg-white rounded-lg shadow-sm border-2 border-yellow-300 p-6 hover:shadow-md transition-shadow">
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-start gap-4 flex-1">
                  <div className="h-12 w-12 rounded-lg bg-yellow-100 flex items-center justify-center flex-shrink-0">
                    <Clock className="h-6 w-6 text-yellow-600" />
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-2">
                      <h3 className="text-lg font-semibold text-gray-900">
                        {request.requestType === 'ADD' ? 'Add Service Request' : 'Remove Service Request'}
                      </h3>
                      <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                        request.requestType === 'ADD' ? 'bg-blue-100 text-blue-800' : 'bg-orange-100 text-orange-800'
                      }`}>
                        {request.requestType}
                      </span>
                    </div>
                    <p className="text-sm text-gray-500">
                      Requested on {new Date(request.requestedAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                    </p>
                  </div>
                </div>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 whitespace-nowrap">
                  ⏳ Pending Review
                </span>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {/* Provider Information */}
                <div className="bg-blue-50 rounded-lg p-4">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                    <User className="w-4 h-4" />
                    Provider Details
                  </h4>
                  <div className="space-y-2 text-sm">
                    <div>
                      <span className="font-medium text-gray-700">Name:</span>
                      <p className="text-gray-900">{request.provider.name}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Email:</span>
                      <p className="text-gray-900">{request.provider.email}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Phone:</span>
                      <p className="text-gray-900">{request.provider.phone}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Experience:</span>
                      <p className="text-gray-900">{request.provider.experience} years</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Qualifications:</span>
                      <p className="text-gray-900">{request.provider.qualification}</p>
                    </div>
                    <div className="flex items-center gap-2">
                      <span className="font-medium text-gray-700">Rating:</span>
                      <div className="flex items-center">
                        <Star className="w-4 h-4 text-yellow-500 mr-1" />
                        <span className="text-gray-900">{request.provider.rating.toFixed(1)}</span>
                        <span className="text-gray-500 ml-1">({request.provider.totalRatings} reviews)</span>
                      </div>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Verified:</span>
                      <span className={`ml-2 inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                        request.provider.isVerified ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                      }`}>
                        {request.provider.isVerified ? '✅ Verified' : '❌ Not Verified'}
                      </span>
                    </div>
                  </div>
                </div>

                {/* Service Information */}
                <div className="bg-green-50 rounded-lg p-4">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                    <Tag className="w-4 h-4" />
                    Service Details
                  </h4>
                  <div className="space-y-2 text-sm">
                    <div>
                      <span className="font-medium text-gray-700">Service Name:</span>
                      <p className="text-gray-900 font-semibold">{request.service.name}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Category:</span>
                      <p className="text-gray-900">{request.service.category.name}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Description:</span>
                      <p className="text-gray-900">{request.service.description}</p>
                    </div>
                    <div className="flex items-center justify-between pt-2 border-t border-green-200">
                      <div>
                        <span className="font-medium text-gray-700">Price:</span>
                        <p className="text-gray-900 font-semibold">₹{request.service.price}</p>
                      </div>
                      <div>
                        <span className="font-medium text-gray-700">Duration:</span>
                        <p className="text-gray-900">{request.service.duration} min</p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* Provider Notes */}
              {request.notes && (
                <div className="mt-4 bg-purple-50 border border-purple-200 rounded-lg p-4">
                  <h4 className="font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <FileText className="w-4 h-4" />
                    Provider's Notes
                  </h4>
                  <p className="text-sm text-gray-900 whitespace-pre-wrap leading-relaxed">
                    {request.notes}
                  </p>
                </div>
              )}

              {/* Action Buttons */}
              <div className="mt-6 flex justify-end gap-3">
                <button
                  onClick={() => {
                    setSelectedRequest(request);
                    setShowDetailsModal(true);
                  }}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 flex items-center gap-2"
                >
                  <Eye className="w-4 h-4" />
                  View Full Details
                </button>
                <button
                  onClick={() => handleReject(request)}
                  className="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 flex items-center gap-2"
                >
                  <XCircle className="w-4 h-4" />
                  Reject
                </button>
                <button
                  onClick={() => handleApprove(request)}
                  disabled={approveMutation.isLoading}
                  className="px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                >
                  {approveMutation.isLoading ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                      Approving...
                    </>
                  ) : (
                    <>
                      <CheckCircle className="w-4 h-4" />
                      Approve
                    </>
                  )}
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Reject Modal */}
      {showRejectModal && selectedRequest && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Reject Service Request
            </h3>

            <div className="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
              <p className="text-sm text-gray-900 mb-2">
                <strong>Provider:</strong> {selectedRequest.provider.name}
              </p>
              <p className="text-sm text-gray-900 mb-2">
                <strong>Service:</strong> {selectedRequest.service.name}
              </p>
              <p className="text-sm text-gray-900">
                <strong>Action:</strong> {selectedRequest.requestType}
              </p>
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Rejection Reason <span className="text-red-600">*</span>
              </label>
              <textarea
                value={rejectionReason}
                onChange={(e) => setRejectionReason(e.target.value)}
                placeholder="E.g., Insufficient qualifications. Please provide RN certification..."
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-red-500 focus:border-transparent"
                rows={4}
                required
              />
              <p className="text-xs text-gray-500 mt-1">
                Provider will see this reason
              </p>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                onClick={() => {
                  setShowRejectModal(false);
                  setRejectionReason('');
                  setSelectedRequest(null);
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={handleConfirmReject}
                disabled={rejectMutation.isLoading}
                className="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              >
                {rejectMutation.isLoading ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    Rejecting...
                  </>
                ) : (
                  <>
                    <XCircle className="w-4 h-4" />
                    Confirm Rejection
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Details Modal */}
      {showDetailsModal && selectedRequest && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-medium text-gray-900 mb-6">
              Complete Request Details
            </h3>

            <div className="space-y-6">
              {/* Request Info */}
              <div className="bg-gray-50 rounded-lg p-4">
                <h4 className="font-semibold text-gray-900 mb-3">Request Information</h4>
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="font-medium text-gray-700">Request ID:</span>
                    <p className="text-xs font-mono text-gray-600 mt-1">{selectedRequest.id}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Action:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.requestType}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Requested:</span>
                    <p className="text-gray-900 mt-1">
                      {new Date(selectedRequest.requestedAt).toLocaleString('en-IN')}
                    </p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Status:</span>
                    <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800 mt-1">
                      {selectedRequest.status}
                    </span>
                  </div>
                </div>
              </div>

              {/* Provider Details (Full) */}
              <div className="bg-blue-50 rounded-lg p-4">
                <h4 className="font-semibold text-gray-900 mb-3">Complete Provider Profile</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="font-medium text-gray-700">Name:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.provider.name}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Email:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.provider.email}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Phone:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.provider.phone}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Experience:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.provider.experience} years</p>
                  </div>
                  <div className="md:col-span-2">
                    <span className="font-medium text-gray-700">Qualifications:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.provider.qualification}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Rating:</span>
                    <div className="flex items-center mt-1">
                      <Star className="w-4 h-4 text-yellow-500 mr-1" />
                      <span className="text-gray-900">{selectedRequest.provider.rating.toFixed(1)} / 5.0</span>
                      <span className="text-gray-500 ml-2">({selectedRequest.provider.totalRatings} reviews)</span>
                    </div>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Verification:</span>
                    <span className={`ml-2 inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                      selectedRequest.provider.isVerified ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    }`}>
                      {selectedRequest.provider.isVerified ? '✅ Verified' : '❌ Not Verified'}
                    </span>
                  </div>
                </div>
              </div>

              {/* Service Details */}
              <div className="bg-green-50 rounded-lg p-4">
                <h4 className="font-semibold text-gray-900 mb-3">Service Information</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                  <div className="md:col-span-2">
                    <span className="font-medium text-gray-700">Service Name:</span>
                    <p className="text-gray-900 font-semibold mt-1">{selectedRequest.service.name}</p>
                  </div>
                  <div className="md:col-span-2">
                    <span className="font-medium text-gray-700">Category:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.service.category.name}</p>
                  </div>
                  <div className="md:col-span-2">
                    <span className="font-medium text-gray-700">Description:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.service.description}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Price:</span>
                    <p className="text-gray-900 font-semibold mt-1">₹{selectedRequest.service.price}</p>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Duration:</span>
                    <p className="text-gray-900 mt-1">{selectedRequest.service.duration} minutes</p>
                  </div>
                </div>
              </div>

              {/* Provider Notes */}
              {selectedRequest.notes && (
                <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
                  <h4 className="font-semibold text-gray-900 mb-2">Provider's Justification</h4>
                  <p className="text-sm text-gray-900 whitespace-pre-wrap leading-relaxed">
                    {selectedRequest.notes}
                  </p>
                </div>
              )}
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

export default ServiceRequests;

