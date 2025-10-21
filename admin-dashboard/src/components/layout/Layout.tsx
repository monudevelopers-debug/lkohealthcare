import React, { useState, useEffect } from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { 
  LayoutDashboard, 
  Calendar, 
  Users, 
  UserCheck,
  Briefcase,
  FileCheck,
  AlertCircle,
  BarChart3, 
  LogOut,
  Menu,
  X,
  Bell
} from 'lucide-react';
import { useQuery } from 'react-query';

const Layout: React.FC = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated, isLoading, user, logout } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  // Fetch pending service requests count
  const { data: pendingServiceCount = 0 } = useQuery(
    'pending-requests-count',
    async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await fetch('http://localhost:8080/api/service-requests/pending/count', {
          headers: { 'Authorization': `Bearer ${token}` },
        });
        if (!response.ok) return 0;
        const data = await response.json();
        return data.count || 0;
      } catch {
        return 0;
      }
    },
    {
      refetchInterval: 30000,
      enabled: isAuthenticated,
    }
  );

  // Fetch manage bookings count (unassigned + rejections)
  const { data: manageBookingsCount = 0 } = useQuery(
    'manage-bookings-count',
    async () => {
      try {
        const token = localStorage.getItem('token');
        
        // Fetch unassigned bookings
        const unassignedRes = await fetch('http://localhost:8080/api/bookings/unassigned', {
          headers: { 'Authorization': `Bearer ${token}` },
        });
        const unassigned = unassignedRes.ok ? await unassignedRes.json() : [];
        
        // Fetch rejection requests
        const rejectionsRes = await fetch('http://localhost:8080/api/booking-rejections/pending', {
          headers: { 'Authorization': `Bearer ${token}` },
        });
        const rejections = rejectionsRes.ok ? await rejectionsRes.json() : [];
        
        return (unassigned.length || 0) + (rejections.length || 0);
      } catch {
        return 0;
      }
    },
    {
      refetchInterval: 15000,
      enabled: isAuthenticated,
    }
  );

  const nav = [
    { to: '/', label: 'Dashboard', icon: LayoutDashboard },
    { to: '/bookings', label: 'All Bookings', icon: Calendar },
    { to: '/manage-bookings', label: 'Manage Bookings', icon: AlertCircle, badge: manageBookingsCount },
    { to: '/providers', label: 'Providers', icon: UserCheck },
    { to: '/users', label: 'Users', icon: Users },
    { to: '/services', label: 'Services', icon: Briefcase },
    { to: '/service-requests', label: 'Service Requests', icon: FileCheck, badge: pendingServiceCount },
    { to: '/analytics', label: 'Analytics', icon: BarChart3 },
  ];

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      navigate('/login');
    }
  }, [isAuthenticated, isLoading, navigate]);

  // Show loading state while checking authentication
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  // Don't render if not authenticated
  if (!isAuthenticated) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <aside className={`
        fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-gray-200 transform transition-transform duration-200 ease-in-out
        lg:relative lg:translate-x-0
        ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}
      `}>
        {/* Sidebar Header */}
        <div className="h-16 flex items-center justify-between px-4 border-b border-gray-200">
          <h1 className="text-lg font-bold text-blue-700">Admin Portal</h1>
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden text-gray-500 hover:text-gray-700"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-1 overflow-y-auto" style={{ maxHeight: 'calc(100vh - 180px)' }}>
          {nav.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.to;
            
            return (
              <Link
                key={item.to}
                to={item.to}
                onClick={() => setSidebarOpen(false)}
                className={`
                  flex items-center justify-between gap-3 px-4 py-3 rounded-lg text-sm font-medium transition-colors
                  ${isActive
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-700 hover:bg-gray-100'
                  }
                `}
              >
                <div className="flex items-center gap-3">
                  <Icon className="w-5 h-5" />
                  {item.label}
                </div>
                {item.badge !== undefined && item.badge > 0 && (
                  <span className={`
                    inline-flex items-center justify-center px-2 py-0.5 text-xs font-bold rounded-full
                    ${isActive ? 'bg-white text-blue-600' : 'bg-yellow-100 text-yellow-800'}
                  `}>
                    {item.badge}
                  </span>
                )}
              </Link>
            );
          })}
        </nav>

        {/* User Info & Logout */}
        <div className="border-t border-gray-200 p-4">
          {user && (
            <div className="mb-3 px-3">
              <p className="text-xs text-gray-500">Logged in as</p>
              <p className="text-sm font-medium text-gray-900 truncate">
                {user.name || user.email}
              </p>
              <p className="text-xs text-gray-500 capitalize">{user.role}</p>
            </div>
          )}
          <button
            onClick={logout}
            className="w-full flex items-center justify-center gap-2 px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-lg hover:bg-red-700 transition-colors"
          >
            <LogOut className="w-4 h-4" />
            Logout
          </button>
        </div>
      </aside>

      {/* Overlay for mobile */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Top Bar (Mobile) */}
        <header className="lg:hidden h-16 bg-white border-b border-gray-200 flex items-center justify-between px-4">
          <button
            onClick={() => setSidebarOpen(true)}
            className="text-gray-500 hover:text-gray-700"
          >
            <Menu className="w-6 h-6" />
          </button>
          <h1 className="text-lg font-bold text-blue-700">Admin Portal</h1>
          <div className="relative">
            {(pendingServiceCount + manageBookingsCount) > 0 && (
              <button className="relative text-gray-500 hover:text-gray-700">
                <Bell className="w-6 h-6" />
                <span className="absolute -top-1 -right-1 inline-flex items-center justify-center px-1.5 py-0.5 text-xs font-bold text-white bg-red-600 rounded-full">
                  {pendingServiceCount + manageBookingsCount}
                </span>
              </button>
            )}
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 p-4 md:p-6 lg:p-8 overflow-auto">
          <div className="max-w-7xl mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;
