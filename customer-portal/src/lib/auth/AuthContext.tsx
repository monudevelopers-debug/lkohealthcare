import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import type { User, AuthState, RegisterRequest } from '../../types/auth.types';
import apiClient from '../api/client';

interface AuthContextType extends AuthState {
  login: (email: string, password: string) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
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
        localStorage.removeItem('lhc_auth');
        setState((prev) => ({ ...prev, isLoading: false }));
      }
    };

    loadAuth();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const response = await apiClient.post('/auth/login', { email, password });
      const { token, user, expiresIn } = response.data;
      const expiresAt = Date.now() + expiresIn;

      const authData = { token, user, expiresAt };
      localStorage.setItem('lhc_auth', JSON.stringify(authData));

      setState({
        user,
        token,
        expiresAt,
        isAuthenticated: true,
        isLoading: false,
      });
    } catch (error) {
      throw error;
    }
  };

  const register = async (userData: RegisterRequest) => {
    try {
      await apiClient.post('/auth/register', userData);
      // Registration successful, user needs to login
    } catch (error) {
      throw error;
    }
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
