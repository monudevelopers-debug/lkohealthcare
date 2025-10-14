import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { 
  Calendar, 
  Clock, 
  DollarSign, 
  Users, 
  Star,
  AlertCircle
} from 'lucide-react';

import { getProviderStats, getRecentBookings } from '../services/api';
import { Booking } from '../types/booking';
import BookingCard from '../components/bookings/BookingCard';
import StatsCard from '../components/dashboard/StatsCard';
import RecentActivity from '../components/dashboard/RecentActivity';

const Dashboard: React.FC = () => {
  const [selectedPeriod, setSelectedPeriod] = useState<'today' | 'week' | 'month'>('today');

  // Fetch provider statistics
  const { data: stats, isLoading: statsLoading } = useQuery(
    ['provider-stats', selectedPeriod],
    () => getProviderStats(selectedPeriod),
    {
      refetchInterval: 30000, // Refetch every 30 seconds
    }
  );

  // Fetch recent bookings
  const { data: recentBookings, isLoading: bookingsLoading } = useQuery(
    'recent-bookings',
    () => getRecentBookings(10),
    {
      refetchInterval: 10000, // Refetch every 10 seconds
    }
  );

  const statsData = [
    {
      title: 'Today\'s Bookings',
      value: stats?.todayBookings || 0,
      icon: Calendar,
      color: 'blue',
      change: stats?.todayBookingsChange || 0,
    },
    {
      title: 'Active Bookings',
      value: stats?.activeBookings || 0,
      icon: Clock,
      color: 'green',
      change: stats?.activeBookingsChange || 0,
    },
    {
      title: 'Total Earnings',
      value: `₹${stats?.totalEarnings || 0}`,
      icon: DollarSign,
      color: 'purple',
      change: stats?.earningsChange || 0,
    },
    {
      title: 'Rating',
      value: stats?.rating || 0,
      icon: Star,
      color: 'yellow',
      change: stats?.ratingChange || 0,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600">Welcome back! Here's what's happening today.</p>
        </div>
        
        {/* Period Selector */}
        <div className="flex space-x-2">
          {(['today', 'week', 'month'] as const).map((period) => (
            <button
              key={period}
              onClick={() => setSelectedPeriod(period)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                selectedPeriod === period
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              {period.charAt(0).toUpperCase() + period.slice(1)}
            </button>
          ))}
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statsData.map((stat, index) => (
          <StatsCard
            key={index}
            title={stat.title}
            value={stat.value}
            icon={stat.icon}
            color={stat.color}
            change={stat.change}
            loading={statsLoading}
          />
        ))}
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <button className="flex items-center p-4 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors">
            <Calendar className="w-5 h-5 text-blue-600 mr-3" />
            <div className="text-left">
              <div className="font-medium text-gray-900">Update Availability</div>
              <div className="text-sm text-gray-600">Set your working hours</div>
            </div>
          </button>
          
          <button className="flex items-center p-4 bg-green-50 rounded-lg hover:bg-green-100 transition-colors">
            <Users className="w-5 h-5 text-green-600 mr-3" />
            <div className="text-left">
              <div className="font-medium text-gray-900">View Bookings</div>
              <div className="text-sm text-gray-600">Manage your appointments</div>
            </div>
          </button>
          
          <button className="flex items-center p-4 bg-purple-50 rounded-lg hover:bg-purple-100 transition-colors">
            <DollarSign className="w-5 h-5 text-purple-600 mr-3" />
            <div className="text-left">
              <div className="font-medium text-gray-900">Earnings</div>
              <div className="text-sm text-gray-600">Track your income</div>
            </div>
          </button>
        </div>
      </div>

      {/* Recent Bookings */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-900">Recent Bookings</h2>
              <button className="text-blue-600 hover:text-blue-700 text-sm font-medium">
                View All
              </button>
            </div>
          </div>
          <div className="p-6">
            {bookingsLoading ? (
              <div className="space-y-4">
                {[...Array(3)].map((_, index) => (
                  <div key={index} className="animate-pulse">
                    <div className="h-20 bg-gray-200 rounded-lg"></div>
                  </div>
                ))}
              </div>
            ) : recentBookings?.length ? (
              <div className="space-y-4">
                {recentBookings.slice(0, 3).map((booking: Booking) => (
                  <BookingCard key={booking.id} booking={booking} compact />
                ))}
              </div>
            ) : (
              <div className="text-center py-8">
                <Calendar className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">No recent bookings</p>
              </div>
            )}
          </div>
        </div>

        {/* Recent Activity */}
        <RecentActivity />
      </div>

      {/* Alerts */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center">
            <AlertCircle className="w-5 h-5 text-orange-500 mr-2" />
            Alerts & Notifications
          </h2>
        </div>
        <div className="p-6">
          <div className="space-y-4">
            <div className="flex items-start p-4 bg-yellow-50 rounded-lg">
              <AlertCircle className="w-5 h-5 text-yellow-600 mt-0.5 mr-3" />
              <div>
                <p className="font-medium text-yellow-800">Profile Incomplete</p>
                <p className="text-sm text-yellow-700 mt-1">
                  Complete your profile to get more bookings
                </p>
              </div>
            </div>
            
            <div className="flex items-start p-4 bg-blue-50 rounded-lg">
              <Clock className="w-5 h-5 text-blue-600 mt-0.5 mr-3" />
              <div>
                <p className="font-medium text-blue-800">Upcoming Booking</p>
                <p className="text-sm text-blue-700 mt-1">
                  You have a booking in 2 hours
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
