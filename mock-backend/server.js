const express = require('express');
const cors = require('cors');
const app = express();
const port = 8080;

// Middleware
app.use(cors());
app.use(express.json());

// Mock data
const users = [
  { id: '1', name: 'John Doe', email: 'john@example.com', phone: '+91-9876543210', role: 'CUSTOMER', status: 'ACTIVE', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '2', name: 'Jane Smith', email: 'jane@example.com', phone: '+91-9876543211', role: 'PROVIDER', status: 'ACTIVE', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '3', name: 'Admin User', email: 'admin@example.com', phone: '+91-9876543212', role: 'ADMIN', status: 'ACTIVE', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
];

const serviceCategories = [
  { id: '1', name: 'Nursing Care', description: 'Professional nursing services', isActive: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '2', name: 'Elderly Care', description: 'Specialized elderly care services', isActive: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '3', name: 'Physiotherapy', description: 'Physical therapy and rehabilitation', isActive: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
];

const services = [
  { id: '1', name: 'Home Nursing', description: 'Professional nursing care at home', price: 500, duration: 60, category: serviceCategories[0], isActive: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '2', name: 'Elderly Companion', description: 'Companion services for elderly', price: 300, duration: 120, category: serviceCategories[1], isActive: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '3', name: 'Physiotherapy Session', description: 'Physical therapy session', price: 800, duration: 45, category: serviceCategories[2], isActive: true, createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
];

const providers = [
  { id: '1', name: 'Dr. Priya Sharma', email: 'priya@example.com', phone: '+91-9876543213', qualifications: 'BSc Nursing, MSc', experience: 5, rating: 4.8, isAvailable: true, services: [services[0]], createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '2', name: 'Mr. Rajesh Kumar', email: 'rajesh@example.com', phone: '+91-9876543214', qualifications: 'BPT, MPT', experience: 8, rating: 4.9, isAvailable: true, services: [services[2]], createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
];

const bookings = [
  { id: '1', user: users[0], service: services[0], provider: providers[0], scheduledDate: '2024-12-25', scheduledTime: '10:00', status: 'CONFIRMED', totalAmount: 500, notes: 'Regular checkup', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
  { id: '2', user: users[0], service: services[2], provider: providers[1], scheduledDate: '2024-12-26', scheduledTime: '14:00', status: 'PENDING', totalAmount: 800, notes: 'Knee pain treatment', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
];

const payments = [
  { id: '1', booking: bookings[0], amount: 500, status: 'COMPLETED', method: 'ONLINE', transactionId: 'TXN123456', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
];

const reviews = [
  { id: '1', user: users[0], provider: providers[0], booking: bookings[0], rating: 5, comment: 'Excellent service!', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' }
];

// Auth endpoints
app.post('/api/auth/login', (req, res) => {
  const { email, password } = req.body;
  const user = users.find(u => u.email === email);
  
  if (user && password === 'password123') {
    res.json({
      token: 'mock-jwt-token-' + user.id,
      user: user,
      expiresIn: 3600
    });
  } else {
    res.status(401).json({ message: 'Invalid credentials' });
  }
});

app.post('/api/auth/logout', (req, res) => {
  res.json({ message: 'Logged out successfully' });
});

app.get('/api/auth/me', (req, res) => {
  const token = req.headers.authorization?.replace('Bearer ', '');
  if (token && token.startsWith('mock-jwt-token-')) {
    const userId = token.replace('mock-jwt-token-', '');
    const user = users.find(u => u.id === userId);
    if (user) {
      res.json(user);
    } else {
      res.status(404).json({ message: 'User not found' });
    }
  } else {
    res.status(401).json({ message: 'Unauthorized' });
  }
});

// User management
app.get('/api/users', (req, res) => {
  const page = parseInt(req.query.page) || 0;
  const size = parseInt(req.query.size) || 20;
  const search = req.query.search;
  
  let filteredUsers = users;
  if (search) {
    filteredUsers = users.filter(u => 
      u.name.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase())
    );
  }
  
  const start = page * size;
  const end = start + size;
  const content = filteredUsers.slice(start, end);
  
  res.json({
    content: content,
    totalElements: filteredUsers.length
  });
});

app.get('/api/users/:id', (req, res) => {
  const user = users.find(u => u.id === req.params.id);
  if (user) {
    res.json(user);
  } else {
    res.status(404).json({ message: 'User not found' });
  }
});

// Service management
app.get('/api/services', (req, res) => {
  const page = parseInt(req.query.page) || 0;
  const size = parseInt(req.query.size) || 20;
  const categoryId = req.query.categoryId;
  
  let filteredServices = services;
  if (categoryId) {
    filteredServices = services.filter(s => s.category.id === categoryId);
  }
  
  const start = page * size;
  const end = start + size;
  const content = filteredServices.slice(start, end);
  
  res.json({
    content: content,
    totalElements: filteredServices.length
  });
});

app.get('/api/service-categories', (req, res) => {
  res.json(serviceCategories);
});

// Provider management
app.get('/api/providers', (req, res) => {
  const page = parseInt(req.query.page) || 0;
  const size = parseInt(req.query.size) || 20;
  const search = req.query.search;
  
  let filteredProviders = providers;
  if (search) {
    filteredProviders = providers.filter(p => 
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.email.toLowerCase().includes(search.toLowerCase())
    );
  }
  
  const start = page * size;
  const end = start + size;
  const content = filteredProviders.slice(start, end);
  
  res.json({
    content: content,
    totalElements: filteredProviders.length
  });
});

app.get('/api/providers/profile', (req, res) => {
  // Return first provider as mock profile
  res.json(providers[0]);
});

// Booking management
app.get('/api/bookings', (req, res) => {
  const page = parseInt(req.query.page) || 0;
  const size = parseInt(req.query.size) || 20;
  const status = req.query.status;
  
  let filteredBookings = bookings;
  if (status) {
    filteredBookings = bookings.filter(b => b.status === status);
  }
  
  const start = page * size;
  const end = start + size;
  const content = filteredBookings.slice(start, end);
  
  res.json({
    content: content,
    totalElements: filteredBookings.length
  });
});

app.get('/api/providers/bookings', (req, res) => {
  // Return bookings for provider
  res.json({
    content: bookings,
    totalElements: bookings.length
  });
});

// Analytics
app.get('/api/admin/stats', (req, res) => {
  res.json({
    totalUsers: users.length,
    totalProviders: providers.length,
    totalBookings: bookings.length,
    activeBookings: bookings.filter(b => b.status === 'CONFIRMED').length,
    totalRevenue: payments.reduce((sum, p) => sum + p.amount, 0),
    usersChange: 12.5,
    providersChange: 8.3,
    bookingsChange: 15.7,
    revenueChange: 22.1,
    usersTrend: 'up',
    providersTrend: 'up',
    bookingsTrend: 'up',
    revenueTrend: 'up'
  });
});

app.get('/api/providers/stats', (req, res) => {
  res.json({
    todayBookings: 3,
    activeBookings: 2,
    totalEarnings: 1500,
    rating: 4.8,
    todayBookingsChange: 20,
    activeBookingsChange: 10,
    earningsChange: 15,
    ratingChange: 0.2
  });
});

app.get('/api/admin/health', (req, res) => {
  res.json({
    database: 'healthy',
    redis: 'healthy',
    email: 'healthy',
    overall: 'healthy',
    lastChecked: new Date().toISOString()
  });
});

// Payments
app.get('/api/payments', (req, res) => {
  const page = parseInt(req.query.page) || 0;
  const size = parseInt(req.query.size) || 20;
  
  const start = page * size;
  const end = start + size;
  const content = payments.slice(start, end);
  
  res.json({
    content: content,
    totalElements: payments.length
  });
});

// Reviews
app.get('/api/reviews', (req, res) => {
  const page = parseInt(req.query.page) || 0;
  const size = parseInt(req.query.size) || 20;
  
  const start = page * size;
  const end = start + size;
  const content = reviews.slice(start, end);
  
  res.json({
    content: content,
    totalElements: reviews.length
  });
});

app.get('/api/providers/reviews', (req, res) => {
  res.json({
    content: reviews,
    totalElements: reviews.length
  });
});

// Start server
app.listen(port, () => {
  console.log(`Mock backend server running at http://localhost:${port}`);
  console.log('Available endpoints:');
  console.log('- POST /api/auth/login (email: any user email, password: password123)');
  console.log('- GET /api/users');
  console.log('- GET /api/services');
  console.log('- GET /api/providers');
  console.log('- GET /api/bookings');
  console.log('- GET /api/admin/stats');
  console.log('- GET /api/admin/health');
});