import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { 
  TrendingUp, 
  TrendingDown,
  Users,
  Calendar,
  DollarSign,
  Star,
  Activity,
  BarChart3,
  PieChart,
  Download,
  RefreshCw,
  Clock,
  Award
} from 'lucide-react';

import { getProviderAnalytics, getProviderRevenueData, getProviderBookingTrends, getProviderRatingData } from '../services/api';
import Chart from '../components/analytics/Chart';

const Analytics: React.FC = () => {
  const [selectedPeriod, setSelectedPeriod] = useState<'week' | 'month' | 'quarter' | 'year'>('month');
  const [selectedMetric, setSelectedMetric] = useState<'revenue' | 'bookings' | 'ratings' | 'availability'>('revenue');

  // Fetch provider analytics data
  const { data: analyticsData, isLoading: analyticsLoading } = useQuery(
    ['provider-analytics', selectedPeriod],
    () => getProviderAnalytics(selectedPeriod),
    {
      refetchInterval: 300000, // Refetch every 5 minutes
    }
  );

  // Fetch revenue data
  const { data: revenueData, isLoading: revenueLoading } = useQuery(
    ['provider-revenue', selectedPeriod],
    () => getProviderRevenueData(selectedPeriod),
  );

  // Fetch booking trends
  const { data: bookingTrendsData, isLoading: bookingTrendsLoading } = useQuery(
    ['provider-booking-trends', selectedPeriod],
    () => getProviderBookingTrends(selectedPeriod),
  );

  // Fetch rating data
  const { data: ratingData, isLoading: ratingLoading } = useQuery(
    ['provider-ratings', selectedPeriod],
    () => getProviderRatingData(selectedPeriod),
  );

  const metrics = [
    {
      title: 'Total Revenue',
      value: `₹${analyticsData?.totalRevenue || 0}`,
      change: analyticsData?.revenueChange || 0,
      trend: analyticsData?.revenueTrend || 'up',
      icon: DollarSign,
      color: 'green',
    },
    {
      title: 'Total Bookings',
      value: analyticsData?.totalBookings || 0,
      change: analyticsData?.bookingsChange || 0,
      trend: analyticsData?.bookingsTrend || 'up',
      icon: Calendar,
      color: 'blue',
    },
    {
      title: 'Average Rating',
      value: analyticsData?.averageRating || 0,
      change: analyticsData?.ratingChange || 0,
      trend: analyticsData?.ratingTrend || 'up',
      icon: Star,
      color: 'yellow',
    },
    {
      title: 'Availability',
      value: `${analyticsData?.availabilityPercentage || 0}%`,
      change: analyticsData?.availabilityChange || 0,
      trend: analyticsData?.availabilityTrend || 'up',
      icon: Activity,
      color: 'purple',
    },
  ];

  const topServices = analyticsData?.topServices || [];
  const recentReviews = analyticsData?.recentReviews || [];
  const bookingStatusDistribution = analyticsData?.bookingStatusDistribution || {};

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Analytics</h1>
          <p className="text-gray-600">Track your performance and earnings</p>
        </div>
        <div className="flex items-center space-x-3">
          <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 flex items-center gap-2">
            <RefreshCw className="w-4 h-4" />
            Refresh
          </button>
          <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
            <Download className="w-4 h-4" />
            Export
          </button>
        </div>
      </div>

      {/* Period Selector */}
      <div className="bg-white p-6 rounded-lg shadow-sm border">
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">Time Period</h2>
          <div className="flex space-x-2">
            {(['week', 'month', 'quarter', 'year'] as const).map((period) => (
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
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {metrics.map((metric, index) => (
          <div key={index} className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{metric.title}</p>
                <p className="text-2xl font-bold text-gray-900">{metric.value}</p>
              </div>
              <div className={`p-3 rounded-full bg-${metric.color}-100`}>
                <metric.icon className={`w-6 h-6 text-${metric.color}-600`} />
              </div>
            </div>
            <div className="mt-4 flex items-center">
              {metric.trend === 'up' ? (
                <TrendingUp className="w-4 h-4 text-green-500 mr-1" />
              ) : (
                <TrendingDown className="w-4 h-4 text-red-500 mr-1" />
              )}
              <span className={`text-sm font-medium ${
                metric.trend === 'up' ? 'text-green-600' : 'text-red-600'
              }`}>
                {metric.change > 0 ? '+' : ''}{metric.change}%
              </span>
              <span className="text-sm text-gray-500 ml-2">vs previous period</span>
            </div>
          </div>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Revenue Trend</h2>
            <div className="flex items-center text-sm text-gray-500">
              <DollarSign className="w-4 h-4 mr-1" />
              {analyticsData?.revenueTrend === 'up' ? '+' : '-'}{analyticsData?.revenueChange || 0}%
            </div>
          </div>
          <Chart
            data={revenueData || []}
            type="line"
            height={300}
            loading={revenueLoading}
          />
        </div>

        {/* Bookings Chart */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Booking Trends</h2>
            <div className="flex items-center text-sm text-gray-500">
              <Calendar className="w-4 h-4 mr-1" />
              {analyticsData?.bookingsTrend === 'up' ? '+' : '-'}{analyticsData?.bookingsChange || 0}%
            </div>
          </div>
          <Chart
            data={bookingTrendsData || []}
            type="bar"
            height={300}
            loading={bookingTrendsLoading}
          />
        </div>
      </div>

      {/* Rating Chart */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-900">Rating Trends</h2>
          <div className="flex items-center text-sm text-gray-500">
            <Star className="w-4 h-4 mr-1" />
            {analyticsData?.ratingTrend === 'up' ? '+' : '-'}{analyticsData?.ratingChange || 0}%
          </div>
        </div>
        <Chart
          data={ratingData || []}
          type="line"
          height={300}
          loading={ratingLoading}
        />
      </div>

      {/* Bottom Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Top Services */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Top Services</h2>
          <div className="space-y-3">
            {topServices.map((service: any, index: number) => (
              <div key={index} className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center">
                    <span className="text-sm font-medium text-blue-600">{index + 1}</span>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-900">{service.name}</p>
                    <p className="text-xs text-gray-500">{service.category}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900">{service.bookings}</p>
                  <p className="text-xs text-gray-500">bookings</p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Recent Reviews */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Reviews</h2>
          <div className="space-y-3">
            {recentReviews.map((review: any, index: number) => (
              <div key={index} className="border-b border-gray-200 pb-3 last:border-b-0">
                <div className="flex items-center justify-between mb-2">
                  <div className="flex items-center">
                    <div className="flex text-yellow-500">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-3 h-3 ${
                            i < review.rating ? 'text-yellow-500' : 'text-gray-300'
                          }`}
                          fill={i < review.rating ? 'currentColor' : 'none'}
                        />
                      ))}
                    </div>
                    <span className="text-sm font-medium text-gray-900 ml-2">
                      {review.rating}/5
                    </span>
                  </div>
                  <span className="text-xs text-gray-500">
                    {new Date(review.createdAt).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-sm text-gray-700 line-clamp-2">{review.comment}</p>
                <p className="text-xs text-gray-500 mt-1">- {review.customerName}</p>
              </div>
            ))}
          </div>
        </div>

        {/* Booking Status Distribution */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Booking Status</h2>
          <div className="space-y-3">
            {Object.entries(bookingStatusDistribution).map(([status, count]: [string, any]) => (
              <div key={status} className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <div className={`w-3 h-3 rounded-full ${
                    status === 'COMPLETED' ? 'bg-green-500' :
                    status === 'PENDING' ? 'bg-yellow-500' :
                    status === 'IN_PROGRESS' ? 'bg-blue-500' :
                    status === 'CANCELLED' ? 'bg-red-500' : 'bg-gray-500'
                  }`} />
                  <span className="text-sm text-gray-700">{status}</span>
                </div>
                <span className="text-sm font-medium text-gray-900">{count}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Performance Insights */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Performance Insights</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="text-center p-4 bg-blue-50 rounded-lg">
            <div className="text-2xl font-bold text-blue-600">
              {analyticsData?.averageBookingValue || 0}
            </div>
            <div className="text-sm text-blue-800">Avg Booking Value</div>
          </div>
          <div className="text-center p-4 bg-green-50 rounded-lg">
            <div className="text-2xl font-bold text-green-600">
              {analyticsData?.completionRate || 0}%
            </div>
            <div className="text-sm text-green-800">Completion Rate</div>
          </div>
          <div className="text-center p-4 bg-purple-50 rounded-lg">
            <div className="text-2xl font-bold text-purple-600">
              {analyticsData?.responseTime || 0}m
            </div>
            <div className="text-sm text-purple-800">Avg Response Time</div>
          </div>
          <div className="text-center p-4 bg-orange-50 rounded-lg">
            <div className="text-2xl font-bold text-orange-600">
              {analyticsData?.customerSatisfaction || 0}%
            </div>
            <div className="text-sm text-orange-800">Customer Satisfaction</div>
          </div>
        </div>
      </div>

      {/* Goals and Targets */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Monthly Goals</h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm font-medium text-gray-700">Revenue Target</span>
              <span className="text-sm text-gray-500">₹{analyticsData?.revenueTarget || 50000}</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div 
                className="bg-green-600 h-2 rounded-full" 
                style={{ width: `${Math.min((analyticsData?.totalRevenue || 0) / (analyticsData?.revenueTarget || 50000) * 100, 100)}%` }}
              ></div>
            </div>
            <div className="text-xs text-gray-500 mt-1">
              {Math.round((analyticsData?.totalRevenue || 0) / (analyticsData?.revenueTarget || 50000) * 100)}% completed
            </div>
          </div>
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm font-medium text-gray-700">Booking Target</span>
              <span className="text-sm text-gray-500">{analyticsData?.bookingTarget || 50}</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div 
                className="bg-blue-600 h-2 rounded-full" 
                style={{ width: `${Math.min((analyticsData?.totalBookings || 0) / (analyticsData?.bookingTarget || 50) * 100, 100)}%` }}
              ></div>
            </div>
            <div className="text-xs text-gray-500 mt-1">
              {Math.round((analyticsData?.totalBookings || 0) / (analyticsData?.bookingTarget || 50) * 100)}% completed
            </div>
          </div>
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm font-medium text-gray-700">Rating Target</span>
              <span className="text-sm text-gray-500">{analyticsData?.ratingTarget || 4.5}</span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2">
              <div 
                className="bg-yellow-600 h-2 rounded-full" 
                style={{ width: `${Math.min((analyticsData?.averageRating || 0) / (analyticsData?.ratingTarget || 4.5) * 100, 100)}%` }}
              ></div>
            </div>
            <div className="text-xs text-gray-500 mt-1">
              {Math.round((analyticsData?.averageRating || 0) / (analyticsData?.ratingTarget || 4.5) * 100)}% completed
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
