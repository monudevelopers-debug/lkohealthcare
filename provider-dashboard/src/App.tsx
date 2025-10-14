import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Toaster } from 'react-hot-toast';

// Minimal inline layout and auth for scaffolding so app runs
const Layout: React.FC = () => (
  <div className="p-4">
    <h1 className="text-xl font-semibold mb-4">Provider Dashboard</h1>
    <div id="content" />
  </div>
);

const Dashboard = () => <div>Dashboard</div>;
const Bookings = () => <div>Bookings</div>;
const Earnings = () => <div>Earnings</div>;
const Profile = () => <div>Profile</div>;
const Login = () => <div>Login</div>;

const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => <>{children}</>;

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <div className="min-h-screen bg-gray-50">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/" element={<Layout />}>
                <Route index element={<Dashboard />} />
                <Route path="bookings" element={<Bookings />} />
                <Route path="earnings" element={<Earnings />} />
                <Route path="profile" element={<Profile />} />
              </Route>
            </Routes>
            <Toaster
              position="top-right"
              toastOptions={{
                duration: 4000,
                style: {
                  background: '#363636',
                  color: '#fff',
                },
              }}
            />
          </div>
        </Router>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;
