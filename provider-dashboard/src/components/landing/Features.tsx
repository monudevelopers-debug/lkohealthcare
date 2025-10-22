import React from 'react';
import { 
  ClockIcon, 
  CurrencyDollarIcon, 
  AcademicCapIcon,
  UserGroupIcon,
  PhoneIcon,
  ChartBarIcon,
  ShieldCheckIcon,
  HeartIcon
} from '@heroicons/react/24/solid';

export const Features: React.FC = () => {
  const features = [
    {
      icon: ClockIcon,
      title: 'Flexible Schedule',
      description: 'Work when you want, accept bookings that fit your availability. Set your own hours and manage your calendar.',
      color: 'from-blue-600 to-blue-700',
    },
    {
      icon: CurrencyDollarIcon,
      title: 'Guaranteed Payments',
      description: 'Get paid on time, every time with our secure payment system. No chasing payments or billing hassles.',
      color: 'from-emerald-600 to-emerald-700',
    },
    {
      icon: AcademicCapIcon,
      title: 'Professional Growth',
      description: 'Access training, certifications, and career development opportunities. Stay updated with latest healthcare practices.',
      color: 'from-slate-600 to-slate-700',
    },
    {
      icon: UserGroupIcon,
      title: 'Quality Patients',
      description: 'Verified customers with complete medical histories. Build meaningful relationships with patients who value your expertise.',
      color: 'from-teal-600 to-teal-700',
    },
    {
      icon: PhoneIcon,
      title: '24/7 Support',
      description: 'Dedicated provider support team available anytime. Get help when you need it, day or night.',
      color: 'from-indigo-600 to-indigo-700',
    },
    {
      icon: ChartBarIcon,
      title: 'Competitive Rates',
      description: 'Earn competitive rates with transparent pricing. No hidden fees, just fair compensation for your skills.',
      color: 'from-blue-700 to-blue-800',
    },
    {
      icon: UserGroupIcon,
      title: 'Patient Experience Insight',
      description: 'See exactly what your patients experience when they book services. Understand their journey and improve your service delivery.',
      color: 'from-teal-600 to-teal-700',
    },
  ];

  return (
    <section className="py-20 bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
            Why Choose Lucknow Healthcare?
          </h2>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Join the most trusted healthcare platform in Lucknow and unlock 
            opportunities for professional growth and financial success.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <div
              key={index}
              className="bg-white rounded-xl p-8 shadow-lg hover:shadow-xl transition-all duration-500 transform hover:-translate-y-2 hover:scale-105 animate-fade-in-up"
              style={{ animationDelay: `${index * 0.1}s` }}
            >
              <div className={`w-16 h-16 bg-gradient-to-r ${feature.color} rounded-xl flex items-center justify-center mb-6 hover:scale-110 transition-transform duration-300`}>
                <feature.icon className="w-8 h-8 text-white" />
              </div>
              
              <h3 className="text-xl font-bold text-gray-900 mb-4">
                {feature.title}
              </h3>
              
              <p className="text-gray-600 leading-relaxed">
                {feature.description}
              </p>
            </div>
          ))}
        </div>

        {/* Additional Benefits Section */}
        <div className="mt-16 bg-white rounded-2xl p-8 shadow-lg">
          <div className="text-center mb-8">
            <h3 className="text-2xl font-bold text-gray-900 mb-4">
              Additional Benefits for Lucknow Providers
            </h3>
            <p className="text-gray-600">
              Special advantages for healthcare professionals in Lucknow
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="flex items-start space-x-4">
              <div className="w-12 h-12 bg-gradient-to-r from-emerald-600 to-emerald-700 rounded-lg flex items-center justify-center flex-shrink-0">
                <ShieldCheckIcon className="w-6 h-6 text-white" />
              </div>
              <div>
                <h4 className="font-bold text-gray-900 mb-2">Local Network</h4>
                <p className="text-gray-600">Connect with other healthcare professionals in Lucknow and build your local network.</p>
              </div>
            </div>
            
            <div className="flex items-start space-x-4">
              <div className="w-12 h-12 bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg flex items-center justify-center flex-shrink-0">
                <HeartIcon className="w-6 h-6 text-white" />
              </div>
              <div>
                <h4 className="font-bold text-gray-900 mb-2">Community Impact</h4>
                <p className="text-gray-600">Make a real difference in your community by providing accessible healthcare services.</p>
              </div>
            </div>
            
            <div className="flex items-start space-x-4">
              <div className="w-12 h-12 bg-gradient-to-r from-slate-600 to-slate-700 rounded-lg flex items-center justify-center flex-shrink-0">
                <ChartBarIcon className="w-6 h-6 text-white" />
              </div>
              <div>
                <h4 className="font-bold text-gray-900 mb-2">Growth Analytics</h4>
                <p className="text-gray-600">Track your performance, earnings, and patient satisfaction with detailed analytics.</p>
              </div>
            </div>
            
            <div className="flex items-start space-x-4">
              <div className="w-12 h-12 bg-gradient-to-r from-teal-600 to-teal-700 rounded-lg flex items-center justify-center flex-shrink-0">
                <UserGroupIcon className="w-6 h-6 text-white" />
              </div>
              <div>
                <h4 className="font-bold text-gray-900 mb-2">Referral Program</h4>
                <p className="text-gray-600">Earn bonuses by referring other qualified healthcare professionals to our platform.</p>
              </div>
            </div>
          </div>
        </div>
        
        {/* Customer Portal Insight Section */}
        <div className="mt-16 bg-gradient-to-r from-blue-50 to-slate-50 rounded-2xl p-8 border border-blue-100">
          <div className="text-center">
            <h3 className="text-2xl font-bold text-gray-900 mb-4">
              Understand Your Patients' Journey
            </h3>
            <p className="text-gray-600 mb-6 max-w-3xl mx-auto">
              See exactly how patients discover, book, and experience your services. 
              This insight helps you provide better care and understand what patients value most.
            </p>
            <a
              href="http://localhost:5175"
              target="_blank"
              rel="noopener noreferrer"
              className="inline-flex items-center space-x-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-3 rounded-lg shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105"
            >
              <span>Explore Customer Portal</span>
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
              </svg>
            </a>
          </div>
        </div>
      </div>
    </section>
  );
};
