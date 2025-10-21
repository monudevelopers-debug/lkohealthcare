import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  AlertCircle, 
  CheckCircle, 
  XCircle, 
  User, 
  Calendar,
  MapPin,
  Phone,
  Mail,
  Clock,
  Tag,
  DollarSign
} from 'lucide-react';
import { toast } from 'react-hot-toast';

interface Booking {
  id: string;
  user: {
    id: string;
    name: string;
    email: string;
    phone: string;
  };
  service: {
    id: string;
    name: string;
    price: number;
    category: {
      name: string;
    };
  };
  provider?: {
    id: string;
    name: string;
    email: string;
    phone: string;
  };
  scheduledDate: string;
  scheduledTime: string;
  duration: number;
  totalAmount: number;
  status: string;
  specialInstructions?: string;
  notes?: string;
  createdAt: string;
}

interface Provider {
  id: string;
  name: string;
  email: string;
  phone: string;
  qualifications: string;
  experience: number;
  rating: number;
  availabilityStatus: string;
}

interface RejectionRequest {
  id: string;
  booking: Booking;
  provider: Provider;
  rejectionReason: string;
  status: string;
  requestedAt: string;
}

const ManageBookings: React.FC = () => {
  const [selectedTab, setSelectedTab] = useState<'unassigned' | 'rejections'>('unassigned');
  const [selectedBooking, setSelectedBooking] = useState<Booking | null>(null);
  const [selectedProviderId, setSelectedProviderId] = useState('');
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedRejection, setSelectedRejection] = useState<RejectionRequest | null>(null);
  const [adminNotes, setAdminNotes] = useState('');
  
  const queryClient = useQueryClient();

  // Fetch bookings that need provider assignment (not cancelled or completed)
  const { data: unassignedBookings = [], isLoading: loadingUnassigned } = useQuery(
    'unassigned-bookings',
    async () => {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/bookings/unassigned', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('Failed to fetch bookings needing assignment');
      return response.json();
    },
    { refetchInterval: 15000 }
  );

  // Fetch pending rejection requests
  const { data: rejectionRequests = [], isLoading: loadingRejections } = useQuery(
    'rejection-requests',
    async () => {
      const token = localStorage.getItem('token');
      const response = await fetch('http://localhost:8080/api/booking-rejections/pending', {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('Failed to fetch rejection requests');
      return response.json();
    },
    { refetchInterval: 15000 }
  );

  // Fetch providers for assignment
  const { data: providersData } = useQuery(
    ['providers-for-assignment', selectedBooking?.id],
    async () => {
      if (!selectedBooking) return null;
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/bookings/${selectedBooking.id}/available-providers`,
        { headers: { 'Authorization': `Bearer ${token}` } }
      );
      if (!response.ok) throw new Error('Failed to fetch providers');
      return response.json();
    },
    { enabled: !!selectedBooking && showAssignModal }
  );

  // Assign provider mutation
  const assignProviderMutation = useMutation(
    async ({ bookingId, providerId }: { bookingId: string; providerId: string }) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/bookings/${bookingId}/assign-provider/${providerId}`,
        {
          method: 'POST',
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to assign provider');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries('unassigned-bookings');
        setShowAssignModal(false);
        toast.success('✅ Provider assigned successfully!');
      },
      onError: (error: any) => {
        toast.error(`❌ ${error.message}`);
      }
    }
  );

  // Approve rejection mutation
  const approveRejectionMutation = useMutation(
    async ({ requestId, notes }: { requestId: string; notes: string }) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/booking-rejections/${requestId}/approve`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ notes })
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to approve rejection');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries('rejection-requests');
        queryClient.invalidateQueries('unassigned-bookings');
        setShowReviewModal(false);
        toast.success('✅ Rejection approved! Booking now available for reassignment.');
      },
      onError: (error: any) => {
        toast.error(`❌ ${error.message}`);
      }
    }
  );

  // Deny rejection mutation
  const denyRejectionMutation = useMutation(
    async ({ requestId, notes }: { requestId: string; notes: string }) => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/booking-rejections/${requestId}/deny`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({ notes })
        }
      );
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Failed to deny rejection');
      }
      return response.json();
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries('rejection-requests');
        setShowReviewModal(false);
        toast.success('✅ Rejection denied! Provider must complete the booking.');
      },
      onError: (error: any) => {
        toast.error(`❌ ${error.message}`);
      }
    }
  );

  const totalCount = unassignedBookings.length + rejectionRequests.length;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Manage Bookings</h1>
          <p className="text-gray-600">Assign providers to bookings and review rejection requests</p>
        </div>
        <div className="flex items-center gap-2 px-4 py-2 bg-blue-100 rounded-lg">
          <AlertCircle className="w-5 h-5 text-blue-600" />
          <span className="text-sm font-semibold text-blue-900">
            {totalCount} Item{totalCount !== 1 ? 's' : ''} Requiring Attention
          </span>
        </div>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex space-x-8">
          <button
            onClick={() => setSelectedTab('unassigned')}
            className={`whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm ${
              selectedTab === 'unassigned'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Bookings Needing Assignment ({unassignedBookings.length})
          </button>
          <button
            onClick={() => setSelectedTab('rejections')}
            className={`whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm ${
              selectedTab === 'rejections'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Provider Rejections ({rejectionRequests.length})
          </button>
        </nav>
      </div>

      {/* Unassigned Bookings Tab */}
      {selectedTab === 'unassigned' && (
        <div className="bg-white rounded-lg shadow-sm border">
          {loadingUnassigned ? (
            <div className="p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-2"></div>
              <p className="text-gray-600">Loading unassigned bookings...</p>
            </div>
          ) : unassignedBookings.length === 0 ? (
            <div className="p-12 text-center">
              <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
              <p className="text-gray-500 text-lg">All active bookings have providers assigned!</p>
              <p className="text-gray-400 text-sm mt-2">Bookings that are cancelled or completed are not shown here.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Customer</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Service</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date & Time</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Current Provider</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {unassignedBookings.map((booking: Booking) => (
                    <tr key={booking.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4">
                        <div className="text-sm font-medium text-gray-900">{booking.user.name}</div>
                        <div className="text-sm text-gray-500">{booking.user.phone}</div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900">{booking.service.name}</div>
                        <div className="text-xs text-gray-500">{booking.service.category.name}</div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm text-gray-900">{new Date(booking.scheduledDate).toLocaleDateString()}</div>
                        <div className="text-sm text-gray-500">{booking.scheduledTime}</div>
                      </td>
                      <td className="px-6 py-4">
                        {booking.provider ? (
                          <div>
                            <div className="text-sm font-medium text-gray-900">{booking.provider.name}</div>
                            <div className="text-xs text-gray-500">{booking.provider.phone}</div>
                          </div>
                        ) : (
                          <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                            Not Assigned
                          </span>
                        )}
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-sm font-semibold text-gray-900">₹{booking.totalAmount}</div>
                      </td>
                      <td className="px-6 py-4">
                        <button
                          onClick={() => {
                            setSelectedBooking(booking);
                            setShowAssignModal(true);
                          }}
                          className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                        >
                          {booking.provider ? 'Reassign' : 'Assign'} Provider
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Rejection Requests Tab */}
      {selectedTab === 'rejections' && (
        <div className="space-y-4">
          {loadingRejections ? (
            <div className="bg-white rounded-lg shadow-sm border p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-2"></div>
              <p className="text-gray-600">Loading rejection requests...</p>
            </div>
          ) : rejectionRequests.length === 0 ? (
            <div className="bg-white rounded-lg shadow-sm border p-12 text-center">
              <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
              <p className="text-gray-500 text-lg">No pending rejection requests!</p>
            </div>
          ) : (
            rejectionRequests.map((request: RejectionRequest) => (
              <div key={request.id} className="bg-white rounded-lg shadow-sm border p-6">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2">
                      <h3 className="text-lg font-semibold text-gray-900">
                        Provider Rejection Request
                      </h3>
                      <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                        PENDING REVIEW
                      </span>
                    </div>
                    <p className="text-sm text-gray-500">
                      Requested on {new Date(request.requestedAt).toLocaleString('en-IN')}
                    </p>
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  {/* Provider Info */}
                  <div className="bg-purple-50 rounded-lg p-4">
                    <h4 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                      <User className="w-4 h-4" />
                      Provider Details
                    </h4>
                    <div className="space-y-2 text-sm">
                      <p><span className="font-medium">Name:</span> {request.provider.name}</p>
                      <p><span className="font-medium">Email:</span> {request.provider.email}</p>
                      <p><span className="font-medium">Phone:</span> {request.provider.phone}</p>
                    </div>
                  </div>

                  {/* Booking Info */}
                  <div className="bg-blue-50 rounded-lg p-4">
                    <h4 className="font-semibold text-gray-900 mb-3 flex items-center gap-2">
                      <Calendar className="w-4 h-4" />
                      Booking Details
                    </h4>
                    <div className="space-y-2 text-sm">
                      <p><span className="font-medium">Customer:</span> {request.booking.user.name}</p>
                      <p><span className="font-medium">Service:</span> {request.booking.service.name}</p>
                      <p><span className="font-medium">Date:</span> {new Date(request.booking.scheduledDate).toLocaleDateString()}</p>
                    </div>
                  </div>
                </div>

                {/* Rejection Reason */}
                <div className="mt-4 bg-red-50 rounded-lg p-4">
                  <h4 className="font-semibold text-gray-900 mb-2 flex items-center gap-2">
                    <AlertCircle className="w-4 h-4 text-red-600" />
                    Rejection Reason
                  </h4>
                  <p className="text-sm text-gray-900 whitespace-pre-wrap">{request.rejectionReason}</p>
                </div>

                {/* Actions */}
                <div className="mt-4 flex gap-3">
                  <button
                    onClick={() => {
                      setSelectedRejection(request);
                      setShowReviewModal(true);
                      setAdminNotes('');
                    }}
                    className="flex-1 px-4 py-2 text-sm font-medium text-white bg-green-600 rounded-md hover:bg-green-700"
                  >
                    <CheckCircle className="w-4 h-4 inline mr-2" />
                    Approve (Cancel & Reassign)
                  </button>
                  <button
                    onClick={() => {
                      setSelectedRejection(request);
                      setShowReviewModal(true);
                      setAdminNotes('Provider must complete this booking as agreed.');
                    }}
                    className="flex-1 px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700"
                  >
                    <XCircle className="w-4 h-4 inline mr-2" />
                    Deny (Provider Must Complete)
                  </button>
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {/* Assign Provider Modal */}
      {showAssignModal && selectedBooking && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Assign Provider to Booking</h3>
            
            <div className="bg-blue-50 p-4 rounded-lg mb-4">
              <div className="grid grid-cols-2 gap-3 text-sm">
                <div>
                  <span className="font-medium">Service:</span> {selectedBooking.service.name}
                </div>
                <div>
                  <span className="font-medium">Customer:</span> {selectedBooking.user.name}
                </div>
                <div>
                  <span className="font-medium">Date:</span> {new Date(selectedBooking.scheduledDate).toLocaleDateString()}
                </div>
                <div>
                  <span className="font-medium">Time:</span> {selectedBooking.scheduledTime}
                </div>
              </div>
            </div>

            <div className="space-y-3 mb-6">
              <h4 className="font-medium text-gray-900">Select Available Provider</h4>
              {!providersData ? (
                <div className="text-center py-4">
                  <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto mb-2"></div>
                  <p className="text-sm text-gray-500">Loading providers...</p>
                </div>
              ) : providersData.length === 0 ? (
                <div className="text-center py-8 bg-yellow-50 rounded-lg">
                  <AlertCircle className="w-8 h-8 text-yellow-600 mx-auto mb-2" />
                  <p className="text-sm text-yellow-800">No available providers for this service</p>
                </div>
              ) : (
                <div className="space-y-2 max-h-96 overflow-y-auto">
                  {providersData.map((provider: Provider) => (
                    <button
                      key={provider.id}
                      onClick={() => setSelectedProviderId(provider.id)}
                      className={`w-full p-4 text-left border-2 rounded-lg transition-all ${
                        selectedProviderId === provider.id
                          ? 'border-blue-500 bg-blue-50'
                          : 'border-gray-200 hover:border-blue-300'
                      }`}
                    >
                      <div className="flex items-start justify-between">
                        <div>
                          <h5 className="font-semibold text-gray-900">{provider.name}</h5>
                          <p className="text-sm text-gray-600">{provider.qualifications}</p>
                          <div className="flex gap-4 mt-2 text-xs text-gray-500">
                            <span>⭐ {provider.rating}/5.0</span>
                            <span>{provider.experience} years</span>
                          </div>
                        </div>
                        {selectedProviderId === provider.id && (
                          <CheckCircle className="w-6 h-6 text-blue-600" />
                        )}
                      </div>
                    </button>
                  ))}
                </div>
              )}
            </div>

            <div className="flex justify-end gap-3">
              <button
                onClick={() => {
                  setShowAssignModal(false);
                  setSelectedProviderId('');
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  if (selectedProviderId) {
                    assignProviderMutation.mutate({
                      bookingId: selectedBooking.id,
                      providerId: selectedProviderId
                    });
                    setSelectedProviderId('');
                  }
                }}
                disabled={!selectedProviderId || assignProviderMutation.isLoading}
                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {assignProviderMutation.isLoading ? 'Assigning...' : 'Assign Provider'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Review Rejection Modal */}
      {showReviewModal && selectedRejection && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">Review Rejection Request</h3>
            
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Admin Notes (Optional)
              </label>
              <textarea
                value={adminNotes}
                onChange={(e) => setAdminNotes(e.target.value)}
                rows={4}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                placeholder="Add notes about your decision..."
              />
            </div>

            <div className="flex gap-3">
              <button
                onClick={() => setShowReviewModal(false)}
                className="flex-1 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  if (adminNotes.includes('must complete')) {
                    denyRejectionMutation.mutate({
                      requestId: selectedRejection.id,
                      notes: adminNotes
                    });
                  } else {
                    approveRejectionMutation.mutate({
                      requestId: selectedRejection.id,
                      notes: adminNotes
                    });
                  }
                }}
                disabled={approveRejectionMutation.isLoading || denyRejectionMutation.isLoading}
                className="flex-1 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50"
              >
                {approveRejectionMutation.isLoading || denyRejectionMutation.isLoading ? 'Processing...' : 'Confirm Decision'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ManageBookings;

