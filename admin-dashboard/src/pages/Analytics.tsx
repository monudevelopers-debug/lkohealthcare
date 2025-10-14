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
  RefreshCw
} from 'lucide-react';

import { getAnalytics, getRevenueData, getUserGrowthData, getBookingTrends } from '../services/api';
import Chart from '../components/analytics/Chart';

const Analytics: React.FC = () => {
  const [selectedPeriod, setSelectedPeriod] = useState<'week' | 'month' | 'quarter' | 'year'>('month');
  const [selectedMetric, setSelectedMetric] = useState<'revenue' | 'bookings' | 'users' | 'providers'>('revenue');

  // Fetch analytics data
  const { data: analyticsData, isLoading: analyticsLoading } = useQuery(
    ['analytics', selectedPeriod],
    () => getAnalytics(selectedPeriod),
    {
      refetchInterval: 300000, // Refetch every 5 minutes
    }
  );

  // Fetch revenue data
  const { data: revenueData, isLoading: revenueLoading } = useQuery(
    ['revenue', selectedPeriod],
    () => getRevenueData(selectedPeriod),
  );

  // Fetch user growth data
  const { data: userGrowthData, isLoading: userGrowthLoading } = useQuery(
    ['user-growth', selectedPeriod],
    () => getUserGrowthData(selectedPeriod),
  );

  // Fetch booking trends
  const { data: bookingTrendsData, isLoading: bookingTrendsLoading } = useQuery(
    ['booking-trends', selectedPeriod],
    () => getBookingTrends(selectedPeriod),
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
      title: 'Active Users',
      value: analyticsData?.activeUsers || 0,
      change: analyticsData?.usersChange || 0,
      trend: analyticsData?.usersTrend || 'up',
      icon: Users,
      color: 'purple',
    },
    {
      title: 'Active Providers',
      value: analyticsData?.activeProviders || 0,
      change: analyticsData?.providersChange || 0,
      trend: analyticsData?.providersTrend || 'up',
      icon: Activity,
      color: 'orange',
    },
  ];

  const topServices = analyticsData?.topServices || [];
  const topProviders = analyticsData?.topProviders || [];
  const bookingStatusDistribution = analyticsData?.bookingStatusDistribution || {};

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Analytics Dashboard</h1>
          <p className="text-gray-600">Comprehensive insights and performance metrics</p>
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

      {/* User Growth Chart */}
      <div className="bg-white rounded-lg shadow-sm border p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-semibold text-gray-900">User Growth</h2>
          <div className="flex items-center text-sm text-gray-500">
            <Users className="w-4 h-4 mr-1" />
            {analyticsData?.usersTrend === 'up' ? '+' : '-'}{analyticsData?.usersChange || 0}%
          </div>
        </div>
        <Chart
          data={userGrowthData || []}
          type="area"
          height={300}
          loading={userGrowthLoading}
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

        {/* Top Providers */}
        <div className="bg-white rounded-lg shadow-sm border p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Top Providers</h2>
          <div className="space-y-3">
            {topProviders.map((provider: any, index: number) => (
              <div key={index} className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center">
                    <span className="text-sm font-medium text-green-600">{index + 1}</span>
                  </div>
                  <div>
                    <p className="text-sm font-medium text-gray-900">{provider.name}</p>
                    <div className="flex items-center">
                      <Star className="w-3 h-3 text-yellow-500 mr-1" />
                      <span className="text-xs text-gray-500">{provider.rating}/5.0</span>
                    </div>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-sm font-medium text-gray-900">{provider.bookings}</p>
                  <p className="text-xs text-gray-500">bookings</p>
                </div>
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
              {analyticsData?.customerSatisfaction || 0}
            </div>
            <div className="text-sm text-purple-800">Customer Rating</div>
          </div>
          <div className="text-center p-4 bg-orange-50 rounded-lg">
            <div className="text-2xl font-bold text-orange-600">
              {analyticsData?.responseTime || 0}m
            </div>
            <div className="text-sm text-orange-800">Avg Response Time</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
