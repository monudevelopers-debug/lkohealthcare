import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { HelmetProvider } from 'react-helmet-async';
import { AuthProvider } from './lib/auth/AuthContext';
import { Header } from './components/layout/Header';
import { Footer } from './components/layout/Footer';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { LandingPage } from './pages/landing/LandingPage';
import { ServicesPage } from './pages/dashboard/ServicesPage';
import { ServiceDetailPage } from './pages/dashboard/ServiceDetailPage';
import { BookingsPage } from './pages/dashboard/BookingsPage';
import { ProfilePage } from './pages/dashboard/ProfilePage';
import { DebugPage } from './pages/dashboard/DebugPage';

// Create a client
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
    <HelmetProvider>
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
        <Router>
          <div className="min-h-screen flex flex-col">
            <Header />
            <main className="flex-grow">
              <Routes>
                  <Route path="/" element={<LandingPage />} />
                  <Route path="/services" element={<ServicesPage />} />
                  <Route path="/services/:id" element={<ServiceDetailPage />} />
                  <Route 
                    path="/bookings" 
                    element={
                      <ProtectedRoute>
                        <BookingsPage />
                      </ProtectedRoute>
                    } 
                  />
                  <Route 
                    path="/profile" 
                    element={
                      <ProtectedRoute>
                        <ProfilePage />
                      </ProtectedRoute>
                    } 
                  />
                <Route path="/about" element={<div className="p-8 text-center">About Page Coming Soon</div>} />
                <Route path="/contact" element={<div className="p-8 text-center">Contact Page Coming Soon</div>} />
                <Route path="/debug" element={<DebugPage />} />
              </Routes>
              </main>
              <Footer />
            </div>
          </Router>
        </AuthProvider>
      </QueryClientProvider>
    </HelmetProvider>
  );
}

export default App;