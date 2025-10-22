import { Dialog } from '@headlessui/react';
import { XMarkIcon, UserIcon, EnvelopeIcon, PhoneIcon, LockClosedIcon, ShieldCheckIcon } from '@heroicons/react/24/outline';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../lib/auth/AuthContext';
import { Button } from '../ui/Button';
import { Input } from '../ui/Input';
import apiClient from '../../lib/api/client';
import { Notification, useNotification } from '../ui/Notification';

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
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { register, login } = useAuth();
  const navigate = useNavigate();
  const { notification, showSuccess, showError, hideNotification } = useNotification();

  const handleSendOTP = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setError('');
    setIsLoading(true);

    try {
      const response = await apiClient.post('/auth/send-otp', {
        phone: formData.phone
      });
      
      console.log('✅ OTP sent successfully:', response.data);
      
      // Show professional success notification
      showSuccess(
        'OTP Sent Successfully',
        `A 6-digit OTP has been sent to ${formData.phone}. Please check your phone and enter the code below.`
      );
      
      setStep('otp');
    } catch (err: any) {
      console.error('❌ OTP Send Error:', err);
      const errorMessage = err.response?.data?.error || 'Failed to send OTP';
      
      // Show professional error notification
      showError(
        'OTP Send Failed',
        errorMessage.includes('Failed to send OTP') 
          ? 'Unable to send OTP. Please try again or contact support if the issue persists.'
          : errorMessage
      );
      
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerifyOTP = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const verifyResponse = await apiClient.post('/auth/verify-otp', {
        phone: formData.phone,
        otp: otp
      });

      if (verifyResponse.data.verified) {
        await register({
          name: formData.name,
          email: formData.email,
          phone: formData.phone,
          password: formData.password,
          role: 'CUSTOMER',
        });

        // Auto-login after successful registration
        await login(formData.email, formData.password);
        
        // Show success notification
        showSuccess(
          'Registration Successful',
          'Your account has been created successfully. Welcome to Lucknow Healthcare!'
        );
        
        handleClose();
        
        // Redirect to dashboard
        navigate('/dashboard');
      } else {
        const errorMsg = verifyResponse.data.error || 'OTP verification failed';
        showError('OTP Verification Failed', errorMsg);
        setError(errorMsg);
      }
    } catch (err: any) {
      console.error('Verification Error:', err);
      const errorMsg = err.response?.data?.error || 'OTP verification failed. Please try again.';
      showError('Verification Error', errorMsg);
      setError(errorMsg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    setStep('form');
    setFormData({
      name: '',
      email: '',
      phone: '',
      password: '',
      confirmPassword: '',
    });
    setOtp('');
    setError('');
    onClose();
  };

  return (
    <Dialog open={isOpen} onClose={handleClose} className="relative z-50">
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/40 backdrop-blur-sm" aria-hidden="true" />
      
      <div className="fixed inset-0 flex items-center justify-center p-4 overflow-y-auto">
        <Dialog.Panel className="mx-auto max-w-md w-full bg-white rounded-3xl shadow-2xl p-6 animate-scale-in border border-blue-100 max-h-[90vh] overflow-y-auto">
          {/* Header */}
          <div className="flex justify-between items-center mb-6">
            <div>
              <div className="flex items-center space-x-3 mb-4">
                <div className="w-12 h-12 bg-gradient-to-br from-blue-600 to-slate-800 rounded-xl flex items-center justify-center">
                  <span className="text-white font-bold text-xl">L</span>
                </div>
                <div>
                  <h3 className="text-lg font-bold text-gray-900">Lucknow Healthcare</h3>
                  <p className="text-xs text-gray-500">Professional Care at Home</p>
                </div>
              </div>
              <Dialog.Title className="text-2xl font-bold text-gray-900">
                {step === 'form' ? 'Create Account ✨' : 'Verify Your Phone 📱'}
              </Dialog.Title>
              <p className="text-gray-600 mt-1">
                {step === 'form' ? 'Join our healthcare community' : 'Enter the OTP we sent to your phone'}
              </p>
            </div>
            <button 
              onClick={handleClose} 
              className="text-gray-400 hover:text-gray-600 hover:bg-gray-100 rounded-xl p-2 transition-all"
            >
              <XMarkIcon className="w-6 h-6" />
            </button>
          </div>

          {error && (
            <div className="mb-6 p-4 bg-red-50 border-2 border-red-200 rounded-xl text-red-700 text-sm flex items-start animate-shake">
              <svg className="w-5 h-5 mr-2 flex-shrink-0 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
              </svg>
              <div>
                <strong>Oops!</strong> {error}
              </div>
            </div>
          )}

          {step === 'form' ? (
            <form onSubmit={handleSendOTP} className="space-y-4">
              <Input
                type="text"
                label="Full Name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                placeholder="John Doe"
                icon={<UserIcon className="w-5 h-5" />}
                required
              />

              <Input
                type="email"
                label="Email Address"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="your@email.com"
                icon={<EnvelopeIcon className="w-5 h-5" />}
                required
              />

              <Input
                type="tel"
                label="Phone Number"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="+91 84000-01034"
                icon={<PhoneIcon className="w-5 h-5" />}
                helperText="We'll send an OTP to verify your number"
                required
              />

              <Input
                type="password"
                label="Password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder="Create a strong password"
                icon={<LockClosedIcon className="w-5 h-5" />}
                helperText="Minimum 6 characters"
                required
              />

              <Input
                type="password"
                label="Confirm Password"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                placeholder="Re-enter your password"
                icon={<LockClosedIcon className="w-5 h-5" />}
                required
              />

              <Button
                type="submit"
                fullWidth
                size="lg"
                isLoading={isLoading}
                className="bg-gradient-to-r from-blue-600 to-slate-700 hover:from-blue-700 hover:to-slate-800 text-white font-semibold hover:scale-105 transform transition-all duration-300"
              >
                {isLoading ? 'Sending OTP...' : '✨ Send OTP'}
              </Button>

              <p className="text-xs text-gray-500 text-center">
                By continuing, you agree to our Terms of Service and Privacy Policy
              </p>
            </form>
          ) : (
            <form onSubmit={handleVerifyOTP} className="space-y-4">
              <div className="text-center mb-4">
                <div className="w-20 h-20 mx-auto mb-4 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center animate-pulse-slow shadow-xl">
                  <ShieldCheckIcon className="w-10 h-10 text-white" />
                </div>
                <p className="text-gray-600 leading-relaxed">
                  We sent a 6-digit OTP to<br />
                  <span className="font-bold text-gray-900">{formData.phone}</span>
                </p>
              </div>

              <Input
                type="text"
                label="Enter OTP"
                value={otp}
                onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="••••••"
                maxLength={6}
                className="text-center text-3xl tracking-widest font-bold"
                required
              />

              <Button
                type="submit"
                fullWidth
                size="lg"
                isLoading={isLoading}
                className="bg-gradient-to-r from-blue-600 to-slate-700 hover:from-blue-700 hover:to-slate-800 text-white font-semibold hover:scale-105 transform transition-all duration-300"
              >
                {isLoading ? 'Verifying...' : '✓ Verify & Create Account'}
              </Button>

              <Button
                type="button"
                variant="ghost"
                fullWidth
                size="sm"
                onClick={() => setStep('form')}
              >
                ← Back to form
              </Button>

              <button
                type="button"
                onClick={handleSendOTP}
                className="w-full text-sm text-blue-600 hover:text-blue-700 font-semibold transition-colors"
                disabled={isLoading}
              >
                Resend OTP
              </button>
            </form>
          )}

          {step === 'form' && (
            <div className="mt-6 text-center">
              <p className="text-gray-600">
                Already have an account?{' '}
                <button
                  onClick={onSwitchToLogin}
                  className="font-bold bg-gradient-to-r from-blue-600 to-slate-700 bg-clip-text text-transparent hover:from-blue-700 hover:to-slate-800 transition-all"
                >
                  Sign In →
                </button>
              </p>
            </div>
          )}
        </Dialog.Panel>
      </div>
      
      {/* Professional Notification */}
      <Notification
        type={notification.type}
        title={notification.title}
        message={notification.message}
        isVisible={notification.isVisible}
        onClose={hideNotification}
      />
    </Dialog>
  );
};