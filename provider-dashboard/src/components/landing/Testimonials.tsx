import React from 'react';
import { StarIcon } from '@heroicons/react/24/solid';

export const Testimonials: React.FC = () => {
  const testimonials = [
    {
      name: 'Dr. Priya Sharma',
      role: 'Senior Physiotherapist',
      location: 'Gomti Nagar, Lucknow',
      comment: 'Joining Lucknow Healthcare has transformed my practice. I can now focus on what I do best - helping patients recover. The platform handles all the administrative work, and I earn 40% more than my previous clinic.',
      rating: 5,
      image: '👩‍⚕️',
      earnings: '₹45,000/month',
    },
    {
      name: 'Dr. Rajesh Kumar',
      role: 'Home Nursing Specialist',
      location: 'Hazratganj, Lucknow',
      comment: 'The flexibility is amazing! I can choose my working hours and still earn a great income. The patients are well-screened and appreciative. It\'s the best decision I made for my career.',
      rating: 5,
      image: '👨‍⚕️',
      earnings: '₹38,000/month',
    },
    {
      name: 'Sister Anita Verma',
      role: 'Elder Care Specialist',
      location: 'Alambagh, Lucknow',
      comment: 'I love the sense of community here. The support team is always available, and I\'ve built lasting relationships with my patients. The payment system is so reliable - no more chasing bills!',
      rating: 5,
      image: '👩‍⚕️',
      earnings: '₹32,000/month',
    },
    {
      name: 'Dr. Amit Singh',
      role: 'Physiotherapist',
      location: 'Indira Nagar, Lucknow',
      comment: 'As a new graduate, this platform gave me the perfect start to my career. I get quality patients, fair compensation, and continuous learning opportunities. Highly recommended for young professionals!',
      rating: 5,
      image: '👨‍⚕️',
      earnings: '₹28,000/month',
    },
  ];

  return (
    <section className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
            What Our Providers Say
          </h2>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            Real stories from healthcare professionals who have transformed 
            their careers with Lucknow Healthcare
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {testimonials.map((testimonial, index) => (
            <div
              key={index}
              className="bg-gradient-to-br from-slate-50 to-blue-50 rounded-xl p-6 shadow-lg hover:shadow-xl transition-all duration-500 transform hover:-translate-y-2 hover:scale-105 animate-fade-in-up"
              style={{ animationDelay: `${index * 0.15}s` }}
            >
              {/* Rating */}
              <div className="flex items-center mb-4">
                {[...Array(testimonial.rating)].map((_, i) => (
                  <StarIcon key={i} className="w-5 h-5 text-amber-500 animate-pulse" style={{ animationDelay: `${i * 0.1}s` }} />
                ))}
              </div>
              
              {/* Comment */}
              <p className="text-gray-700 mb-6 leading-relaxed italic">
                "{testimonial.comment}"
              </p>
              
              {/* Provider Info */}
              <div className="flex items-center space-x-4">
                <div className="w-12 h-12 bg-gradient-to-r from-blue-600 to-slate-600 rounded-full flex items-center justify-center text-2xl">
                  {testimonial.image}
                </div>
                <div className="flex-1">
                  <h4 className="font-bold text-gray-900">{testimonial.name}</h4>
                  <p className="text-sm text-gray-600">{testimonial.role}</p>
                  <p className="text-xs text-gray-500">{testimonial.location}</p>
                </div>
              </div>
              
              {/* Earnings Badge */}
              <div className="mt-4 inline-block bg-emerald-100 text-emerald-800 text-sm font-medium px-3 py-1 rounded-full">
                {testimonial.earnings}
              </div>
            </div>
          ))}
        </div>

        {/* Stats Section */}
        <div className="mt-16 bg-gradient-to-r from-slate-800 to-blue-900 rounded-2xl p-8 text-white">
          <div className="text-center mb-8">
            <h3 className="text-2xl font-bold mb-4">
              Join Lucknow's Most Successful Healthcare Network
            </h3>
            <p className="text-slate-200">
              See why healthcare professionals choose us
            </p>
          </div>
          
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            <div className="text-center">
              <div className="text-3xl font-bold mb-2">500+</div>
              <div className="text-slate-200">Active Providers</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold mb-2">₹2.5L+</div>
              <div className="text-slate-200">Avg Monthly Earnings</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold mb-2">4.8/5</div>
              <div className="text-slate-200">Provider Rating</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold mb-2">95%</div>
              <div className="text-slate-200">Satisfaction Rate</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
