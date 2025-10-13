import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  Calendar, 
  Clock, 
  User, 
  Phone, 
  MapPin, 
  Search,
  Filter,
  MoreVertical,
  CheckCircle,
  XCircle,
  AlertCircle,
  Play,
  Square
} from 'lucide-react';

import { getMyBookings, updateBookingStatus, acceptBooking, rejectBooking, startService, completeService } from '../services/api';
import { Booking } from '../services/api';

const Bookings: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedBooking, setSelectedBooking] = useState<Booking | null>(null);
  const [showActionModal, setShowActionModal] = useState(false);
  const [actionType, setActionType] = useState<'accept' | 'reject' | 'start' | 'complete'>('accept');
  
  const queryClient = useQueryClient();

  // Fetch provider's bookings
  const { data: bookingsData, isLoading } = useQuery(
    ['my-bookings', statusFilter],
    () => getMyBookings(0, 50, statusFilter === 'ALL' ? undefined : statusFilter as any),
    {
      refetchInterval: 30000, // Refetch every 30 seconds
    }
  );

  // Update booking status mutation
  const updateStatusMutation = useMutation(
    ({ id, status }: { id: string; status: string }) => 
      updateBookingStatus(id, status as any),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['my-bookings']);
      },
    }
  );

  // Accept booking mutation
  const acceptBookingMutation = useMutation(
    (bookingId: string) => acceptBooking(bookingId),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
    }
  );

  // Reject booking mutation
  const rejectBookingMutation = useMutation(
    ({ bookingId, reason }: { bookingId: string; reason?: string }) =>
      rejectBooking(bookingId, reason),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
    }
  );

  // Start service mutation
  const startServiceMutation = useMutation(
    (bookingId: string) => startService(bookingId),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
    }
  );

  // Complete service mutation
  const completeServiceMutation = useMutation(
    ({ bookingId, notes }: { bookingId: string; notes?: string }) =>
      completeService(bookingId, notes),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
    }
  );

  const bookings = bookingsData?.content || [];
  const filteredBookings = bookings.filter(booking =>
    booking.user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    booking.service.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    booking.id.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'CONFIRMED': return 'bg-blue-100 text-blue-800';
      case 'IN_PROGRESS': return 'bg-purple-100 text-purple-800';
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING': return <AlertCircle className="w-4 h-4" />;
      case 'CONFIRMED': return <CheckCircle className="w-4 h-4" />;
      case 'IN_PROGRESS': return <Clock className="w-4 h-4" />;
      case 'COMPLETED': return <CheckCircle className="w-4 h-4" />;
      case 'CANCELLED': return <XCircle className="w-4 h-4" />;
      default: return <AlertCircle className="w-4 h-4" />;
    }
  };

  const handleBookingAction = (bookingId: string, action: 'accept' | 'reject' | 'start' | 'complete') => {
    setSelectedBooking(bookings.find(b => b.id === bookingId) || null);
    setActionType(action);
    setShowActionModal(true);
  };

  const handleActionSubmit = () => {
    if (!selectedBooking) return;

    switch (actionType) {
      case 'accept':
        acceptBookingMutation.mutate(selectedBooking.id);
        break;
      case 'reject':
        rejectBookingMutation.mutate({ bookingId: selectedBooking.id });
        break;
      case 'start':
        startServiceMutation.mutate(selectedBooking.id);
        break;
      case 'complete':
        completeServiceMutation.mutate({ bookingId: selectedBooking.id });
        break;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Bookings</h1>
          <p className="text-gray-600">Manage your assigned healthcare service bookings</p>
        </div>
      </div>

      {/* Filters and Search */}
      <div className="bg-white p-6 rounded-lg shadow-sm border">
        <div className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              <input
                type="text"
                placeholder="Search bookings by customer, service, or ID..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>
          <div className="flex gap-2">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            >
              <option value="ALL">All Status</option>
              <option value="PENDING">Pending</option>
              <option value="CONFIRMED">Confirmed</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="COMPLETED">Completed</option>
              <option value="CANCELLED">Cancelled</option>
            </select>
            <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
              <Filter className="w-4 h-4" />
              Filters
            </button>
          </div>
        </div>
      </div>

      {/* Bookings List */}
      <div className="bg-white rounded-lg shadow-sm border">
        {isLoading ? (
          <div className="p-8 text-center">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-2 text-gray-600">Loading bookings...</p>
          </div>
        ) : filteredBookings.length === 0 ? (
          <div className="p-8 text-center">
            <Calendar className="w-12 h-12 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">No bookings found</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Booking
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Customer
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Service
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Schedule
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Amount
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredBookings.map((booking) => (
                  <tr key={booking.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          #{booking.id.slice(0, 8)}
                        </div>
                        <div className="text-sm text-gray-500">
                          {new Date(booking.createdAt).toLocaleDateString()}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="flex-shrink-0 h-10 w-10">
                          <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                            <User className="h-5 w-5 text-blue-600" />
                          </div>
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {booking.user.name}
                          </div>
                          <div className="text-sm text-gray-500">
                            {booking.user.phone}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        {booking.service.name}
                      </div>
                      <div className="text-sm text-gray-500">
                        {booking.service.category.name}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">
                        {new Date(booking.scheduledDate).toLocaleDateString()}
                      </div>
                      <div className="text-sm text-gray-500">
                        {booking.scheduledTime}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(booking.status)}`}>
                        {getStatusIcon(booking.status)}
                        <span className="ml-1">{booking.status}</span>
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      ₹{booking.totalAmount}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <div className="flex items-center space-x-2">
                        {booking.status === 'PENDING' && (
                          <>
                            <button
                              onClick={() => handleBookingAction(booking.id, 'accept')}
                              className="text-green-600 hover:text-green-900 flex items-center gap-1"
                            >
                              <CheckCircle className="w-4 h-4" />
                              Accept
                            </button>
                            <button
                              onClick={() => handleBookingAction(booking.id, 'reject')}
                              className="text-red-600 hover:text-red-900 flex items-center gap-1"
                            >
                              <XCircle className="w-4 h-4" />
                              Reject
                            </button>
                          </>
                        )}
                        {booking.status === 'CONFIRMED' && (
                          <button
                            onClick={() => handleBookingAction(booking.id, 'start')}
                            className="text-purple-600 hover:text-purple-900 flex items-center gap-1"
                          >
                            <Play className="w-4 h-4" />
                            Start
                          </button>
                        )}
                        {booking.status === 'IN_PROGRESS' && (
                          <button
                            onClick={() => handleBookingAction(booking.id, 'complete')}
                            className="text-green-600 hover:text-green-900 flex items-center gap-1"
                          >
                            <Square className="w-4 h-4" />
                            Complete
                          </button>
                        )}
                        <button className="text-gray-600 hover:text-gray-900">
                          <MoreVertical className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Action Modal */}
      {showActionModal && selectedBooking && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              {actionType === 'accept' && 'Accept Booking'}
              {actionType === 'reject' && 'Reject Booking'}
              {actionType === 'start' && 'Start Service'}
              {actionType === 'complete' && 'Complete Service'}
            </h3>
            
            <div className="mb-4">
              <p className="text-sm text-gray-600 mb-2">
                <strong>Service:</strong> {selectedBooking.service.name}
              </p>
              <p className="text-sm text-gray-600 mb-2">
                <strong>Customer:</strong> {selectedBooking.user.name}
              </p>
              <p className="text-sm text-gray-600 mb-2">
                <strong>Schedule:</strong> {new Date(selectedBooking.scheduledDate).toLocaleDateString()} at {selectedBooking.scheduledTime}
              </p>
            </div>

            {actionType === 'reject' && (
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Reason for Rejection (Optional)
                </label>
                <textarea
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  rows={3}
                  placeholder="Enter reason for rejection..."
                />
              </div>
            )}

            {actionType === 'complete' && (
              <div className="mb-4">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Service Notes (Optional)
                </label>
                <textarea
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  rows={3}
                  placeholder="Enter any notes about the service provided..."
                />
              </div>
            )}

            <div className="flex justify-end space-x-3">
              <button
                onClick={() => setShowActionModal(false)}
                className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
              >
                Cancel
              </button>
              <button
                onClick={handleActionSubmit}
                className={`px-4 py-2 text-sm font-medium text-white rounded-md ${
                  actionType === 'accept' || actionType === 'start' || actionType === 'complete'
                    ? 'bg-green-600 hover:bg-green-700'
                    : 'bg-red-600 hover:bg-red-700'
                }`}
              >
                {actionType === 'accept' && 'Accept Booking'}
                {actionType === 'reject' && 'Reject Booking'}
                {actionType === 'start' && 'Start Service'}
                {actionType === 'complete' && 'Complete Service'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Bookings;