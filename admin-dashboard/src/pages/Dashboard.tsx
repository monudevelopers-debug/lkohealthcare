import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { 
  Users, 
  Calendar, 
  DollarSign, 
  TrendingUp,
  Activity,
  AlertCircle,
  CheckCircle
} from 'lucide-react';

import { getAdminStats, getSystemHealth } from '../services/api';
import StatsCard from '../components/dashboard/StatsCard';
import Chart from '../components/analytics/Chart';
import RecentActivity from '../components/dashboard/RecentActivity';
import SystemHealth from '../components/dashboard/SystemHealth';

const Dashboard: React.FC = () => {
  const [selectedPeriod, setSelectedPeriod] = useState<'today' | 'week' | 'month' | 'year'>('month');

  // Fetch admin statistics
  const { data: stats, isLoading: statsLoading } = useQuery(
    ['admin-stats', selectedPeriod],
    () => getAdminStats(selectedPeriod),
    {
      refetchInterval: 60000, // Refetch every minute
    }
  );

  // Fetch system health
  const { data: systemHealth, isLoading: healthLoading } = useQuery(
    'system-health',
    getSystemHealth,
    {
      refetchInterval: 30000, // Refetch every 30 seconds
    }
  );

  const statsData = [
    {
      title: 'Total Users',
      value: stats?.totalUsers || 0,
      icon: Users,
      color: 'blue',
      change: stats?.usersChange || 0,
      trend: stats?.usersTrend || 'up',
    },
    {
      title: 'Active Bookings',
      value: stats?.activeBookings || 0,
      icon: Calendar,
      color: 'green',
      change: stats?.bookingsChange || 0,
      trend: stats?.bookingsTrend || 'up',
    },
    {
      title: 'Total Revenue',
      value: `₹${stats?.totalRevenue || 0}`,
      icon: DollarSign,
      color: 'purple',
      change: stats?.revenueChange || 0,
      trend: stats?.revenueTrend || 'up',
    },
    {
      title: 'Providers',
      value: stats?.totalProviders || 0,
      icon: Activity,
      color: 'orange',
      change: stats?.providersChange || 0,
      trend: stats?.providersTrend || 'up',
    },
  ];

  const quickActions = [
    {
      title: 'Manage Users',
      description: 'View and manage user accounts',
      icon: Users,
      color: 'blue',
      href: '/users',
    },
    {
      title: 'View Bookings',
      description: 'Monitor all bookings and appointments',
      icon: Calendar,
      color: 'green',
      href: '/bookings',
    },
    {
      title: 'Provider Management',
      description: 'Manage healthcare providers',
      icon: Activity,
      color: 'orange',
      href: '/providers',
    },
    {
      title: 'Analytics',
      description: 'View detailed analytics and reports',
      icon: TrendingUp,
      color: 'purple',
      href: '/analytics',
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="text-gray-600">System overview and management</p>
        </div>
        
        {/* Period Selector */}
        <div className="flex space-x-2">
          {(['today', 'week', 'month', 'year'] as const).map((period) => (
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

      {/* System Health Alert */}
      {systemHealth && systemHealth.overall !== 'healthy' && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <div className="flex items-center">
            <AlertCircle className="w-5 h-5 text-red-600 mr-3" />
            <div>
              <h3 className="text-sm font-medium text-red-800">System Health Alert</h3>
              <p className="text-sm text-red-700 mt-1">
                System components are not healthy
              </p>
            </div>
          </div>
        </div>
      )}

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
            trend={stat.trend}
            loading={statsLoading}
          />
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Revenue Chart */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Revenue Trend</h2>
            <div className="flex items-center text-sm text-gray-500">
              <TrendingUp className="w-4 h-4 mr-1" />
              {stats?.revenueTrend === 'up' ? '+' : '-'}{stats?.revenueChange || 0}%
            </div>
          </div>
          <Chart
            data={[]}
            type="line"
            height={200}
          />
        </div>

        {/* Bookings Chart */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">Bookings Overview</h2>
            <div className="flex items-center text-sm text-gray-500">
              <Calendar className="w-4 h-4 mr-1" />
              {stats?.bookingsTrend === 'up' ? '+' : '-'}{stats?.bookingsChange || 0}%
            </div>
          </div>
          <Chart
            data={[]}
            type="bar"
            height={200}
          />
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {quickActions.map((action, index) => (
            <a
              key={index}
              href={action.href}
              className="flex items-center p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
            >
              <div className={`p-2 rounded-lg bg-${action.color}-100 mr-3`}>
                <action.icon className={`w-5 h-5 text-${action.color}-600`} />
              </div>
              <div>
                <div className="font-medium text-gray-900">{action.title}</div>
                <div className="text-sm text-gray-600">{action.description}</div>
              </div>
            </a>
          ))}
        </div>
      </div>

      {/* System Health & Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <SystemHealth health={systemHealth} loading={healthLoading} />
        <RecentActivity />
      </div>

      {/* Alerts & Notifications */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="p-6 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900 flex items-center">
            <AlertCircle className="w-5 h-5 text-orange-500 mr-2" />
            System Alerts
          </h2>
        </div>
        <div className="p-6">
          <div className="space-y-4">
            <div className="text-center py-8">
              <CheckCircle className="w-12 h-12 text-green-500 mx-auto mb-4" />
              <p className="text-gray-500">No alerts at this time</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
