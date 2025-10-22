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
  Eye,
  DollarSign,
  Star
} from 'lucide-react';

import { getBookings, updateBookingStatus, assignProvider, getProviders } from '../services/api';
import { Booking, Provider } from '../services/api';
import ProviderAvailabilityCard from '../components/ProviderAvailabilityCard';

const Bookings: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [selectedBooking, setSelectedBooking] = useState<Booking | null>(null);
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const [menuPosition, setMenuPosition] = useState<{ top?: number; bottom?: number; right: number }>({ right: 0 });
  const [selectedProviderId, setSelectedProviderId] = useState<string>('');
  
  const queryClient = useQueryClient();

  // Fetch available providers when assign modal is open (filtered by service)
  const { data: providersData } = useQuery(
    ['available-providers', selectedBooking?.id],
    async () => {
      if (!selectedBooking) return null;
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/bookings/${selectedBooking.id}/available-providers`,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );
      if (!response.ok) throw new Error('Failed to fetch available providers');
      const providers = await response.json();
      // Return in same format as getProviders
      return { content: providers };
    },
    {
      enabled: showAssignModal && !!selectedBooking, // Only fetch when modal is open and booking is selected
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

  const handleMenuClick = (bookingId: string, event: React.MouseEvent) => {
    event.stopPropagation(); // Prevent immediate close from click-outside
    if (openMenuId === bookingId) {
      setOpenMenuId(null);
      return;
    }
    
    // Get button position
    const buttonRect = (event.currentTarget as HTMLElement).getBoundingClientRect();
    const viewportHeight = window.innerHeight;
    const spaceBelow = viewportHeight - buttonRect.bottom;
    const spaceAbove = buttonRect.top;
    
    const menuHeight = 300; // Estimated menu height
    
    // Smart positioning: choose direction with more space
    // Also ensure menu doesn't go off-screen at top
    let position;
    if (spaceBelow >= menuHeight || spaceBelow > spaceAbove) {
      // Open downward - has enough space or more space than above
      position = {
        top: buttonRect.bottom + 5,
        right: window.innerWidth - buttonRect.right
      };
    } else if (spaceAbove >= menuHeight) {
      // Open upward - has enough space above
      position = {
        bottom: viewportHeight - buttonRect.top + 5,
        right: window.innerWidth - buttonRect.right
      };
    } else {
      // Not enough space either way - position to fit best
      // Calculate top position that keeps menu in viewport
      const idealTop = Math.max(10, Math.min(buttonRect.bottom, viewportHeight - menuHeight - 10));
      position = {
        top: idealTop,
        right: window.innerWidth - buttonRect.right
      };
    }
    
    setMenuPosition(position);
    setOpenMenuId(bookingId);
  };

  // Fetch bookings
  const { data: bookingsData, isLoading } = useQuery(
    ['bookings'],
    () => getBookings(0, 1000), // Get all bookings
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
        queryClient.invalidateQueries(['bookings']);
      },
      onError: (error: any, variables) => {
        const errorMessage = error?.response?.data?.error || error?.message || 'Unknown error';
        
        // Check if error is about missing provider
        if (errorMessage.includes('No provider assigned') || errorMessage.includes('assign a provider')) {
          const booking = bookings.find(b => b.id === variables.id);
          if (booking) {
            // Show assign provider modal
            setSelectedBooking(booking);
            setShowAssignModal(true);
            alert('⚠️ Cannot start service: No provider assigned.\n\nPlease assign a provider first.');
          }
        } else {
          alert(`Error: ${errorMessage}`);
        }
      },
    }
  );

  // Assign provider mutation
  const assignProviderMutation = useMutation(
    ({ bookingId, providerId }: { bookingId: string; providerId: string }) =>
      assignProvider(bookingId, providerId),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(['bookings']);
        setShowAssignModal(false);
        setSelectedBooking(null);
      },
    }
  );

  const bookings = bookingsData?.content || [];
  const filteredBookings = bookings.filter(booking => {
    // Apply search filter
    const matchesSearch = searchTerm === '' || 
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

  const handleStatusUpdate = (bookingId: string, newStatus: string) => {
    updateStatusMutation.mutate({ id: bookingId, status: newStatus });
  };

  const handleAssignProvider = (bookingId: string) => {
    setSelectedBooking(bookings.find(b => b.id === bookingId) || null);
    setShowAssignModal(true);
  };

  const handleViewBookingDetails = (booking: Booking) => {
    setSelectedBooking(booking);
    setShowDetailsModal(true);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Bookings Management</h1>
          <p className="text-gray-600">Manage and monitor all healthcare service bookings</p>
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
          <div className="overflow-x-auto overflow-y-visible">
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
                    Provider
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
                            {booking.user.email}
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
                      {booking.provider ? (
                        <div>
                          <div className="text-sm font-medium text-gray-900">
                            {booking.provider.name}
                          </div>
                          <div className="text-sm text-gray-500">
                            {booking.provider.phone}
                          </div>
                        </div>
                      ) : (
                        <span className="text-sm text-gray-500">Not assigned</span>
                      )}
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
                              onClick={() => handleStatusUpdate(booking.id, 'CONFIRMED')}
                              className="text-green-600 hover:text-green-900"
                            >
                              Confirm
                            </button>
                            <button
                              onClick={() => handleAssignProvider(booking.id)}
                              className="text-blue-600 hover:text-blue-900"
                            >
                              Assign
                            </button>
                          </>
                        )}
                        {booking.status === 'CONFIRMED' && (
                          <button
                            onClick={() => handleStatusUpdate(booking.id, 'IN_PROGRESS')}
                            className="text-purple-600 hover:text-purple-900"
                          >
                            Start
                          </button>
                        )}
                        {booking.status === 'IN_PROGRESS' && (
                          <button
                            onClick={() => handleStatusUpdate(booking.id, 'COMPLETED')}
                            className="text-green-600 hover:text-green-900"
                          >
                            Complete
                          </button>
                        )}
                        <button 
                          onClick={(e) => handleMenuClick(booking.id, e)}
                          className="text-gray-600 hover:text-gray-900"
                        >
                          <MoreVertical className="w-4 h-4" />
                        </button>
                        {openMenuId === booking.id && (
                          <div 
                            className="fixed w-48 bg-white rounded-lg shadow-xl border border-gray-200 py-1 z-[9999]"
                            style={{
                              top: menuPosition.top ? `${menuPosition.top}px` : undefined,
                              bottom: menuPosition.bottom ? `${menuPosition.bottom}px` : undefined,
                              right: `${menuPosition.right}px`
                            }}
                          >
                              <button
                                onClick={() => {
                                  handleViewBookingDetails(booking);
                                  setOpenMenuId(null);
                                }}
                                className="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                              >
                                <Eye className="w-4 h-4" />
                                View Details
                              </button>
                              {booking.status === 'PENDING' && (
                                <>
                                  <button
                                    onClick={() => {
                                      handleStatusUpdate(booking.id, 'CONFIRMED');
                                      setOpenMenuId(null);
                                    }}
                                    className="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                  >
                                    <CheckCircle className="w-4 h-4" />
                                    Confirm Booking
                                  </button>
                                  <button
                                    onClick={() => {
                                      handleAssignProvider(booking.id);
                                      setOpenMenuId(null);
                                    }}
                                    className="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                  >
                                    <User className="w-4 h-4" />
                                    Assign Provider
                                  </button>
                                </>
                              )}
                              {booking.status === 'CONFIRMED' && (
                                <button
                                  onClick={() => {
                                    handleStatusUpdate(booking.id, 'IN_PROGRESS');
                                    setOpenMenuId(null);
                                  }}
                                  className="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <Clock className="w-4 h-4" />
                                  Start Service
                                </button>
                              )}
                              {booking.status === 'IN_PROGRESS' && (
                                <button
                                  onClick={() => {
                                    handleStatusUpdate(booking.id, 'COMPLETED');
                                    setOpenMenuId(null);
                                  }}
                                  className="w-full px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100 flex items-center gap-2"
                                >
                                  <CheckCircle className="w-4 h-4" />
                                  Mark Complete
                                </button>
                              )}
                              <div className="border-t border-gray-200 my-1"></div>
                              <button
                                onClick={() => {
                                  handleStatusUpdate(booking.id, 'CANCELLED');
                                  setOpenMenuId(null);
                                }}
                                className="w-full px-4 py-2 text-left text-sm text-red-600 hover:bg-red-50 flex items-center gap-2"
                              >
                                <XCircle className="w-4 h-4" />
                                Cancel Booking
                              </button>
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

      {/* Booking Details Modal */}
      {showDetailsModal && selectedBooking && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold text-gray-900">
                Complete Booking Details
              </h3>
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
              {/* Basic Information */}
              <div className="bg-gray-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <Calendar className="w-5 h-5 mr-2" />
                  Booking Information
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Booking ID</label>
                    <p className="text-sm text-gray-900 font-mono bg-white px-3 py-2 rounded border">{selectedBooking.id}</p>
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
                      {new Date(selectedBooking.scheduledDate + 'T' + selectedBooking.scheduledTime).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
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
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Customer ID</label>
                    <p className="text-xs text-gray-500 font-mono bg-white px-3 py-2 rounded border">{selectedBooking.user.id}</p>
                  </div>
                </div>
              </div>

              {/* Service Information */}
              <div className="bg-green-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <Clock className="w-5 h-5 mr-2" />
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
                </div>
              </div>

              {/* Patient Information */}
              {selectedBooking.patient && (
                <div className="bg-pink-50 p-4 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                    <User className="w-5 h-5 mr-2" />
                    Patient Details
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
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
                      <div className="md:col-span-2">
                        <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Emergency Contact</label>
                        <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">
                          {selectedBooking.patient.emergencyContactName} - {selectedBooking.patient.emergencyContactPhone}
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* Provider Information */}
              {selectedBooking.provider && (
                <div className="bg-purple-50 p-4 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                    <User className="w-5 h-5 mr-2" />
                    Assigned Provider
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Provider Name</label>
                      <p className="text-sm text-gray-900 font-semibold bg-white px-3 py-2 rounded border">{selectedBooking.provider.name}</p>
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Email</label>
                      <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.provider.email}</p>
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Phone</label>
                      <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border flex items-center">
                        <Phone className="w-4 h-4 mr-2 text-gray-400" />
                        {selectedBooking.provider.phone}
                      </p>
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Experience</label>
                      <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.provider.experience} years</p>
                    </div>
                    <div className="md:col-span-2">
                      <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Qualifications</label>
                      <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.provider.qualifications}</p>
                    </div>
                  </div>
                </div>
              )}

              {/* Customer Notes & Requirements */}
              {selectedBooking.specialInstructions && (
                <div className="bg-yellow-50 p-4 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                    <MapPin className="w-5 h-5 mr-2" />
                    Customer Notes & Requirements
                  </h4>
                  <div className="bg-white px-4 py-3 rounded border">
                    <p className="text-sm text-gray-900 whitespace-pre-wrap leading-relaxed">{selectedBooking.specialInstructions}</p>
                  </div>
                </div>
              )}

              {/* Additional Notes */}
              {selectedBooking.notes && (
                <div className="bg-orange-50 p-4 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                    <AlertCircle className="w-5 h-5 mr-2" />
                    Additional Notes
                  </h4>
                  <div className="bg-white px-4 py-3 rounded border">
                    <p className="text-sm text-gray-900 whitespace-pre-wrap leading-relaxed">{selectedBooking.notes}</p>
                  </div>
                </div>
              )}

              {/* Payment Information */}
              <div className="bg-green-50 p-4 rounded-lg">
                <h4 className="font-semibold text-gray-900 mb-3 flex items-center">
                  <DollarSign className="w-5 h-5 mr-2" />
                  Payment Information
                </h4>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Total Amount</label>
                    <p className="text-lg text-gray-900 font-bold bg-white px-3 py-2 rounded border">₹{selectedBooking.totalAmount}</p>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Payment Status</label>
                    <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${
                      selectedBooking.paymentStatus === 'PAID' ? 'bg-green-100 text-green-800' :
                      selectedBooking.paymentStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                      selectedBooking.paymentStatus === 'FAILED' ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {selectedBooking.paymentStatus}
                    </span>
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-500 uppercase mb-1">Duration Booked</label>
                    <p className="text-sm text-gray-900 bg-white px-3 py-2 rounded border">{selectedBooking.duration} hour{selectedBooking.duration > 1 ? 's' : ''}</p>
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
                    <span className="text-gray-900 font-semibold">{new Date(selectedBooking.scheduledDate + 'T' + selectedBooking.scheduledTime).toLocaleString('en-IN', { dateStyle: 'full', timeStyle: 'short' })}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowDetailsModal(false)}
                className="px-6 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Assign Provider Modal */}
      {showAssignModal && selectedBooking && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Assign Provider to Booking
            </h3>
            
            {/* Booking Summary */}
            <div className="bg-blue-50 p-4 rounded-lg mb-4">
              <div className="grid grid-cols-2 gap-3 text-sm">
                <div>
                  <span className="font-medium text-gray-700">Service:</span>
                  <p className="text-gray-900">{selectedBooking.service.name}</p>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Customer:</span>
                  <p className="text-gray-900">{selectedBooking.user.name}</p>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Date & Time:</span>
                  <p className="text-gray-900">
                    {new Date(selectedBooking.scheduledDate + 'T' + selectedBooking.scheduledTime).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                  </p>
                </div>
                <div>
                  <span className="font-medium text-gray-700">Duration:</span>
                  <p className="text-gray-900">{selectedBooking.duration} hour(s)</p>
                </div>
              </div>
            </div>

            {/* Available Providers List */}
            <div className="space-y-3 mb-6">
              <h4 className="font-medium text-gray-900">Select a Provider</h4>
              
              {!providersData ? (
                <div className="text-center py-4">
                  <div className="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600 mx-auto mb-2"></div>
                  <p className="text-sm text-gray-500">Loading providers...</p>
                </div>
              ) : (() => {
                const allProviders = providersData?.content || [];
                const availableProviders = allProviders.filter(
                  (p: Provider) => p.availabilityStatus === 'AVAILABLE' || p.isAvailable
                );
                
                return allProviders.length === 0 ? (
                  <div className="text-center py-8 bg-yellow-50 rounded-lg border border-yellow-200">
                    <AlertCircle className="w-8 h-8 text-yellow-600 mx-auto mb-2" />
                    <p className="text-sm text-yellow-800 font-medium">No providers found</p>
                  </div>
                ) : (
                  <div className="space-y-3 max-h-96 overflow-y-auto">
                    {allProviders.map((provider: Provider) => (
                      <ProviderAvailabilityCard
                        key={provider.id}
                        provider={provider}
                        isSelected={selectedProviderId === provider.id}
                        onSelect={() => setSelectedProviderId(provider.id)}
                        bookingDate={selectedBooking?.scheduledDate || ''}
                        bookingTime={selectedBooking?.scheduledTime || ''}
                        bookingDuration={selectedBooking?.duration || 1}
                      />
                    ))}
                  </div>
                );
              })()}
            </div>

            <div className="flex justify-end space-x-3">
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
                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {assignProviderMutation.isLoading ? 'Assigning...' : 'Assign Provider'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Bookings;
