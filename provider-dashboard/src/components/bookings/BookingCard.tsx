import React from 'react';
import { Calendar, Clock, User } from 'lucide-react';

interface Booking {
  id: string;
  user: {
    name: string;
  };
  service: {
    name: string;
  };
  scheduledDate: string;
  scheduledTime: string;
  status: string;
  totalAmount: number;
}

interface BookingCardProps {
  booking: Booking;
  compact?: boolean;
}

const BookingCard: React.FC<BookingCardProps> = ({ booking, compact = false }) => {
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

  return (
    <div className={`bg-white border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow ${compact ? 'text-sm' : ''}`}>
      <div className="flex items-start justify-between mb-2">
        <div className="flex items-center">
          <User className="w-4 h-4 text-gray-500 mr-2" />
          <span className="font-medium text-gray-900">{booking.user.name}</span>
        </div>
        <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(booking.status)}`}>
          {booking.status}
        </span>
      </div>
      
      <div className="text-gray-600 mb-2">
        {booking.service.name}
      </div>
      
      <div className="flex items-center justify-between text-sm text-gray-500">
        <div className="flex items-center">
          <Calendar className="w-3 h-3 mr-1" />
          {new Date(booking.scheduledDate).toLocaleDateString()}
        </div>
        <div className="flex items-center">
          <Clock className="w-3 h-3 mr-1" />
          {booking.scheduledTime}
        </div>
        <div className="font-medium text-gray-900">
          ₹{booking.totalAmount}
        </div>
      </div>
    </div>
  );
};

export default BookingCard;

