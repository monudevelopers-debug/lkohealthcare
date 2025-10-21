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
      try {
        const { token } = JSON.parse(auth);
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      } catch (error) {
        console.error('Failed to parse auth token:', error);
        localStorage.removeItem('lhc_auth');
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
      
      console.log('401 Unauthorized - API:', originalRequest.url);
      console.log('401 Error Response:', error.response?.data);
      
      // Try to refresh token
      try {
        const auth = localStorage.getItem('lhc_auth');
        if (!auth) {
          console.log('No auth found in localStorage, redirecting to login');
          localStorage.removeItem('lhc_auth');
          window.location.href = '/';
          return Promise.reject(error);
        }
        
        const { token } = JSON.parse(auth);
        if (!token) {
          console.log('No token found, redirecting to login');
          localStorage.removeItem('lhc_auth');
          window.location.href = '/';
          return Promise.reject(error);
        }
        
        console.log('Attempting to refresh token...');
        const response = await axios.post(
          `${import.meta.env.VITE_API_URL}/auth/refresh-token`,
          {},
          { headers: { Authorization: `Bearer ${token}` } }
        );
        
        const { token: newToken, user, expiresIn } = response.data;
        const expiresAt = Date.now() + expiresIn;
        
        localStorage.setItem('lhc_auth', JSON.stringify({ token: newToken, user, expiresAt }));
        console.log('Token refreshed successfully');
        
        // Retry original request with new token
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return apiClient(originalRequest);
      } catch (refreshError) {
        // Refresh failed, logout user
        console.error('Token refresh failed, logging out:', refreshError);
        localStorage.removeItem('lhc_auth');
        window.location.href = '/';
        return Promise.reject(refreshError);
      }
    }

    // If it's a 401 and already retried, logout
    if (error.response?.status === 401 && originalRequest._retry) {
      console.log('Token refresh already attempted, logging out');
      localStorage.removeItem('lhc_auth');
      window.location.href = '/';
    }

    return Promise.reject(error);
  }
);

export default apiClient;
