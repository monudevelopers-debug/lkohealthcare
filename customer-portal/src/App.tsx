import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { HelmetProvider } from 'react-helmet-async';
import { AuthProvider } from './lib/auth/AuthContext';
import { PatientProvider } from './lib/context/PatientContext';
import { Header } from './components/layout/Header';
import { Footer } from './components/layout/Footer';
import { DashboardLayout } from './components/layout/DashboardLayout';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { LandingPage } from './pages/landing/LandingPage';
import { DashboardPage } from './pages/dashboard/DashboardPage';
import { ServicesPage } from './pages/dashboard/ServicesPage';
import { ServiceDetailPage } from './pages/dashboard/ServiceDetailPage';
import { PatientsPage } from './pages/dashboard/PatientsPage';
import { BookingsPage } from './pages/dashboard/BookingsPage';
import { PaymentHistoryPage } from './pages/dashboard/PaymentHistoryPage';
import { FAQPage } from './pages/dashboard/FAQPage';
import { BlogsPage } from './pages/dashboard/BlogsPage';
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
          <PatientProvider>
            <Router>
          <Routes>
            {/* Public routes with Header/Footer */}
            <Route path="/" element={
              <div className="min-h-screen flex flex-col">
                <Header />
                <main className="flex-grow">
                  <LandingPage />
                </main>
                <Footer />
              </div>
            } />
            <Route path="/about" element={
              <div className="min-h-screen flex flex-col">
                <Header />
                <main className="flex-grow p-8 text-center">About Page Coming Soon</main>
                <Footer />
              </div>
            } />
            <Route path="/contact" element={
              <div className="min-h-screen flex flex-col">
                <Header />
                <main className="flex-grow p-8 text-center">Contact Page Coming Soon</main>
                <Footer />
              </div>
            } />
            <Route path="/debug" element={<DebugPage />} />
                
                {/* Protected routes with Sidebar Layout */}
                <Route 
                  path="/dashboard" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <DashboardPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/services" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <ServicesPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/services/:id" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <ServiceDetailPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/patients" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <PatientsPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/bookings" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <BookingsPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/profile" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <ProfilePage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/payments" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <PaymentHistoryPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/reviews" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <div className="p-8 text-center">My Reviews Coming Soon</div>
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/blogs" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <BlogsPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/faq" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <FAQPage />
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/settings" 
                  element={
                    <ProtectedRoute>
                      <DashboardLayout>
                        <div className="p-8 text-center">Settings Coming Soon</div>
                      </DashboardLayout>
                    </ProtectedRoute>
                  } 
                />
              </Routes>
            </Router>
          </PatientProvider>
        </AuthProvider>
      </QueryClientProvider>
    </HelmetProvider>
  );
}

export default App;