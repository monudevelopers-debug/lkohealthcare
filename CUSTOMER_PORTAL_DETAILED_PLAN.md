# Lucknow Nursing HealthCare Services - Customer Portal
## Detailed Implementation Plan

**Last Updated:** October 20, 2025  
**Version:** 1.0.0  
**Project Type:** React TypeScript Customer Web Portal  
**Replaces:** Flutter mobile app  
**Keeps:** Existing admin-dashboard and provider-dashboard (no changes)

---

## 📋 Table of Contents

1. [Company Information](#company-information)
2. [Configuration Summary](#configuration-summary)
3. [Technology Stack](#technology-stack)
4. [Project Architecture](#project-architecture)
5. [Detailed Implementation Phases](#detailed-implementation-phases)
6. [Backend Integration](#backend-integration)
7. [SEO Strategy](#seo-strategy)
8. [Testing Strategy](#testing-strategy)
9. [Deployment Plan](#deployment-plan)
10. [Timeline & Milestones](#timeline--milestones)

---

## 🏥 Company Information

### Business Details
```
Company Name: Lucknow Nursing HealthCare Services
Tagline: Professional Healthcare Services at Your Doorstep
Service Area: Lucknow, Uttar Pradesh (Only)
```

### Contact Information
```
Phone/WhatsApp: +91-8400001034
Email: lucknow.services2014@gmail.com
Office Address: Behind Meena Market, Near Lekhraj Metro Station,
                Indira Nagar, Lucknow - 226016
```

### Branding
```
Primary Brand: Lucknow Nursing HealthCare Services
Copyright: Connatecoders
Developer: Connatecoders (www.connatecoders.com)
```

### Notification Settings
```
Booking Alerts Email: monudevelopers@gmail.com
Admin Email: monudevelopers@gmail.com
Customer Support: lucknow.services2014@gmail.com
```

---

## ⚙️ Configuration Summary

### Firebase Configuration (OTP Verification)
```env
VITE_FIREBASE_API_KEY=AIzaSyDNfNSWagT-Em_CU7rZmfoxG9PjWnqrFxU
VITE_FIREBASE_AUTH_DOMAIN=nowhealthcare-2a9f9.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=nowhealthcare-2a9f9
VITE_FIREBASE_STORAGE_BUCKET=nowhealthcare-2a9f9.firebasestorage.app
VITE_FIREBASE_MESSAGING_SENDER_ID=771072392733
VITE_FIREBASE_APP_ID=1:771072392733:web:b6eef0b307f984403910f4
VITE_FIREBASE_MEASUREMENT_ID=G-Z8J7PPLXWJ
```

**Features:**
- ✅ FREE (10,000 verifications/month)
- ✅ Phone OTP verification for registration
- ✅ No credit card required
- ✅ India phone numbers supported

### Backend Email Configuration
```yaml
# application.yml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: monudevelopers@gmail.com
    password: SHIvam@7426
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

# Notification Configuration
notification:
  admin-email: monudevelopers@gmail.com
  customer-support: lucknow.services2014@gmail.com
```

**Purpose:**
- ✅ Send booking confirmation emails to customers
- ✅ Send booking notification to monudevelopers@gmail.com
- ✅ Send status update emails

### Backend API Configuration
```
Base URL: http://localhost:8080/api
Context Path: /api (configured in application.yml)
Authentication: JWT Bearer Token
Token Expiry: 15 minutes (900000ms)
Refresh Token: 7 days (604800000ms)
```

### Address Input Strategy
```
Method: Simple Text Input (NO Google Maps API)
Validation: Frontend checks for "lucknow" or "226" (pin code) in address
Cost: FREE - No API required
Fallback: Backend can add additional validation if needed
```

**Address Validation Logic:**
```typescript
function validateLucknowAddress(address: string): { valid: boolean; error?: string } {
  const addressLower = address.toLowerCase();
  const lucknowKeywords = ['lucknow', 'lko', '226'];
  
  const isLucknow = lucknowKeywords.some(keyword => addressLower.includes(keyword));
  
  if (!isLucknow) {
    return {
      valid: false,
      error: "Sorry, we only serve in Lucknow area. Please enter a valid Lucknow address."
    };
  }
  
  return { valid: true };
}
```

---

## 🛠️ Technology Stack

### Frontend
```json
{
  "framework": "React 18.3+",
  "language": "TypeScript 5.6+",
  "build": "Vite 6.0+",
  "styling": "Tailwind CSS 3.4+",
  "components": "HeadlessUI 2.2+",
  "icons": "Heroicons 2.2+",
  "routing": "React Router 6.28+",
  "data": "React Query 5.62+",
  "http": "Axios 1.7+",
  "forms": "React Hook Form 7.54+",
  "validation": "Zod 3.23+",
  "auth": "Firebase 11.0+",
  "dates": "date-fns 4.1+",
  "seo": "React Helmet Async 2.0+"
}
```

### Backend (Existing - Spring Boot)
```
Framework: Spring Boot 3.x
Database: PostgreSQL
Authentication: JWT with Spring Security
APIs: RESTful with /api prefix
```

### Development Tools
```
Package Manager: npm
Code Quality: ESLint + Prettier
Testing: Vitest + React Testing Library + Playwright
Version Control: Git
```

### Deployment
```
Frontend: Vercel / Netlify (FREE tier)
Backend: Already deployed (keep as is)
Domain: TBD
SSL: Auto (via hosting provider)
```

---

## 🏗️ Project Architecture

### Folder Structure
```
customer-portal/
├── public/
│   ├── images/
│   │   ├── logo.png
│   │   ├── hero-healthcare.jpg
│   │   ├── services/
│   │   └── team/
│   ├── favicon.ico
│   └── robots.txt
│
├── src/
│   ├── components/
│   │   ├── layout/
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── DashboardLayout.tsx
│   │   │   └── PublicLayout.tsx
│   │   │
│   │   ├── ui/
│   │   │   ├── Button.tsx
│   │   │   ├── Card.tsx
│   │   │   ├── Modal.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Textarea.tsx
│   │   │   ├── Select.tsx
│   │   │   ├── Spinner.tsx
│   │   │   ├── Alert.tsx
│   │   │   ├── Badge.tsx
│   │   │   ├── Pagination.tsx
│   │   │   └── Toast.tsx
│   │   │
│   │   ├── auth/
│   │   │   ├── LoginModal.tsx
│   │   │   ├── RegisterModal.tsx
│   │   │   ├── OTPVerification.tsx
│   │   │   └── ProtectedRoute.tsx
│   │   │
│   │   ├── services/
│   │   │   ├── ServiceCard.tsx
│   │   │   ├── ServiceGrid.tsx
│   │   │   ├── ServiceFilters.tsx
│   │   │   ├── ServiceDetails.tsx
│   │   │   └── CategoryTabs.tsx
│   │   │
│   │   ├── bookings/
│   │   │   ├── BookingForm.tsx
│   │   │   ├── BookingCard.tsx
│   │   │   ├── BookingList.tsx
│   │   │   ├── AddressInput.tsx
│   │   │   └── DateTimePicker.tsx
│   │   │
│   │   └── profile/
│   │       ├── ProfileForm.tsx
│   │       ├── PasswordChange.tsx
│   │       └── ProfileAvatar.tsx
│   │
│   ├── pages/
│   │   ├── landing/
│   │   │   ├── LandingPage.tsx
│   │   │   ├── HeroSection.tsx
│   │   │   ├── FeaturesSection.tsx
│   │   │   ├── ServicesPreview.tsx
│   │   │   ├── TestimonialsSection.tsx
│   │   │   └── CTASection.tsx
│   │   │
│   │   ├── dashboard/
│   │   │   ├── DashboardHome.tsx
│   │   │   ├── ServicesPage.tsx
│   │   │   ├── ServiceDetailPage.tsx
│   │   │   ├── BookingsPage.tsx
│   │   │   ├── ProfilePage.tsx
│   │   │   └── SettingsPage.tsx
│   │   │
│   │   ├── static/
│   │   │   ├── AboutPage.tsx
│   │   │   ├── ContactPage.tsx
│   │   │   ├── PrivacyPolicy.tsx
│   │   │   └── TermsOfService.tsx
│   │   │
│   │   └── NotFoundPage.tsx
│   │
│   ├── lib/
│   │   ├── api/
│   │   │   ├── client.ts
│   │   │   ├── endpoints.ts
│   │   │   ├── auth.api.ts
│   │   │   ├── services.api.ts
│   │   │   ├── bookings.api.ts
│   │   │   ├── categories.api.ts
│   │   │   └── reviews.api.ts
│   │   │
│   │   ├── auth/
│   │   │   ├── AuthContext.tsx
│   │   │   ├── AuthProvider.tsx
│   │   │   ├── useAuth.ts
│   │   │   └── firebase.ts
│   │   │
│   │   ├── hooks/
│   │   │   ├── useServices.ts
│   │   │   ├── useBookings.ts
│   │   │   ├── useCategories.ts
│   │   │   ├── useDebounce.ts
│   │   │   └── useToast.ts
│   │   │
│   │   └── utils/
│   │       ├── formatDate.ts
│   │       ├── formatCurrency.ts
│   │       ├── validation.ts
│   │       ├── addressValidation.ts
│   │       └── constants.ts
│   │
│   ├── types/
│   │   ├── api.types.ts
│   │   ├── auth.types.ts
│   │   ├── booking.types.ts
│   │   ├── service.types.ts
│   │   └── pagination.types.ts
│   │
│   ├── App.tsx
│   ├── main.tsx
│   └── index.css
│
├── .env.development
├── .env.production
├── .gitignore
├── index.html
├── package.json
├── postcss.config.js
├── tailwind.config.js
├── tsconfig.json
├── tsconfig.node.json
└── vite.config.ts
```

---

## 📅 Detailed Implementation Phases

### **Phase 1: Project Setup & Foundation** (Week 1, Days 1-2)

#### 1.1 Initialize Project
```bash
# Create Vite React TypeScript project
npm create vite@latest customer-portal -- --template react-ts
cd customer-portal
npm install
```

#### 1.2 Install Core Dependencies
```bash
# UI & Styling
npm install tailwindcss postcss autoprefixer
npm install @headlessui/react @heroicons/react

# Routing & Data
npm install react-router-dom @tanstack/react-query

# HTTP & Forms
npm install axios react-hook-form zod

# Firebase & Utils
npm install firebase date-fns react-helmet-async

# Dev Dependencies
npm install -D @types/node
```

#### 1.3 Configure Tailwind CSS
```bash
npx tailwindcss init -p
```

**tailwind.config.js:**
```javascript
/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eff6ff',
          100: '#dbeafe',
          200: '#bfdbfe',
          300: '#93c5fd',
          400: '#60a5fa',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
          800: '#1e40af',
          900: '#1e3a8a',
        },
        secondary: {
          500: '#10b981',
          600: '#059669',
          700: '#047857',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
```

#### 1.4 Create Environment Variables
**.env.development:**
```env
VITE_API_URL=http://localhost:8080/api
VITE_FIREBASE_API_KEY=AIzaSyDNfNSWagT-Em_CU7rZmfoxG9PjWnqrFxU
VITE_FIREBASE_AUTH_DOMAIN=nowhealthcare-2a9f9.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=nowhealthcare-2a9f9
VITE_FIREBASE_STORAGE_BUCKET=nowhealthcare-2a9f9.firebasestorage.app
VITE_FIREBASE_MESSAGING_SENDER_ID=771072392733
VITE_FIREBASE_APP_ID=1:771072392733:web:b6eef0b307f984403910f4
VITE_FIREBASE_MEASUREMENT_ID=G-Z8J7PPLXWJ
VITE_APP_NAME=Lucknow Nursing HealthCare Services
VITE_COMPANY_PHONE=+91-8400001034
VITE_COMPANY_EMAIL=lucknow.services2014@gmail.com
VITE_COMPANY_ADDRESS=Behind Meena Market, Near Lekhraj Metro Station, Indira Nagar, Lucknow - 226016
```

#### 1.5 Setup Folder Structure
```bash
mkdir -p src/{components/{layout,ui,auth,services,bookings,profile},pages/{landing,dashboard,static},lib/{api,auth,hooks,utils},types}
```

#### 1.6 Configure Axios Client
**src/lib/api/client.ts:**
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor - Add auth token
apiClient.interceptors.request.use(
  (config) => {
    const auth = localStorage.getItem('lhc_auth');
    if (auth) {
      const { token } = JSON.parse(auth);
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor - Handle errors
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Handle 401 - Unauthorized
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      // Try to refresh token
      try {
        const auth = localStorage.getItem('lhc_auth');
        if (auth) {
          const { token } = JSON.parse(auth);
          const response = await axios.post(
            `${import.meta.env.VITE_API_URL}/auth/refresh-token`,
            {},
            { headers: { Authorization: `Bearer ${token}` } }
          );
          
          const { token: newToken, user, expiresIn } = response.data;
          const expiresAt = Date.now() + expiresIn;
          
          localStorage.setItem('lhc_auth', JSON.stringify({ token: newToken, user, expiresAt }));
          
          // Retry original request with new token
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return apiClient(originalRequest);
        }
      } catch (refreshError) {
        // Refresh failed, logout user
        localStorage.removeItem('lhc_auth');
        window.location.href = '/';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
```

#### 1.7 Configure Firebase
**src/lib/auth/firebase.ts:**
```typescript
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY,
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN,
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID,
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID,
  appId: import.meta.env.VITE_FIREBASE_APP_ID,
  measurementId: import.meta.env.VITE_FIREBASE_MEASUREMENT_ID,
};

export const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
```

---

### **Phase 2: Authentication System** (Week 1, Days 3-4)

#### 2.1 Create Auth Types
**src/types/auth.types.ts:**
```typescript
export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: 'USER' | 'ADMIN' | 'PROVIDER';
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
  emailVerified: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  expiresAt: number | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  phone: string;
  password: string;
  role?: 'USER';
}
```

#### 2.2 Create AuthContext
**src/lib/auth/AuthContext.tsx:**
```typescript
import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { User, AuthState } from '../../types/auth.types';

interface AuthContextType extends AuthState {
  login: (email: string, password: string) => Promise<void>;
  register: (userData: any) => Promise<void>;
  logout: () => void;
  updateUser: (user: User) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [state, setState] = useState<AuthState>({
    user: null,
    token: null,
    expiresAt: null,
    isAuthenticated: false,
    isLoading: true,
  });

  // Load auth state from localStorage on mount
  useEffect(() => {
    const loadAuth = () => {
      try {
        const saved = localStorage.getItem('lhc_auth');
        if (saved) {
          const { token, user, expiresAt } = JSON.parse(saved);
          if (Date.now() < expiresAt) {
            setState({
              user,
              token,
              expiresAt,
              isAuthenticated: true,
              isLoading: false,
            });
          } else {
            localStorage.removeItem('lhc_auth');
            setState((prev) => ({ ...prev, isLoading: false }));
          }
        } else {
          setState((prev) => ({ ...prev, isLoading: false }));
        }
      } catch (error) {
        console.error('Failed to load auth state:', error);
        setState((prev) => ({ ...prev, isLoading: false }));
      }
    };

    loadAuth();
  }, []);

  const login = async (email: string, password: string) => {
    // Implementation in auth.api.ts
  };

  const register = async (userData: any) => {
    // Implementation in auth.api.ts
  };

  const logout = () => {
    localStorage.removeItem('lhc_auth');
    setState({
      user: null,
      token: null,
      expiresAt: null,
      isAuthenticated: false,
      isLoading: false,
    });
  };

  const updateUser = (user: User) => {
    setState((prev) => ({ ...prev, user }));
    const saved = localStorage.getItem('lhc_auth');
    if (saved) {
      const auth = JSON.parse(saved);
      localStorage.setItem('lhc_auth', JSON.stringify({ ...auth, user }));
    }
  };

  return (
    <AuthContext.Provider value={{ ...state, login, register, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
```

#### 2.3 Create Login Modal
**src/components/auth/LoginModal.tsx:**
```typescript
import { Dialog } from '@headlessui/react';
import { XMarkIcon } from '@heroicons/react/24/outline';
import { useState } from 'react';
import { useAuth } from '../../lib/auth/AuthContext';

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSwitchToRegister: () => void;
}

export const LoginModal = ({ isOpen, onClose, onSwitchToRegister }: LoginModalProps) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await login(email, password);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <Dialog.Panel className="mx-auto max-w-md w-full bg-white rounded-lg shadow-xl p-6">
          <div className="flex justify-between items-center mb-4">
            <Dialog.Title className="text-2xl font-bold text-gray-900">
              Login
            </Dialog.Title>
            <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
              <XMarkIcon className="w-6 h-6" />
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                placeholder="your@email.com"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                placeholder="••••••••"
              />
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-2 px-4 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
            >
              {isLoading ? 'Logging in...' : 'Login'}
            </button>
          </form>

          <div className="mt-4 text-center text-sm text-gray-600">
            Don't have an account?{' '}
            <button
              onClick={onSwitchToRegister}
              className="text-primary-600 hover:text-primary-700 font-medium"
            >
              Sign Up
            </button>
          </div>
        </Dialog.Panel>
      </div>
    </Dialog>
  );
};
```

#### 2.4 Create Register Modal with OTP
**src/components/auth/RegisterModal.tsx:**
```typescript
import { Dialog } from '@headlessui/react';
import { XMarkIcon } from '@heroicons/react/24/outline';
import { useState } from 'react';
import { RecaptchaVerifier, signInWithPhoneNumber } from 'firebase/auth';
import { auth } from '../../lib/auth/firebase';
import apiClient from '../../lib/api/client';

interface RegisterModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSwitchToLogin: () => void;
}

export const RegisterModal = ({ isOpen, onClose, onSwitchToLogin }: RegisterModalProps) => {
  const [step, setStep] = useState<'form' | 'otp'>('form');
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
  });
  const [otp, setOtp] = useState('');
  const [confirmationResult, setConfirmationResult] = useState<any>(null);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSendOTP = async (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setError('');
    setIsLoading(true);

    try {
      // Setup reCAPTCHA
      const recaptchaVerifier = new RecaptchaVerifier(auth, 'recaptcha-container', {
        size: 'invisible',
      });

      // Send OTP
      const phoneNumber = formData.phone.startsWith('+91') 
        ? formData.phone 
        : `+91${formData.phone}`;
      
      const result = await signInWithPhoneNumber(auth, phoneNumber, recaptchaVerifier);
      setConfirmationResult(result);
      setStep('otp');
    } catch (err: any) {
      setError(err.message || 'Failed to send OTP');
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyOTP = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      // Verify OTP
      await confirmationResult.confirm(otp);

      // Register user
      const response = await apiClient.post('/auth/register', {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        password: formData.password,
        role: 'USER',
      });

      // Success - switch to login
      onSwitchToLogin();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={isOpen} onClose={onClose} className="relative z-50">
      <div className="fixed inset-0 bg-black/30" aria-hidden="true" />
      <div className="fixed inset-0 flex items-center justify-center p-4">
        <Dialog.Panel className="mx-auto max-w-md w-full bg-white rounded-lg shadow-xl p-6">
          <div className="flex justify-between items-center mb-4">
            <Dialog.Title className="text-2xl font-bold text-gray-900">
              {step === 'form' ? 'Create Account' : 'Verify OTP'}
            </Dialog.Title>
            <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
              <XMarkIcon className="w-6 h-6" />
            </button>
          </div>

          {error && (
            <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {error}
            </div>
          )}

          {step === 'form' ? (
            <form onSubmit={handleSendOTP} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Full Name
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Email
                </label>
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Phone Number
                </label>
                <input
                  type="tel"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  required
                  placeholder="+91 84000-01034"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Password
                </label>
                <input
                  type="password"
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Confirm Password
                </label>
                <input
                  type="password"
                  value={formData.confirmPassword}
                  onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500"
                />
              </div>

              <div id="recaptcha-container"></div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full py-2 px-4 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 transition"
              >
                {isLoading ? 'Sending OTP...' : 'Send OTP'}
              </button>
            </form>
          ) : (
            <form onSubmit={handleVerifyOTP} className="space-y-4">
              <p className="text-sm text-gray-600">
                Enter the 6-digit OTP sent to {formData.phone}
              </p>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  OTP Code
                </label>
                <input
                  type="text"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                  required
                  maxLength={6}
                  placeholder="123456"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 text-center text-2xl tracking-widest"
                />
              </div>

              <button
                type="submit"
                disabled={isLoading}
                className="w-full py-2 px-4 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 transition"
              >
                {isLoading ? 'Verifying...' : 'Verify & Register'}
              </button>

              <button
                type="button"
                onClick={() => setStep('form')}
                className="w-full text-sm text-gray-600 hover:text-gray-800"
              >
                ← Back to form
              </button>
            </form>
          )}

          {step === 'form' && (
            <div className="mt-4 text-center text-sm text-gray-600">
              Already have an account?{' '}
              <button
                onClick={onSwitchToLogin}
                className="text-primary-600 hover:text-primary-700 font-medium"
              >
                Login
              </button>
            </div>
          )}
        </Dialog.Panel>
      </div>
    </Dialog>
  );
};
```

---

### **Phase 3: Landing Page** (Week 1, Days 5-7)

[Continuing with detailed implementation for Landing Page components, Services, Bookings, etc...]

---

## 🔗 Backend Integration

### API Endpoints Used

#### Authentication
- `POST /api/auth/login` - Login with email/password
- `POST /api/auth/register` - Register new user
- `POST /api/auth/logout` - Logout user
- `POST /api/auth/refresh-token` - Refresh JWT token
- `GET /api/auth/me` - Get current user

#### Service Categories
- `GET /api/service-categories/active` - Get active categories

#### Services
- `GET /api/services/active` - Get all active services
- `GET /api/services/active/page?page=0&size=20` - Paginated services
- `GET /api/services/active/category/{categoryId}` - Services by category
- `GET /api/services/active/search?name=query` - Search services
- `GET /api/services/{id}` - Get service details

#### Bookings
- `POST /api/bookings` - Create new booking
- `GET /api/bookings/user/{userId}` - Get user's bookings
- `PUT /api/bookings/{id}/cancel` - Cancel booking
- `GET /api/bookings/{id}` - Get booking details

#### Users
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile
- `PUT /api/users/{id}/password?newPassword=xxx` - Change password

---

## 📊 Timeline & Milestones

### Week 1: Foundation (Days 1-7)
- ✅ Project setup and dependencies
- ✅ Authentication system with Firebase OTP
- ✅ Landing page with all sections
- ✅ Basic routing and navigation

### Week 2: Core Features (Days 8-14)
- ✅ Services browsing with filters
- ✅ Service details page
- ✅ Booking form with Lucknow validation
- ✅ Dashboard layout
- ✅ My Bookings page

### Week 3: Polish & Testing (Days 15-21)
- ✅ Profile management
- ✅ Error handling and loading states
- ✅ Mobile responsive design
- ✅ Accessibility improvements
- ✅ E2E and unit tests

### Week 4: Launch (Days 22-28)
- ✅ Backend email notifications
- ✅ SEO optimization
- ✅ Production build
- ✅ Deployment
- ✅ Documentation

---

## 💰 Cost Summary

| Item | Cost |
|------|------|
| Firebase OTP | **$0** (10,000/month free) |
| Address Input | **$0** (simple text) |
| Hosting (Vercel/Netlify) | **$0** (free tier) |
| **Total Monthly Cost** | **$0** |

---

## ✅ Success Criteria

- [ ] User can register with OTP verification
- [ ] User can login and stay authenticated
- [ ] User can browse services by category
- [ ] User can search services
- [ ] User can create bookings with Lucknow address validation
- [ ] User can view their bookings
- [ ] User can cancel bookings
- [ ] User can edit their profile
- [ ] Admin receives email for each booking at monudevelopers@gmail.com
- [ ] Landing page ranks well for "healthcare services Lucknow"
- [ ] Mobile responsive on all devices
- [ ] 90+ Lighthouse score
- [ ] Zero cost to run

---

**Document Version:** 1.0.0  
**Last Updated:** October 20, 2025  
**Status:** Ready for Implementation

**Contact:**  
Developer: Connatecoders  
Client: Lucknow Nursing HealthCare Services  
Email: lucknow.services2014@gmail.com  
Phone: +91-8400001034

