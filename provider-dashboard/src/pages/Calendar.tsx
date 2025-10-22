import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { 
  Calendar as CalendarIcon,
  Clock,
  User,
  MapPin,
  Phone,
  CheckCircle,
  AlertCircle,
  XCircle,
  Eye
} from 'lucide-react';
import Calendar from '../components/calendar/Calendar';
import { useAuth } from '../hooks/useAuth';
import { Booking } from '../services/api';
import { getBookingPrivacyStatus, formatPhoneNumber, formatAddress } from '../utils/privacyUtils';

const CalendarPage: React.FC = () => {
  const { isAuthenticated, isLoading, user } = useAuth();
  const [selectedBooking, setSelectedBooking] = useState<Booking | null>(null);
  const [showBookingDetails, setShowBookingDetails] = useState(false);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-2 text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="text-red-500 mb-2">⚠️</div>
          <p className="text-red-600 mb-2">Authentication Required</p>
          <p className="text-sm text-gray-500 mb-4">Please log in to view your schedule</p>
          <button
            onClick={() => window.location.href = '/login'}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Go to Login
          </button>
        </div>
      </div>
    );
  }

  const handleBookingSelect = (booking: Booking) => {
    setSelectedBooking(booking);
    setShowBookingDetails(true);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800 border-yellow-300';
      case 'CONFIRMED': return 'bg-blue-100 text-blue-800 border-blue-300';
      case 'IN_PROGRESS': return 'bg-purple-100 text-purple-800 border-purple-300';
      case 'COMPLETED': return 'bg-green-100 text-green-800 border-green-300';
      case 'CANCELLED': return 'bg-red-100 text-red-800 border-red-300';
      default: return 'bg-gray-100 text-gray-800 border-gray-300';
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

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Schedule</h1>
          <p className="text-gray-600">View and manage your upcoming bookings</p>
        </div>
        <div className="flex items-center space-x-2 text-sm text-gray-500">
          <div className="flex items-center space-x-1">
            <div className="w-3 h-3 bg-red-500 rounded-full"></div>
            <span>Busy</span>
          </div>
          <div className="flex items-center space-x-1">
            <div className="w-3 h-3 bg-green-500 rounded-full"></div>
            <span>Available</span>
          </div>
        </div>
      </div>

      {/* Calendar Component */}
      <Calendar onBookingSelect={handleBookingSelect} />

      {/* Booking Details Modal */}
      {showBookingDetails && selectedBooking && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              {/* Header */}
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-lg font-semibold text-gray-900">Booking Details</h3>
                <button
                  onClick={() => setShowBookingDetails(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <XCircle className="w-6 h-6" />
                </button>
              </div>

              {/* Booking Info */}
              <div className="space-y-4">
                {/* Status */}
                <div className={`inline-flex items-center space-x-2 px-3 py-1 rounded-full text-sm font-medium border ${getStatusColor(selectedBooking.status)}`}>
                  {getStatusIcon(selectedBooking.status)}
                  <span>{selectedBooking.status}</span>
                </div>

                {/* Service Details */}
                <div className="bg-gray-50 rounded-lg p-4">
                  <h4 className="font-semibold text-gray-900 mb-2">{selectedBooking.service.name}</h4>
                  <p className="text-sm text-gray-600 mb-3">{selectedBooking.service.description}</p>
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-gray-500">Duration: {selectedBooking.duration}h</span>
                    <span className="font-semibold text-gray-900">₹{selectedBooking.totalAmount}</span>
                  </div>
                </div>

                {/* Schedule */}
                <div className="space-y-3">
                  <div className="flex items-center space-x-3">
                    <CalendarIcon className="w-5 h-5 text-gray-400" />
                    <div>
                      <div className="font-medium text-gray-900">
                        {new Date(selectedBooking.scheduledDate).toLocaleDateString('en-US', {
                          weekday: 'long',
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric'
                        })}
                      </div>
                    </div>
                  </div>
                  
                  <div className="flex items-center space-x-3">
                    <Clock className="w-5 h-5 text-gray-400" />
                    <div>
                      <div className="font-medium text-gray-900">{selectedBooking.scheduledTime}</div>
                      <div className="text-sm text-gray-500">
                        {selectedBooking.duration} hour{selectedBooking.duration > 1 ? 's' : ''} duration
                      </div>
                    </div>
                  </div>
                </div>

                {/* Customer Details */}
                <div className="border-t pt-4">
                  <h4 className="font-semibold text-gray-900 mb-3">Customer Information</h4>
                  
                  {/* Privacy Status for Providers */}
                  {user?.role === 'PROVIDER' && (() => {
                    const privacyStatus = getBookingPrivacyStatus(selectedBooking, user.role);
                    return !privacyStatus.contactDetailsVisible ? (
                      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3 mb-3">
                        <div className="flex items-center space-x-2">
                          <AlertCircle className="w-4 h-4 text-yellow-600" />
                          <span className="text-sm text-yellow-800 font-medium">Contact Details Protected</span>
                        </div>
                        <p className="text-xs text-yellow-700 mt-1">{privacyStatus.privacyMessage}</p>
                      </div>
                    ) : null;
                  })()}
                  
                  <div className="space-y-2">
                    <div className="flex items-center space-x-3">
                      <User className="w-4 h-4 text-gray-400" />
                      <span className="text-gray-900">{selectedBooking.user.name}</span>
                    </div>
                    
                    {/* Phone with privacy protection */}
                    <div className="flex items-center space-x-3">
                      <Phone className="w-4 h-4 text-gray-400" />
                      <span className={`${user?.role === 'PROVIDER' && !getBookingPrivacyStatus(selectedBooking, user.role).contactDetailsVisible ? 'text-gray-500 italic' : 'text-gray-900'}`}>
                        {user?.role === 'PROVIDER' 
                          ? formatPhoneNumber(selectedBooking.user.phone, getBookingPrivacyStatus(selectedBooking, user.role).contactDetailsVisible)
                          : selectedBooking.user.phone
                        }
                      </span>
                    </div>
                    
                    {/* Address with privacy protection */}
                    {selectedBooking.user.address && (
                      <div className="flex items-center space-x-3">
                        <MapPin className="w-4 h-4 text-gray-400" />
                        <span className={`${user?.role === 'PROVIDER' && !getBookingPrivacyStatus(selectedBooking, user.role).contactDetailsVisible ? 'text-gray-500 italic' : 'text-gray-900'}`}>
                          {user?.role === 'PROVIDER' 
                            ? formatAddress(selectedBooking.user.address, getBookingPrivacyStatus(selectedBooking, user.role).contactDetailsVisible)
                            : selectedBooking.user.address
                          }
                        </span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Special Instructions */}
                {selectedBooking.specialInstructions && (
                  <div className="border-t pt-4">
                    <h4 className="font-semibold text-gray-900 mb-2">Special Instructions</h4>
                    <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">
                      {selectedBooking.specialInstructions}
                    </p>
                  </div>
                )}

                {/* Notes */}
                {selectedBooking.notes && (
                  <div className="border-t pt-4">
                    <h4 className="font-semibold text-gray-900 mb-2">Notes</h4>
                    <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">
                      {selectedBooking.notes}
                    </p>
                  </div>
                )}

                {/* Payment Status */}
                <div className="border-t pt-4">
                  <div className="flex items-center justify-between">
                    <span className="text-sm text-gray-500">Payment Status:</span>
                    <span className={`text-sm font-medium ${
                      selectedBooking.paymentStatus === 'PAID' ? 'text-green-600' : 
                      selectedBooking.paymentStatus === 'PENDING' ? 'text-yellow-600' : 
                      'text-red-600'
                    }`}>
                      {selectedBooking.paymentStatus}
                    </span>
                  </div>
                </div>
              </div>

              {/* Actions */}
              <div className="flex justify-end space-x-3 mt-6 pt-4 border-t">
                <button
                  onClick={() => setShowBookingDetails(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  Close
                </button>
                <button
                  onClick={() => {
                    // Navigate to bookings page with this booking selected
                    window.location.href = '/bookings';
                  }}
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700"
                >
                  Manage Booking
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CalendarPage;
