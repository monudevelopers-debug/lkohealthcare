import React from 'react';
import { Button } from '../ui/Button';
import { 
  HeartIcon, 
  UserGroupIcon,
  ShieldCheckIcon,
  StarIcon
} from '@heroicons/react/24/solid';

interface HeroProps {
  onSignUpClick: () => void;
  onLoginClick: () => void;
}

export const Hero: React.FC<HeroProps> = ({ onSignUpClick, onLoginClick }) => {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-slate-800 via-blue-900 to-slate-900 text-white py-20 md:py-32 animate-fade-in">
      {/* Background Pattern */}
      <div className="absolute inset-0 bg-black/5">
        <div className="absolute inset-0 bg-gradient-to-r from-blue-700/10 to-slate-700/10"></div>
      </div>
      
      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center">
          {/* Main Headline */}
          <h1 className="text-4xl md:text-6xl font-bold mb-6 leading-tight animate-slide-up">
            Join Lucknow's Leading
            <span className="block text-blue-300 animate-pulse">Healthcare Network</span>
          </h1>
          
          {/* Subheadline */}
          <p className="text-xl md:text-2xl mb-8 text-blue-100 max-w-4xl mx-auto leading-relaxed animate-fade-in-delay">
            Connect with patients who need your expertise. Flexible scheduling, 
            guaranteed payments, and professional growth opportunities in Lucknow's 
            premier healthcare platform.
          </p>
          
          {/* CTA Buttons */}
          <div className="flex flex-col sm:flex-row gap-4 justify-center mb-8 animate-bounce-in">
            <Button
              onClick={onSignUpClick}
              size="lg"
              className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-4 text-lg rounded-lg shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105 transform"
            >
              Start Your Provider Journey
            </Button>
            <Button
              onClick={onLoginClick}
              variant="outline"
              size="lg"
              className="border-2 border-blue-300 bg-blue-600/20 text-blue-100 hover:bg-blue-500 hover:text-white font-semibold px-8 py-4 text-lg rounded-lg transition-all duration-300 hover:scale-105 transform shadow-lg"
            >
              Already a Provider? Login
            </Button>
          </div>
          
          {/* Customer Portal Link */}
          <div className="text-center mb-12">
            <p className="text-blue-200 mb-4">Want to see what your patients experience?</p>
            <a
              href="http://localhost:5175"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center space-x-2 text-blue-300 hover:text-blue-100 transition-colors font-medium"
            >
              <span>View Customer Portal</span>
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
              </svg>
            </a>
          </div>
          
          {/* Trust Indicators */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6 max-w-4xl mx-auto animate-stagger-in">
            <div className="flex flex-col items-center animate-fade-in-up" style={{ animationDelay: '0.1s' }}>
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mb-2 hover:scale-110 transition-transform duration-300">
                <UserGroupIcon className="w-6 h-6" />
              </div>
              <span className="text-sm font-medium">500+ Providers</span>
            </div>
            <div className="flex flex-col items-center animate-fade-in-up" style={{ animationDelay: '0.2s' }}>
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mb-2 hover:scale-110 transition-transform duration-300">
                <HeartIcon className="w-6 h-6" />
              </div>
              <span className="text-sm font-medium">10,000+ Patients</span>
            </div>
            <div className="flex flex-col items-center animate-fade-in-up" style={{ animationDelay: '0.3s' }}>
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mb-2 hover:scale-110 transition-transform duration-300">
                <StarIcon className="w-6 h-6" />
              </div>
              <span className="text-sm font-medium">4.8/5 Rating</span>
            </div>
            <div className="flex flex-col items-center animate-fade-in-up" style={{ animationDelay: '0.4s' }}>
              <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mb-2 hover:scale-110 transition-transform duration-300">
                <ShieldCheckIcon className="w-6 h-6" />
              </div>
              <span className="text-sm font-medium">100% Verified</span>
            </div>
          </div>
        </div>
      </div>
      
      {/* Floating Elements */}
      <div className="absolute top-20 left-10 w-20 h-20 bg-blue-500/10 rounded-full blur-xl animate-float"></div>
      <div className="absolute bottom-20 right-10 w-32 h-32 bg-slate-500/10 rounded-full blur-xl animate-float-reverse"></div>
      <div className="absolute top-1/2 left-1/4 w-16 h-16 bg-blue-400/10 rounded-full blur-lg animate-float-slow"></div>
    </section>
  );
};
