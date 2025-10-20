import { useState } from 'react';
import { useAuth } from '../../lib/auth/AuthContext';
import { useUserBookings, useCancelBooking } from '../../lib/hooks/useBookings';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { formatCurrency } from '../../lib/utils/formatDate';
import { Dialog } from '@headlessui/react';
import { 
  CalendarIcon, 
  ClockIcon,
  XMarkIcon 
} from '@heroicons/react/24/outline';

export const BookingsPage = () => {
  const { user } = useAuth();
  const { data: bookings, isLoading } = useUserBookings(user?.id);
  const cancelBooking = useCancelBooking();
  const [cancelingId, setCancelingId] = useState<string | null>(null);
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [selectedBooking, setSelectedBooking] = useState<string | null>(null);

  const handleCancelClick = (bookingId: string) => {
    setSelectedBooking(bookingId);
    setIsConfirmOpen(true);
  };

  const handleConfirmCancel = async () => {
    if (!selectedBooking) return;
    
    setCancelingId(selectedBooking);
    try {
      await cancelBooking.mutateAsync(selectedBooking);
      setIsConfirmOpen(false);
      setSelectedBooking(null);
    } catch (error) {
      console.error('Failed to cancel booking:', error);
    } finally {
      setCancelingId(null);
    }
  };

  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      CONFIRMED: 'bg-blue-100 text-blue-800',
      IN_PROGRESS: 'bg-purple-100 text-purple-800',
      COMPLETED: 'bg-green-100 text-green-800',
      CANCELLED: 'bg-red-100 text-red-800',
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  const getPaymentStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      PENDING: 'text-yellow-600',
      PAID: 'text-green-600',
      FAILED: 'text-red-600',
      REFUNDED: 'text-gray-600',
    };
    return colors[status] || 'text-gray-600';
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">My Bookings</h1>
          <p className="text-gray-600">View and manage your healthcare service bookings</p>
        </div>

        {bookings && bookings.length > 0 ? (
          <div className="space-y-6">
            {bookings.map((booking) => (
              <Card key={booking.id}>
                <div className="flex flex-col md:flex-row md:items-center md:justify-between">
                  <div className="flex-grow">
                    {/* Header */}
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-xl font-semibold text-gray-900">
                        {booking.service.name}
                      </h3>
                      <div className="flex gap-2">
                        <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(booking.status)}`}>
                          {booking.status}
                        </span>
                      </div>
                    </div>

                    {/* Details Grid */}
                    <div className="grid md:grid-cols-2 gap-4 mb-4">
                      <div className="flex items-start">
                        <CalendarIcon className="h-5 w-5 text-gray-400 mr-2 mt-0.5" />
                        <div>
                          <p className="text-sm text-gray-500">Scheduled Date & Time</p>
                          <p className="text-gray-900 font-medium">
                            {new Date(booking.scheduledDate + 'T' + booking.scheduledTime).toLocaleString('en-IN', {
                              dateStyle: 'medium',
                              timeStyle: 'short'
                            })}
                          </p>
                        </div>
                      </div>

                      <div className="flex items-start">
                        <ClockIcon className="h-5 w-5 text-gray-400 mr-2 mt-0.5" />
                        <div>
                          <p className="text-sm text-gray-500">Duration</p>
                          <p className="text-gray-900 font-medium">{booking.duration} hour{booking.duration > 1 ? 's' : ''}</p>
                        </div>
                      </div>

                      {booking.specialInstructions && (
                        <div className="md:col-span-2">
                          <p className="text-sm text-gray-500">Service Details</p>
                          <p className="text-gray-900 whitespace-pre-wrap">{booking.specialInstructions}</p>
                        </div>
                      )}
                    </div>

                    {/* Payment Info */}
                    <div className="flex items-center justify-between pt-4 border-t border-gray-200">
                      <div>
                        <p className="text-sm text-gray-500">Total Amount</p>
                        <p className="text-2xl font-bold text-primary-600">
                          {formatCurrency(booking.totalAmount)}
                        </p>
                      </div>
                      <div>
                        <p className="text-sm text-gray-500">Payment Status</p>
                        <p className={`font-semibold ${getPaymentStatusColor(booking.paymentStatus)}`}>
                          {booking.paymentStatus}
                        </p>
                      </div>
                    </div>
                  </div>

                  {/* Actions */}
                  {booking.status === 'PENDING' || booking.status === 'CONFIRMED' ? (
                    <div className="mt-4 md:mt-0 md:ml-6">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleCancelClick(booking.id)}
                        disabled={cancelingId === booking.id}
                        className="border-red-300 text-red-600 hover:bg-red-50"
                      >
                        {cancelingId === booking.id ? 'Canceling...' : 'Cancel Booking'}
                      </Button>
                    </div>
                  ) : null}
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <Card className="text-center py-12">
            <p className="text-gray-500 text-lg mb-4">No bookings yet</p>
            <Button onClick={() => window.location.href = '/services'}>
              Browse Services
            </Button>
          </Card>
        )}

        {/* Cancel Confirmation Dialog */}
        <Dialog
          open={isConfirmOpen}
          onClose={() => setIsConfirmOpen(false)}
          className="relative z-50"
        >
          <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
          <div className="fixed inset-0 flex items-center justify-center p-4">
            <Dialog.Panel className="mx-auto max-w-md w-full bg-white rounded-lg shadow-xl p-6">
              <div className="flex justify-between items-center mb-4">
                <Dialog.Title className="text-xl font-bold text-gray-900">
                  Cancel Booking
                </Dialog.Title>
                <button 
                  onClick={() => setIsConfirmOpen(false)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  <XMarkIcon className="w-6 h-6" />
                </button>
              </div>

              <p className="text-gray-600 mb-6">
                Are you sure you want to cancel this booking? This action cannot be undone.
              </p>

              <div className="flex gap-3">
                <Button
                  variant="outline"
                  onClick={() => setIsConfirmOpen(false)}
                  className="flex-1"
                >
                  Keep Booking
                </Button>
                <Button
                  onClick={handleConfirmCancel}
                  isLoading={!!cancelingId}
                  className="flex-1 bg-red-600 hover:bg-red-700"
                >
                  Yes, Cancel
                </Button>
              </div>
            </Dialog.Panel>
          </div>
        </Dialog>
      </div>
    </div>
  );
};
