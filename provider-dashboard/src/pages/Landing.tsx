import React, { useState } from 'react';
import { Helmet } from 'react-helmet-async';
import { Hero } from '../components/landing/Hero';
import { Features } from '../components/landing/Features';
import { Testimonials } from '../components/landing/Testimonials';
import { FAQ } from '../components/landing/FAQ';
import { SignupModal } from '../components/auth/SignupModal';
import { LoginModal } from '../components/auth/LoginModal';
import { Button } from '../components/ui/Button';
import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';

export const Landing: React.FC = () => {
  const [isSignupModalOpen, setIsSignupModalOpen] = useState(false);
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // Redirect authenticated users to dashboard
  React.useEffect(() => {
    if (isAuthenticated) {
      navigate('/dashboard');
    }
  }, [isAuthenticated, navigate]);

  const handleSignUpClick = () => {
    setIsSignupModalOpen(true);
  };

  const handleLoginClick = () => {
    setIsLoginModalOpen(true);
  };

  const handleSignupSuccess = () => {
    // User will be redirected to dashboard automatically
    navigate('/dashboard');
  };

  const handleLoginSuccess = () => {
    // User will be redirected to dashboard automatically
    navigate('/dashboard');
  };

  return (
    <>
      <Helmet>
        <title>Join Lucknow Healthcare - Professional Healthcare Providers Network</title>
        <meta 
          name="description" 
          content="Join Lucknow's leading healthcare network. Connect with patients, flexible scheduling, guaranteed payments. Register as a healthcare provider in Lucknow today." 
        />
        <meta 
          name="keywords" 
          content="healthcare providers lucknow, medical professionals lucknow, nursing jobs lucknow, physiotherapy lucknow, healthcare network lucknow, medical jobs lucknow, healthcare careers lucknow, doctor jobs lucknow, nurse jobs lucknow, healthcare platform lucknow" 
        />
        <meta name="robots" content="index, follow" />
        <meta name="author" content="Lucknow Healthcare" />
        
        {/* Open Graph / Facebook */}
        <meta property="og:type" content="website" />
        <meta property="og:url" content="https://providers.lucknowhealthcare.com/" />
        <meta property="og:title" content="Join Lucknow Healthcare - Professional Healthcare Providers Network" />
        <meta property="og:description" content="Join Lucknow's leading healthcare network. Connect with patients, flexible scheduling, guaranteed payments. Register as a healthcare provider in Lucknow today." />
        <meta property="og:image" content="https://providers.lucknowhealthcare.com/og-image.jpg" />
        
        {/* Twitter */}
        <meta property="twitter:card" content="summary_large_image" />
        <meta property="twitter:url" content="https://providers.lucknowhealthcare.com/" />
        <meta property="twitter:title" content="Join Lucknow Healthcare - Professional Healthcare Providers Network" />
        <meta property="twitter:description" content="Join Lucknow's leading healthcare network. Connect with patients, flexible scheduling, guaranteed payments. Register as a healthcare provider in Lucknow today." />
        <meta property="twitter:image" content="https://providers.lucknowhealthcare.com/og-image.jpg" />
        
        {/* Structured Data */}
        <script type="application/ld+json">
          {JSON.stringify({
            "@context": "https://schema.org",
            "@type": "Organization",
            "name": "Lucknow Healthcare",
            "url": "https://providers.lucknowhealthcare.com",
            "logo": "https://providers.lucknowhealthcare.com/logo.png",
            "description": "Leading healthcare network connecting patients with qualified healthcare providers in Lucknow",
            "address": {
              "@type": "PostalAddress",
              "addressLocality": "Lucknow",
              "addressRegion": "Uttar Pradesh",
              "addressCountry": "IN"
            },
            "sameAs": [
              "https://www.facebook.com/lucknowhealthcare",
              "https://www.linkedin.com/company/lucknowhealthcare",
              "https://twitter.com/lucknowhealthcare"
            ]
          })}
        </script>
      </Helmet>

      <div className="min-h-screen">
        {/* Hero Section */}
        <Hero 
          onSignUpClick={handleSignUpClick}
          onLoginClick={handleLoginClick}
        />

        {/* Features Section */}
        <Features />

        {/* Testimonials Section */}
        <Testimonials />

        {/* FAQ Section */}
        <FAQ />

        {/* Final CTA Section */}
        <section className="py-20 bg-gradient-to-r from-slate-800 to-blue-900 text-white animate-fade-in">
          <div className="max-w-4xl mx-auto text-center px-4 sm:px-6 lg:px-8">
            <h2 className="text-3xl md:text-4xl font-bold mb-6">
              Ready to Transform Your Healthcare Career?
            </h2>
            <p className="text-xl text-slate-200 mb-8 animate-fade-in-delay">
              Join hundreds of healthcare professionals who have already made the switch 
              to Lucknow Healthcare. Start your journey today!
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center animate-bounce-in">
              <Button
                onClick={handleSignUpClick}
                size="lg"
                className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-8 py-4 text-lg rounded-lg shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105"
              >
                Get Started Now - It's Free!
              </Button>
              <Button
                onClick={handleLoginClick}
                variant="outline"
                size="lg"
                className="border-2 border-blue-300 bg-blue-600/20 text-blue-100 hover:bg-blue-500 hover:text-white font-semibold px-8 py-4 text-lg rounded-lg transition-all duration-300 transform hover:scale-105 shadow-lg"
              >
                Already a Member? Login
              </Button>
            </div>
            
            <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-8 text-center animate-stagger-in">
              <div className="animate-fade-in-up" style={{ animationDelay: '0.1s' }}>
                <div className="text-3xl font-bold mb-2">500+</div>
                <div className="text-slate-200">Active Providers</div>
              </div>
              <div className="animate-fade-in-up" style={{ animationDelay: '0.2s' }}>
                <div className="text-3xl font-bold mb-2">₹2.5L+</div>
                <div className="text-slate-200">Avg Monthly Earnings</div>
              </div>
              <div className="animate-fade-in-up" style={{ animationDelay: '0.3s' }}>
                <div className="text-3xl font-bold mb-2">4.8/5</div>
                <div className="text-slate-200">Provider Rating</div>
              </div>
            </div>
          </div>
        </section>

        {/* Footer */}
        <footer className="bg-gray-900 text-white py-12">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
              <div>
                <h3 className="text-lg font-bold mb-4">Lucknow Healthcare</h3>
                <p className="text-gray-400">
                  Connecting patients with trusted healthcare providers in Lucknow.
                </p>
              </div>
              <div>
                <h4 className="font-semibold mb-4">For Providers</h4>
                <ul className="space-y-2 text-gray-400">
                  <li><a href="#" className="hover:text-white">How it Works</a></li>
                  <li><a href="#" className="hover:text-white">Requirements</a></li>
                  <li><a href="#" className="hover:text-white">Earnings</a></li>
                  <li><a href="#" className="hover:text-white">Support</a></li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Support</h4>
                <ul className="space-y-2 text-gray-400">
                  <li><a href="#" className="hover:text-white">Help Center</a></li>
                  <li><a href="#" className="hover:text-white">Contact Us</a></li>
                  <li><a href="#" className="hover:text-white">Privacy Policy</a></li>
                  <li><a href="#" className="hover:text-white">Terms of Service</a></li>
                </ul>
              </div>
              <div>
                <h4 className="font-semibold mb-4">Patient Experience</h4>
                <ul className="space-y-2 text-gray-400">
                  <li>
                    <a 
                      href="http://localhost:5175" 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="hover:text-white flex items-center space-x-2"
                    >
                      <span>View Customer Portal</span>
                      <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                      </svg>
                    </a>
                  </li>
                  <li className="text-sm text-gray-500">See how patients book services</li>
                  <li className="text-sm text-gray-500">Understand their journey</li>
                </ul>
              </div>
            </div>
            <div className="border-t border-gray-800 mt-8 pt-8">
              <div className="flex flex-col md:flex-row justify-between items-center">
                <p className="text-gray-400 mb-4 md:mb-0">
                  &copy; 2024 Lucknow Healthcare. All rights reserved.
                </p>
                <div className="flex items-center space-x-2 text-gray-400">
                  <span>Developed with</span>
                  <span className="text-red-500 animate-pulse">♥</span>
                  <span>by</span>
                  <a 
                    href="https://connatecoders.com" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="text-blue-400 hover:text-blue-300 transition-colors font-semibold"
                  >
                    ConnateCoders
                  </a>
                </div>
              </div>
            </div>
          </div>
        </footer>
      </div>

      {/* Modals */}
      <SignupModal
        isOpen={isSignupModalOpen}
        onClose={() => setIsSignupModalOpen(false)}
        onSuccess={handleSignupSuccess}
      />

      <LoginModal
        isOpen={isLoginModalOpen}
        onClose={() => setIsLoginModalOpen(false)}
        onSuccess={handleLoginSuccess}
      />
    </>
  );
};
