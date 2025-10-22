import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { 
  CheckCircle, 
  AlertCircle, 
  Clock, 
  Calendar,
  User,
  Star,
  ChevronDown,
  ChevronUp,
  MapPin
} from 'lucide-react';
import { Provider } from '../services/api';

interface ProviderAvailabilityCardProps {
  provider: Provider;
  isSelected: boolean;
  onSelect: () => void;
  bookingDate: string;
  bookingTime: string;
  bookingDuration: number;
}

const ProviderAvailabilityCard: React.FC<ProviderAvailabilityCardProps> = ({
  provider,
  isSelected,
  onSelect,
  bookingDate,
  bookingTime,
  bookingDuration
}) => {
  const [showDetails, setShowDetails] = useState(false);

  // Fetch provider's bookings for the same date to check availability
  const { data: providerBookings, isLoading } = useQuery(
    ['provider-bookings', provider.id, bookingDate],
    async () => {
      const token = localStorage.getItem('token');
      const response = await fetch(
        `http://localhost:8080/api/providers/${provider.id}/bookings/calendar?startDate=${bookingDate}&endDate=${bookingDate}`,
        { headers: { 'Authorization': `Bearer ${token}` } }
      );
      if (!response.ok) throw new Error('Failed to fetch provider bookings');
      return response.json();
    },
    { enabled: !!provider.id && !!bookingDate }
  );

  // Check if provider is busy at the requested time
  const isProviderBusy = () => {
    if (!providerBookings || !bookingDate || !bookingTime) return false;
    
    const requestedStart = new Date(`2000-01-01T${bookingTime}`);
    const requestedEnd = new Date(requestedStart.getTime() + (bookingDuration * 60 * 60 * 1000));
    
    return providerBookings.some((booking: any) => {
      if (booking.scheduledDate !== bookingDate) return false;
      if (booking.status !== 'CONFIRMED' && booking.status !== 'IN_PROGRESS') return false;
      
      const bookingStart = new Date(`2000-01-01T${booking.scheduledTime}`);
      const bookingEnd = new Date(bookingStart.getTime() + (booking.duration * 60 * 60 * 1000));
      
      // Check for time overlap
      return !(requestedEnd <= bookingStart || requestedStart >= bookingEnd);
    });
  };

  const getAvailabilityStatus = () => {
    if (provider.availabilityStatus === 'OFF_DUTY' || provider.availabilityStatus === 'ON_LEAVE') {
      return {
        status: 'offline',
        color: 'bg-red-100 text-red-800 border-red-300',
        icon: <AlertCircle className="w-3 h-3" />,
        text: 'Offline'
      };
    }
    
    if (isProviderBusy()) {
      return {
        status: 'busy',
        color: 'bg-orange-100 text-orange-800 border-orange-300',
        icon: <Clock className="w-3 h-3" />,
        text: 'Busy at this time'
      };
    }
    
    if (provider.availabilityStatus === 'BUSY') {
      return {
        status: 'busy',
        color: 'bg-orange-100 text-orange-800 border-orange-300',
        icon: <Clock className="w-3 h-3" />,
        text: 'Currently busy'
      };
    }
    
    return {
      status: 'available',
      color: 'bg-green-100 text-green-800 border-green-300',
      icon: <CheckCircle className="w-3 h-3" />,
      text: 'Available'
    };
  };

  const availability = getAvailabilityStatus();
  const isAvailable = availability.status === 'available';

  return (
    <div className={`border-2 rounded-lg transition-all ${
      isSelected
        ? 'border-blue-500 bg-blue-50'
        : isAvailable
        ? 'border-gray-200 hover:border-blue-300 hover:bg-gray-50'
        : 'border-gray-200 bg-gray-50 opacity-75'
    }`}>
      <button
        onClick={isAvailable ? onSelect : undefined}
        disabled={!isAvailable}
        className={`w-full p-4 text-left ${!isAvailable ? 'cursor-not-allowed' : 'cursor-pointer'}`}
      >
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <h5 className="font-semibold text-gray-900">{provider.name}</h5>
              <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium border ${availability.color}`}>
                {availability.icon}
                <span className="ml-1">{availability.text}</span>
              </span>
            </div>
            
            <p className="text-sm text-gray-600 mb-2">{provider.qualification}</p>
            
            <div className="flex items-center gap-4 text-xs text-gray-500 mb-2">
              <span className="flex items-center">
                <Star className="w-3 h-3 mr-1 text-yellow-500" />
                {provider.rating} / 5.0 ({provider.totalRatings} reviews)
              </span>
              <span>{provider.experience} years exp.</span>
            </div>

            {/* Show upcoming bookings if available */}
            {providerBookings && providerBookings.length > 0 && (
              <div className="text-xs text-gray-500">
                {providerBookings.filter((b: any) => b.scheduledDate === bookingDate).length} booking(s) today
              </div>
            )}
          </div>
          
          {isSelected && (
            <CheckCircle className="w-6 h-6 text-blue-600 flex-shrink-0" />
          )}
        </div>
      </button>

      {/* Expandable details */}
      <div className="border-t border-gray-200">
        <button
          onClick={() => setShowDetails(!showDetails)}
          className="w-full px-4 py-2 text-left text-sm text-gray-600 hover:bg-gray-50 flex items-center justify-between"
        >
          <span>View schedule details</span>
          {showDetails ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
        </button>
        
        {showDetails && (
          <div className="px-4 pb-4 bg-gray-50">
            {isLoading ? (
              <div className="text-center py-2">
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600 mx-auto"></div>
                <p className="text-xs text-gray-500 mt-1">Loading schedule...</p>
              </div>
            ) : providerBookings && providerBookings.length > 0 ? (
              <div className="space-y-2">
                <h6 className="text-xs font-medium text-gray-700 mb-2">Today's Schedule:</h6>
                {providerBookings
                  .filter((booking: any) => booking.scheduledDate === bookingDate)
                  .sort((a: any, b: any) => a.scheduledTime.localeCompare(b.scheduledTime))
                  .map((booking: any) => (
                    <div key={booking.id} className="text-xs bg-white p-2 rounded border">
                      <div className="flex items-center justify-between">
                        <span className="font-medium">{booking.scheduledTime}</span>
                        <span className={`px-1.5 py-0.5 rounded text-xs ${
                          booking.status === 'CONFIRMED' ? 'bg-blue-100 text-blue-800' :
                          booking.status === 'IN_PROGRESS' ? 'bg-purple-100 text-purple-800' :
                          'bg-gray-100 text-gray-800'
                        }`}>
                          {booking.status}
                        </span>
                      </div>
                      <div className="text-gray-600 mt-1">
                        {booking.service.name} - {booking.user.name}
                      </div>
                    </div>
                  ))}
              </div>
            ) : (
              <div className="text-xs text-gray-500 text-center py-2">
                No bookings scheduled for today
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProviderAvailabilityCard;
