import React from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';

const Layout: React.FC = () => {
  const location = useLocation();

  const nav = [
    { to: '/', label: 'Dashboard' },
    { to: '/bookings', label: 'Bookings' },
    { to: '/providers', label: 'Providers' },
    { to: '/users', label: 'Users' },
    { to: '/services', label: 'Services' },
    { to: '/analytics', label: 'Analytics' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white border-b">
        <div className="mx-auto max-w-7xl px-4 py-4 flex items-center justify-between">
          <div className="flex items-center gap-6">
            <div className="text-xl font-bold text-blue-700">Lucknow Healthcare Admin</div>
            <nav className="hidden md:flex items-center gap-4">
              {nav.map((item) => (
                <Link
                  key={item.to}
                  to={item.to}
                  className={`px-3 py-2 rounded-md text-sm font-medium ${
                    location.pathname === item.to
                      ? 'bg-blue-600 text-white'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  {item.label}
                </Link>
              ))}
            </nav>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl p-4 md:p-6">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
