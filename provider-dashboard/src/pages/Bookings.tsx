import React, { useState, useEffect } from 'react';
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
  Square,
  Eye,
  DollarSign,
  FileText
} from 'lucide-react';

import { getMyBookings, updateBookingStatus, acceptBooking, rejectBooking, startService, completeService } from '../services/api';
import { Booking } from '../services/api';

const Bookings: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedBooking, setSelectedBooking] = useState<Booking | null>(null);
  const [showActionModal, setShowActionModal] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [actionType, setActionType] = useState<'accept' | 'reject' | 'start' | 'complete'>('accept');
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const [menuPosition, setMenuPosition] = useState<{ top?: number; bottom?: number; right: number }>({ right: 0 });
  const [rejectionReason, setRejectionReason] = useState('');
  const [completionNotes, setCompletionNotes] = useState('');
  
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
        console.log('Accept booking success');
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
      onError: (error) => {
        console.error('Accept booking error:', error);
      },
    }
  );

  // Reject booking mutation
  const rejectBookingMutation = useMutation(
    ({ bookingId, reason }: { bookingId: string; reason?: string }) =>
      rejectBooking(bookingId, reason),
    {
      onSuccess: () => {
        console.log('Reject booking success');
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
      onError: (error) => {
        console.error('Reject booking error:', error);
      },
    }
  );

  // Start service mutation
  const startServiceMutation = useMutation(
    (bookingId: string) => startService(bookingId),
    {
      onSuccess: () => {
        console.log('Start service success');
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
      onError: (error) => {
        console.error('Start service error:', error);
      },
    }
  );

  // Complete service mutation
  const completeServiceMutation = useMutation(
    ({ bookingId, notes }: { bookingId: string; notes?: string }) =>
      completeService(bookingId, notes),
    {
      onSuccess: () => {
        console.log('Complete service success');
        queryClient.invalidateQueries(['my-bookings']);
        setShowActionModal(false);
        setSelectedBooking(null);
      },
      onError: (error) => {
        console.error('Complete service error:', error);
      },
    }
  );

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = () => {
      if (openMenuId) {
        setOpenMenuId(null);
      }
    };

    if (openMenuId) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [openMenuId]);

  // Debug modal state
  useEffect(() => {
    console.log('Modal state changed:', { showActionModal, selectedBooking: selectedBooking?.id, actionType });
  }, [showActionModal, selectedBooking, actionType]);

  const handleMenuClick = (bookingId: string, event: React.MouseEvent) => {
    event.stopPropagation();
    if (openMenuId === bookingId) {
      setOpenMenuId(null);
      return;
    }

    const buttonRect = (event.currentTarget as HTMLElement).getBoundingClientRect();
    const viewportHeight = window.innerHeight;
    const spaceBelow = viewportHeight - buttonRect.bottom;
    const spaceAbove = buttonRect.top;
    const menuHeight = 200;

    let position;
    if (spaceBelow >= menuHeight || spaceBelow > spaceAbove) {
      position = {
        top: buttonRect.bottom + 5,
        right: window.innerWidth - buttonRect.right
      };
    } else if (spaceAbove >= menuHeight) {
      position = {
        bottom: viewportHeight - buttonRect.top + 5,
        right: window.innerWidth - buttonRect.right
      };
    } else {
      const idealTop = Math.max(10, Math.min(buttonRect.bottom, viewportHeight - menuHeight - 10));
      position = {
        top: idealTop,
        right: window.innerWidth - buttonRect.right
      };
    }

    setMenuPosition(position);
    setOpenMenuId(bookingId);
  };

  // Filter to show only active bookings (PENDING, CONFIRMED, IN_PROGRESS)
  const activeBookings = (bookingsData?.content || []).filter((booking: Booking) => 
    booking.status === 'PENDING' || booking.status === 'CONFIRMED' || booking.status === 'IN_PROGRESS'
  );
  
  const filteredBookings = activeBookings.filter(booking => {
    // Apply search filter
    const matchesSearch = 
      booking.user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.service.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.id.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Apply status filter
    const matchesStatus = statusFilter === 'ALL' || booking.status === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

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
    console.log('handleBookingAction called:', { bookingId, action });
    const booking = (bookingsData?.content || []).find(b => b.id === bookingId);
    console.log('Found booking:', booking);
    setSelectedBooking(booking || null);
    setActionType(action);
    setRejectionReason('');
    setCompletionNotes('');
    setShowActionModal(true);
    console.log('Modal should be shown now');
  };

  const handleActionSubmit = () => {
    console.log('handleActionSubmit called:', { selectedBooking, actionType, rejectionReason, completionNotes });
    if (!selectedBooking) {
      console.log('No selected booking, returning');
      return;
    }

    console.log('Calling mutation for action:', actionType);
    switch (actionType) {
      case 'accept':
        console.log('Calling acceptBookingMutation');
        acceptBookingMutation.mutate(selectedBooking.id);
        break;
      case 'reject':
        console.log('Calling rejectBookingMutation');
        rejectBookingMutation.mutate({ bookingId: selectedBooking.id, reason: rejectionReason });
        break;
      case 'start':
        console.log('Calling startServiceMutation');
        startServiceMutation.mutate(selectedBooking.id);
        break;
      case 'complete':
        console.log('Calling completeServiceMutation');
        completeServiceMutation.mutate({ bookingId: selectedBooking.id, notes: completionNotes });
        break;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Manage Bookings</h1>
          <p className="text-gray-600">Manage your active bookings (PENDING, CONFIRMED, IN_PROGRESS)</p>
          <p className="text-xs text-gray-500 mt-1">
            💡 Full customer contact details available only for CONFIRMED/IN_PROGRESS bookings
          </p>
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
              <option value="ALL">All Active</option>
              <option value="PENDING">Pending (Need Action)</option>
              <option value="CONFIRMED">Confirmed (Accepted)</option>
              <option value="IN_PROGRESS">In Progress</option>
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
                            {booking.user?.name || 'N/A'}
                          </div>
                          <div className="text-sm text-gray-500">
                            {booking.user?.phone || 'N/A'}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">
                        {booking.service?.name || 'N/A'}
                      </div>
                      <div className="text-sm text-gray-500">
                        {booking.service?.category?.name || 'No Category'}
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
                        <button 
                          onClick={(e) => handleMenuClick(booking.id, e)}
                          className="text-gray-600 hover:text-gray-900 relative"
                        >
                          <MoreVertical className="w-4 h-4" />
                        </button>
                        {openMenuId === booking.id && (
                          <div
                            className="fixed w-56 bg-white rounded-lg shadow-xl border border-gray-200 py-1 z-[9999]"
                            style={{
                              top: menuPosition.top ? `${menuPosition.top}px` : undefined,
                              bottom: menuPosition.bottom ? `${menuPosition.bottom}px` : undefined,
                              right: `${menuPosition.right}px`
                            }}
                          >
                            {booking.status === 'PENDING' && (
                              <>
                                <button
                                  onClick={() => {
                                    setOpenMenuId(null);
                                    handleBookingAction(booking.id, 'accept');
                                  }}
                                  className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <CheckCircle className="w-4 h-4 text-green-600" />
                                  Accept Booking
                                </button>
                                <button
                                  onClick={() => {
                                    setOpenMenuId(null);
                                    handleBookingAction(booking.id, 'reject');
                                  }}
                                  className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <XCircle className="w-4 h-4 text-red-600" />
                                  Reject Booking
                                </button>
                              </>
                            )}
                            {booking.status === 'CONFIRMED' && (
                              <button
                                onClick={() => {
                                  setOpenMenuId(null);
                                  handleBookingAction(booking.id, 'start');
                                }}
                                className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                              >
                                <Play className="w-4 h-4 text-blue-600" />
                                Start Service
                              </button>
                            )}
                            {booking.status === 'IN_PROGRESS' && (
                              <button
                                onClick={() => {
                                  setOpenMenuId(null);
                                  handleBookingAction(booking.id, 'complete');
                                }}
                                className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                              >
                                <Square className="w-4 h-4 text-green-600" />
                                Complete Service
                              </button>
                            )}
                            {/* View Customer Details - Only for active bookings (CONFIRMED, IN_PROGRESS) */}
                            {(booking.status === 'CONFIRMED' || booking.status === 'IN_PROGRESS') && (
                              <>
                                <hr className="my-1" />
                                <button
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    setSelectedBooking(booking);
                                    setShowDetailsModal(true);
                                    setOpenMenuId(null);
                                  }}
                                  className="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <Eye className="w-4 h-4 text-blue-600" />
                                  View Customer Details
                                </button>
                              </>
                            )}
                          </div>
                        )}
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
                  value={rejectionReason}
                  onChange={(e) => setRejectionReason(e.target.value)}
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
                  value={completionNotes}
                  onChange={(e) => setCompletionNotes(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  rows={3}
                  placeholder="Enter any notes about the service provided..."
                />
              </div>
            )}

            <div className="flex justify-end space-x-3">
              <button
                onClick={() => {
                  setShowActionModal(false);
                  setRejectionReason('');
                  setCompletionNotes('');
                }}
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

      {/* Complete Booking Details Modal - Only for Active Bookings */}
      {showDetailsModal && selectedBooking && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h3 className="text-xl font-bold text-gray-900">Complete Booking Details</h3>
                <p className="text-sm text-gray-500 mt-1">
                  {selectedBooking.status === 'PENDING' && '📋 Review all details before making a decision'}
                  {selectedBooking.status === 'CONFIRMED' && '✅ Customer details for upcoming service'}
                  {selectedBooking.status === 'IN_PROGRESS' && '🔄 Current service in progress'}
                </p>
              </div>
              <span className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-medium ${
                selectedBooking.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                selectedBooking.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-800' :
                selectedBooking.status === 'IN_PROGRESS' ? 'bg-purple-100 text-purple-800' :
                selectedBooking.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                'bg-red-100 text-red-800'
              }`}>
                {selectedBooking.status}
              </span>
            </div>

            <div className="space-y-6">
              {/* Booking Information */}
              <div className="bg-gray-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <Calendar className="w-5 h-5 mr-2" />
                  Booking Information
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Booking ID</label>
                    <p className="text-sm text-gray-900 font-mono bg-white px-3 py-2 rounded border">#{selectedBooking.id.slice(0, 8)}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Created On</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">
                      {new Date(selectedBooking.createdAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                    </p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Scheduled Date & Time</label>
                    <p className="text-sm text-gray-900 font-semibold bg-white px-3 py-2 rounded border">
                      {new Date(selectedBooking.scheduledDate).toLocaleDateString('en-IN')} at {selectedBooking.scheduledTime}
                    </p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Duration</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">
                      {selectedBooking.duration} hour{selectedBooking.duration > 1 ? 's' : ''}
                    </p>
                  </div>
                </div>
              </div>

              {/* Customer Information */}
              <div className="bg-blue-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <User className="w-5 h-5 mr-2" />
                  Customer Details
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Name</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.user.name}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Email</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.user.email}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Phone</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border flex items-center">
                      <Phone className="w-4 h-4 mr-2 text-gray-400" />
                      {selectedBooking.user.phone}
                    </p>
                  </div>
                  {selectedBooking.user.address && (
                    <div className="md:col-span-2">
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Address</label>
                      <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border flex items-start">
                        <MapPin className="w-4 h-4 mr-2 mt-0.5 text-gray-400" />
                        {selectedBooking.user.address}
                      </p>
                    </div>
                  )}
                </div>
              </div>

              {/* Patient Information */}
              {(selectedBooking.patient || selectedBooking.specialInstructions?.includes('Patient ID:')) && (
                <div className="bg-pink-50 p-4 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                    <User className="w-5 h-5 mr-2" />
                    Patient Details
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {selectedBooking.patient ? (
                      <>
                        <div>
                          <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Patient Name</label>
                          <p className="text-sm text-gray-900 font-semibold bg-white px-3 py-2 rounded border">{selectedBooking.patient.name}</p>
                        </div>
                    <div>
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Age / Gender</label>
                      <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.patient.age} years / {selectedBooking.patient.gender}</p>
                    </div>
                    {selectedBooking.patient.bloodGroup && (
                      <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Blood Group</label>
                        <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.patient.bloodGroup}</p>
                      </div>
                    )}
                    <div>
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Relationship</label>
                      <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.patient.relationshipToCustomer}</p>
                    </div>
                    {selectedBooking.patient.isDiabetic && (
                      <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Medical Condition</label>
                        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                          Diabetic
                        </span>
                      </div>
                    )}
                    {selectedBooking.patient.bpStatus !== 'NORMAL' && (
                      <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">BP Status</label>
                        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-orange-100 text-orange-800">
                          {selectedBooking.patient.bpStatus} BP
                        </span>
                      </div>
                    )}
                    {selectedBooking.patient.allergies && (
                      <div className="md:col-span-2">
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Allergies</label>
                        <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.patient.allergies}</p>
                      </div>
                    )}
                    {selectedBooking.patient.chronicConditions && (
                      <div className="md:col-span-2">
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Chronic Conditions</label>
                        <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.patient.chronicConditions}</p>
                      </div>
                    )}
                    {selectedBooking.patient.emergencyContactName && (
                      <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Emergency Contact</label>
                        <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.patient.emergencyContactName}</p>
                      </div>
                    )}
                    {selectedBooking.patient.emergencyContactRelation && (
                      <div>
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Emergency Relation</label>
                        <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.patient.emergencyContactRelation}</p>
                      </div>
                    )}
                    {/* Note: Emergency phone number is hidden for provider dashboard as per requirement */}
                      </>
                    ) : (
                      <div className="md:col-span-2">
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Patient Information</label>
                        <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">
                          Patient details are not available. Please check the special instructions for patient ID: {selectedBooking.specialInstructions?.match(/Patient ID: ([a-f0-9-]+)/)?.[1] || 'Not found'}
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* Service Information */}
              <div className="bg-green-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <FileText className="w-5 h-5 mr-2" />
                  Service Details
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Service Name</label>
                    <p className="text-sm text-gray-900 font-semibold bg-white px-3 py-2 rounded border">{selectedBooking.service.name}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Category</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.service.category?.name || 'N/A'}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Price</label>
                    <p className="text-sm text-gray-900 font-semibold bg-white px-3 py-2 rounded border">₹{selectedBooking.service.price}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Service Duration</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.service.duration} minutes</p>
                  </div>
                  <div className="md:col-span-2">
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Description</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.service.description}</p>
                  </div>
                </div>
              </div>

              {/* Customer Requirements & Notes */}
              {selectedBooking.specialInstructions && (
                <div className="bg-yellow-50 p-4 rounded-lg border-2 border-yellow-200">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                    <AlertCircle className="w-5 h-5 mr-2 text-yellow-600" />
                    Customer Requirements & Special Instructions
                  </h4>
                  <div className="bg-white px-4 py-3 rounded border">
                    <p className="text-sm text-gray-900 whitespace-pre-wrap leading-relaxed">{selectedBooking.specialInstructions}</p>
                  </div>
                  {selectedBooking.status === 'PENDING' && (
                    <p className="text-xs text-yellow-700 mt-2 italic">⚠️ Please review these requirements carefully before accepting</p>
                  )}
                </div>
              )}

              {/* Additional Notes */}
              {selectedBooking.notes && (
                <div className="bg-orange-50 p-4 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                    <FileText className="w-5 h-5 mr-2" />
                    Additional Notes
                  </h4>
                  <div className="bg-white px-4 py-3 rounded border">
                    <p className="text-sm text-gray-900 whitespace-pre-wrap leading-relaxed">{selectedBooking.notes}</p>
                  </div>
                </div>
              )}

              {/* Payment Information */}
              <div className="bg-purple-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <DollarSign className="w-5 h-5 mr-2" />
                  Payment Information
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Total Amount</label>
                    <p className="text-lg text-gray-900 font-bold bg-white px-3 py-2 rounded border">₹{selectedBooking.totalAmount}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Payment Status</label>
                    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${
                      selectedBooking.paymentStatus === 'PAID' ? 'bg-green-100 text-green-800' :
                      selectedBooking.paymentStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {selectedBooking.paymentStatus}
                    </span>
                  </div>
                </div>
              </div>

              {/* Timeline */}
              <div className="bg-gray-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <Clock className="w-5 h-5 mr-2" />
                  Timeline
                </h4>
                <div className="space-y-2">
                  <div className="flex items-center text-sm">
                    <span className="font-medium text-gray-700 w-32">Created:</span>
                    <span className="text-gray-900">{new Date(selectedBooking.createdAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}</span>
                  </div>
                  <div className="flex items-center text-sm">
                    <span className="font-medium text-gray-700 w-32">Last Updated:</span>
                    <span className="text-gray-900">{new Date(selectedBooking.updatedAt).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}</span>
                  </div>
                  <div className="flex items-center text-sm">
                    <span className="font-medium text-gray-700 w-32">Scheduled For:</span>
                    <span className="text-gray-900 font-semibold">{new Date(selectedBooking.scheduledDate).toLocaleDateString('en-IN', { dateStyle: 'full' })} at {selectedBooking.scheduledTime}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-6 flex justify-end gap-3">
              <button
                onClick={() => setShowDetailsModal(false)}
                className="px-6 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
              >
                Close
              </button>
              {selectedBooking.status === 'PENDING' && (
                <>
                  <button
                    onClick={() => {
                      setShowDetailsModal(false);
                      handleBookingAction(selectedBooking.id, 'accept');
                    }}
                    className="px-6 py-2 text-sm font-medium text-white bg-green-600 rounded-lg hover:bg-green-700 flex items-center gap-2"
                  >
                    <CheckCircle className="w-4 h-4" />
                    Accept Booking
                  </button>
                  <button
                    onClick={() => {
                      setShowDetailsModal(false);
                      handleBookingAction(selectedBooking.id, 'reject');
                    }}
                    className="px-6 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 flex items-center gap-2"
                  >
                    <XCircle className="w-4 h-4" />
                    Reject Booking
                  </button>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Bookings;