import { useAuth } from '../../lib/auth/AuthContext';
import { Calendar, Users, CreditCard, Clock, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export function DashboardPage() {
  const { user } = useAuth();

  const stats = [
    {
      name: 'My Patients',
      value: '0',
      icon: Users,
      color: 'bg-blue-500',
      href: '/patients',
    },
    {
      name: 'Active Bookings',
      value: '0',
      icon: Calendar,
      color: 'bg-green-500',
      href: '/bookings',
    },
    {
      name: 'Completed',
      value: '0',
      icon: Clock,
      color: 'bg-purple-500',
      href: '/bookings',
    },
    {
      name: 'Total Spent',
      value: '₹0',
      icon: CreditCard,
      color: 'bg-orange-500',
      href: '/payments',
    },
  ];

  const quickActions = [
    {
      title: 'Add Patient',
      description: 'Add a new family member or patient',
      icon: Users,
      href: '/patients',
      color: 'bg-blue-50 text-blue-600',
    },
    {
      title: 'Book Service',
      description: 'Browse and book healthcare services',
      icon: Calendar,
      href: '/services',
      color: 'bg-green-50 text-green-600',
    },
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Section */}
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h1 className="text-2xl font-bold text-gray-900">
          Welcome back, {user?.name || 'User'}! 👋
        </h1>
        <p className="text-gray-600 mt-2">
          Manage your healthcare services and patient information from your dashboard.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Link
              key={stat.name}
              to={stat.href}
              className="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600">{stat.name}</p>
                  <p className="text-2xl font-bold text-gray-900 mt-2">{stat.value}</p>
                </div>
                <div className={`${stat.color} p-3 rounded-lg`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
              </div>
            </Link>
          );
        })}
      </div>

      {/* Quick Actions */}
      <div>
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {quickActions.map((action) => {
            const Icon = action.icon;
            return (
              <Link
                key={action.title}
                to={action.href}
                className="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-all group"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className={`inline-flex p-3 rounded-lg ${action.color} mb-4`}>
                      <Icon className="w-6 h-6" />
                    </div>
                    <h3 className="text-lg font-semibold text-gray-900 mb-2">
                      {action.title}
                    </h3>
                    <p className="text-gray-600">{action.description}</p>
                  </div>
                  <ArrowRight className="w-5 h-5 text-gray-400 group-hover:text-blue-600 group-hover:translate-x-1 transition-all" />
                </div>
              </Link>
            );
          })}
        </div>
      </div>

      {/* Recent Activity Placeholder */}
      <div className="bg-white rounded-lg shadow-sm p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h2>
        <div className="text-center py-12">
          <Calendar className="w-12 h-12 text-gray-300 mx-auto mb-4" />
          <p className="text-gray-500">No recent activity</p>
          <p className="text-sm text-gray-400 mt-1">
            Your bookings and appointments will appear here
          </p>
        </div>
      </div>
    </div>
  );
}

