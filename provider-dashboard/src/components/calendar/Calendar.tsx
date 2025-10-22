import React, { useState, useEffect } from 'react';
import { useQuery } from 'react-query';
import { 
  ChevronLeft, 
  ChevronRight, 
  Calendar as CalendarIcon,
  Clock,
  User,
  MapPin,
  CheckCircle,
  AlertCircle,
  XCircle
} from 'lucide-react';
import { getProviderBookings } from '../../services/api';
import { Booking } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';
import { getBookingPrivacyStatus } from '../../utils/privacyUtils';

interface CalendarProps {
  onBookingSelect?: (booking: Booking) => void;
}

type ViewMode = 'month' | 'week' | 'day';

const Calendar: React.FC<CalendarProps> = ({ onBookingSelect }) => {
  const { isAuthenticated, user } = useAuth();
  const [currentDate, setCurrentDate] = useState(new Date());
  const [viewMode, setViewMode] = useState<ViewMode>('month');
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);

  // Calculate date range for API call
  const getDateRange = () => {
    const start = new Date(currentDate);
    const end = new Date(currentDate);
    
    switch (viewMode) {
      case 'month':
        start.setDate(1);
        end.setMonth(end.getMonth() + 1);
        end.setDate(0);
        break;
      case 'week':
        const dayOfWeek = start.getDay();
        start.setDate(start.getDate() - dayOfWeek);
        end.setDate(start.getDate() + 6);
        break;
      case 'day':
        end.setDate(start.getDate() + 1);
        break;
    }
    
    return { start, end };
  };

  const { start, end } = getDateRange();

  // Fetch provider bookings for the current date range
  const { data: bookingsData, isLoading, error } = useQuery(
    ['provider-bookings', start.toISOString().split('T')[0], end.toISOString().split('T')[0]],
    () => getProviderBookings(start.toISOString().split('T')[0], end.toISOString().split('T')[0]),
    {
      enabled: isAuthenticated, // Only fetch when authenticated
      refetchInterval: 30000, // Refetch every 30 seconds
    }
  );

  const bookings = bookingsData || [];


  // Navigation functions
  const navigateMonth = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentDate);
    if (direction === 'prev') {
      newDate.setMonth(newDate.getMonth() - 1);
    } else {
      newDate.setMonth(newDate.getMonth() + 1);
    }
    setCurrentDate(newDate);
  };

  const navigateWeek = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentDate);
    const days = direction === 'prev' ? -7 : 7;
    newDate.setDate(newDate.getDate() + days);
    setCurrentDate(newDate);
  };

  const navigateDay = (direction: 'prev' | 'next') => {
    const newDate = new Date(currentDate);
    const days = direction === 'prev' ? -1 : 1;
    newDate.setDate(newDate.getDate() + days);
    setCurrentDate(newDate);
  };

  const goToToday = () => {
    setCurrentDate(new Date());
  };

  // Get bookings for a specific date
  const getBookingsForDate = (date: Date) => {
    const dateStr = date.toISOString().split('T')[0];
    return bookings.filter((booking: Booking) => 
      booking.scheduledDate === dateStr
    );
  };

  // Get status color for booking
  const getBookingStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 border-yellow-300 text-yellow-800';
      case 'CONFIRMED': return 'bg-blue-100 border-blue-300 text-blue-800';
      case 'IN_PROGRESS': return 'bg-purple-100 border-purple-300 text-purple-800';
      case 'COMPLETED': return 'bg-green-100 border-green-300 text-green-800';
      case 'CANCELLED': return 'bg-red-100 border-red-300 text-red-800';
      default: return 'bg-gray-100 border-gray-300 text-gray-800';
    }
  };

  // Get status icon for booking
  const getBookingStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING': return <AlertCircle className="w-3 h-3" />;
      case 'CONFIRMED': return <CheckCircle className="w-3 h-3" />;
      case 'IN_PROGRESS': return <Clock className="w-3 h-3" />;
      case 'COMPLETED': return <CheckCircle className="w-3 h-3" />;
      case 'CANCELLED': return <XCircle className="w-3 h-3" />;
      default: return <AlertCircle className="w-3 h-3" />;
    }
  };

  // Check if provider is busy at a specific time
  const isProviderBusy = (date: Date, time: string) => {
    const dateStr = date.toISOString().split('T')[0];
    const bookingsForDate = bookings.filter((booking: Booking) => 
      booking.scheduledDate === dateStr && 
      (booking.status === 'CONFIRMED' || booking.status === 'IN_PROGRESS')
    );
    
    return bookingsForDate.some((booking: Booking) => {
      const bookingTime = booking.scheduledTime;
      const bookingEndTime = new Date(`2000-01-01T${bookingTime}`).getTime() + (booking.duration * 60 * 60 * 1000);
      const checkTime = new Date(`2000-01-01T${time}`).getTime();
      
      return checkTime >= new Date(`2000-01-01T${bookingTime}`).getTime() && 
             checkTime < bookingEndTime;
    });
  };

  // Render month view
  const renderMonthView = () => {
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - firstDay.getDay());
    
    const days = [];
    const current = new Date(startDate);
    
    for (let i = 0; i < 42; i++) {
      days.push(new Date(current));
      current.setDate(current.getDate() + 1);
    }


    return (
      <div className="grid grid-cols-7 gap-1">
        {/* Day headers */}
        {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
          <div key={day} className="p-2 text-center text-sm font-medium text-gray-500 bg-gray-50">
            {day}
          </div>
        ))}
        
        {/* Calendar days */}
        {days.map((date, index) => {
          const isCurrentMonth = date.getMonth() === month;
          const isToday = date.toDateString() === new Date().toDateString();
          const isSelected = selectedDate?.toDateString() === date.toDateString();
          const dayBookings = getBookingsForDate(date);
          const isBusy = dayBookings.some((booking: Booking) => 
            booking.status === 'CONFIRMED' || booking.status === 'IN_PROGRESS'
          );

          return (
            <div
              key={index}
              className={`min-h-[100px] p-2 border border-gray-200 cursor-pointer hover:bg-gray-50 ${
                !isCurrentMonth ? 'bg-gray-50 text-gray-400' : 'bg-white'
              } ${isToday ? 'bg-blue-50 border-blue-300' : ''} ${
                isSelected ? 'bg-blue-100 border-blue-400' : ''
              } ${isBusy ? 'bg-red-50 border-red-200' : ''}`}
              onClick={() => setSelectedDate(date)}
            >
              <div className="flex items-center justify-between mb-1">
                <span className={`text-sm font-medium ${isToday ? 'text-blue-600' : 'text-gray-900'}`}>
                  {date.getDate()}
                </span>
                {isBusy && (
                  <div className="w-2 h-2 bg-red-500 rounded-full" title="Provider is busy" />
                )}
              </div>
              
              {/* Show bookings for this day */}
              <div className="space-y-1">
                {dayBookings.slice(0, 2).map((booking: Booking) => (
                  <div
                    key={booking.id}
                    className={`text-xs p-1 rounded border ${getBookingStatusColor(booking.status)} cursor-pointer hover:shadow-sm`}
                    onClick={(e) => {
                      e.stopPropagation();
                      onBookingSelect?.(booking);
                    }}
                  >
                    <div className="flex items-center space-x-1">
                      {getBookingStatusIcon(booking.status)}
                      <span className="truncate">{booking.scheduledTime}</span>
                    </div>
                    <div className="truncate font-medium">{booking.service.name}</div>
                    <div className="truncate text-xs opacity-75">{booking.user.name}</div>
                    {user?.role === 'PROVIDER' && !getBookingPrivacyStatus(booking, user.role).contactDetailsVisible && (
                      <div className="text-xs text-yellow-600 flex items-center">
                        <AlertCircle className="w-3 h-3 mr-1" />
                        Contact hidden
                      </div>
                    )}
                  </div>
                ))}
                {dayBookings.length > 2 && (
                  <div className="text-xs text-gray-500 text-center">
                    +{dayBookings.length - 2} more
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>
    );
  };

  // Render week view
  const renderWeekView = () => {
    const startOfWeek = new Date(currentDate);
    const dayOfWeek = startOfWeek.getDay();
    startOfWeek.setDate(startOfWeek.getDate() - dayOfWeek);
    
    const weekDays = [];
    for (let i = 0; i < 7; i++) {
      const date = new Date(startOfWeek);
      date.setDate(startOfWeek.getDate() + i);
      weekDays.push(date);
    }

    return (
      <div className="grid grid-cols-7 gap-1">
        {weekDays.map((date, index) => {
          const isToday = date.toDateString() === new Date().toDateString();
          const isSelected = selectedDate?.toDateString() === date.toDateString();
          const dayBookings = getBookingsForDate(date);
          const isBusy = dayBookings.some((booking: Booking) => 
            booking.status === 'CONFIRMED' || booking.status === 'IN_PROGRESS'
          );

          return (
            <div
              key={index}
              className={`min-h-[200px] p-2 border border-gray-200 bg-white ${
                isToday ? 'bg-blue-50 border-blue-300' : ''
              } ${isSelected ? 'bg-blue-100 border-blue-400' : ''} ${
                isBusy ? 'bg-red-50 border-red-200' : ''
              }`}
            >
              <div className="flex items-center justify-between mb-2">
                <div>
                  <div className="text-sm font-medium text-gray-900">
                    {date.toLocaleDateString('en-US', { weekday: 'short' })}
                  </div>
                  <div className={`text-lg font-bold ${isToday ? 'text-blue-600' : 'text-gray-900'}`}>
                    {date.getDate()}
                  </div>
                </div>
                {isBusy && (
                  <div className="w-3 h-3 bg-red-500 rounded-full" title="Provider is busy" />
                )}
              </div>
              
              {/* Time slots for the day */}
              <div className="space-y-1">
                {dayBookings.map((booking: Booking) => (
                  <div
                    key={booking.id}
                    className={`text-xs p-2 rounded border ${getBookingStatusColor(booking.status)} cursor-pointer hover:shadow-sm`}
                    onClick={() => onBookingSelect?.(booking)}
                  >
                    <div className="flex items-center space-x-1 mb-1">
                      {getBookingStatusIcon(booking.status)}
                      <span className="font-medium">{booking.scheduledTime}</span>
                    </div>
                    <div className="truncate font-medium">{booking.service.name}</div>
                    <div className="truncate text-xs opacity-75">{booking.user.name}</div>
                    {user?.role === 'PROVIDER' && !getBookingPrivacyStatus(booking, user.role).contactDetailsVisible && (
                      <div className="text-xs text-yellow-600 flex items-center">
                        <AlertCircle className="w-3 h-3 mr-1" />
                        Contact hidden
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          );
        })}
      </div>
    );
  };

  // Render day view
  const renderDayView = () => {
    const dayBookings = getBookingsForDate(currentDate);
    const isToday = currentDate.toDateString() === new Date().toDateString();
    const isBusy = dayBookings.some((booking: Booking) => 
      booking.status === 'CONFIRMED' || booking.status === 'IN_PROGRESS'
    );

    return (
      <div className="space-y-4">
        <div className={`p-4 rounded-lg border ${
          isToday ? 'bg-blue-50 border-blue-300' : ''
        } ${isBusy ? 'bg-red-50 border-red-200' : ''}`}>
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-lg font-semibold text-gray-900">
                {currentDate.toLocaleDateString('en-US', { 
                  weekday: 'long', 
                  year: 'numeric', 
                  month: 'long', 
                  day: 'numeric' 
                })}
              </h3>
              {isBusy && (
                <div className="flex items-center space-x-2 text-red-600 text-sm">
                  <div className="w-2 h-2 bg-red-500 rounded-full"></div>
                  <span>Provider is busy today</span>
                </div>
              )}
            </div>
          </div>
          
          {dayBookings.length === 0 ? (
            <div className="text-center py-8 text-gray-500">
              <CalendarIcon className="w-12 h-12 mx-auto mb-2 opacity-50" />
              <p>No bookings scheduled for this day</p>
            </div>
          ) : (
            <div className="space-y-3">
              {dayBookings.map((booking: Booking) => (
                <div
                  key={booking.id}
                  className={`p-4 rounded-lg border ${getBookingStatusColor(booking.status)} cursor-pointer hover:shadow-md transition-shadow`}
                  onClick={() => onBookingSelect?.(booking)}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center space-x-2 mb-2">
                        {getBookingStatusIcon(booking.status)}
                        <span className="font-medium">{booking.scheduledTime}</span>
                        <span className="text-sm opacity-75">({booking.duration}h)</span>
                      </div>
                      <div className="font-semibold text-gray-900 mb-1">{booking.service.name}</div>
                      <div className="flex items-center space-x-4 text-sm text-gray-600">
                        <div className="flex items-center space-x-1">
                          <User className="w-4 h-4" />
                          <span>{booking.user.name}</span>
                        </div>
                        <div className="flex items-center space-x-1">
                          <MapPin className="w-4 h-4" />
                          <span>₹{booking.totalAmount}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  };

  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="text-yellow-500 mb-2">🔒</div>
          <p className="text-yellow-600">Authentication Required</p>
          <p className="text-sm text-gray-500 mt-1">Please log in to view your schedule</p>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-2 text-gray-600">Loading your schedule...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <div className="text-red-500 mb-2">⚠️</div>
          <p className="text-red-600">Failed to load schedule</p>
          <p className="text-sm text-gray-500 mt-1">Please try refreshing the page</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200">
      {/* Header */}
      <div className="p-4 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            <h2 className="text-xl font-semibold text-gray-900">My Schedule</h2>
            <div className="flex items-center space-x-2">
              <button
                onClick={() => setViewMode('month')}
                className={`px-3 py-1 text-sm rounded-md ${
                  viewMode === 'month' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                Month
              </button>
              <button
                onClick={() => setViewMode('week')}
                className={`px-3 py-1 text-sm rounded-md ${
                  viewMode === 'week' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                Week
              </button>
              <button
                onClick={() => setViewMode('day')}
                className={`px-3 py-1 text-sm rounded-md ${
                  viewMode === 'day' ? 'bg-blue-100 text-blue-700' : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                Day
              </button>
            </div>
          </div>
          
          <div className="flex items-center space-x-2">
            <button
              onClick={goToToday}
              className="px-3 py-1 text-sm text-gray-600 hover:bg-gray-100 rounded-md"
            >
              Today
            </button>
            <div className="flex items-center space-x-1">
              <button
                onClick={() => {
                  if (viewMode === 'month') navigateMonth('prev');
                  else if (viewMode === 'week') navigateWeek('prev');
                  else navigateDay('prev');
                }}
                className="p-1 text-gray-600 hover:bg-gray-100 rounded"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => {
                  if (viewMode === 'month') navigateMonth('next');
                  else if (viewMode === 'week') navigateWeek('next');
                  else navigateDay('next');
                }}
                className="p-1 text-gray-600 hover:bg-gray-100 rounded"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
        
        {/* Current date display */}
        <div className="mt-2">
          <h3 className="text-lg font-medium text-gray-900">
            {viewMode === 'month' && currentDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
            {viewMode === 'week' && `Week of ${start.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}`}
            {viewMode === 'day' && currentDate.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric', year: 'numeric' })}
          </h3>
        </div>
      </div>

      {/* Calendar Content */}
      <div className="p-4">
        {viewMode === 'month' && renderMonthView()}
        {viewMode === 'week' && renderWeekView()}
        {viewMode === 'day' && renderDayView()}
      </div>
    </div>
  );
};

export default Calendar;
