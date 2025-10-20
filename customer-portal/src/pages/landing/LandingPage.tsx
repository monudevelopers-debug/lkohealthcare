import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { SEO } from '../../components/layout/SEO';
import { 
  HeartIcon, 
  ShieldCheckIcon, 
  ClockIcon, 
  SparklesIcon,
  UserGroupIcon,
  PhoneIcon,
  CheckCircleIcon,
  StarIcon
} from '@heroicons/react/24/solid';

export const LandingPage = () => {
  const services = [
    {
      name: 'Home Nursing Care',
      description: 'Professional nursing care in the comfort of your home with certified nurses',
      price: 'Starting from ₹500/day',
      icon: HeartIcon,
      color: 'from-pink-500 to-rose-500',
    },
    {
      name: 'Physiotherapy',
      description: 'Expert physiotherapy sessions for recovery, wellness, and pain management',
      price: 'Starting from ₹800/session',
      icon: SparklesIcon,
      color: 'from-blue-500 to-cyan-500',
    },
    {
      name: 'Elder Care',
      description: 'Compassionate and dedicated care for your elderly family members',
      price: 'Starting from ₹600/day',
      icon: UserGroupIcon,
      color: 'from-purple-500 to-indigo-500',
    },
  ];

  const features = [
    {
      icon: ShieldCheckIcon,
      title: 'Certified Professionals',
      description: 'All our healthcare providers are verified, certified, and experienced',
      color: 'from-green-500 to-emerald-500',
    },
    {
      icon: ClockIcon,
      title: '24/7 Availability',
      description: 'Round-the-clock support and emergency services whenever you need',
      color: 'from-blue-500 to-indigo-500',
    },
    {
      icon: HeartIcon,
      title: 'Compassionate Care',
      description: 'We treat every patient with empathy, respect, and dignity',
      color: 'from-pink-500 to-rose-500',
    },
    {
      icon: CheckCircleIcon,
      title: 'Quality Assured',
      description: 'Highest standards of healthcare delivery at competitive prices',
      color: 'from-purple-500 to-violet-500',
    },
  ];

  const testimonials = [
    {
      name: 'Priya Sharma',
      role: 'Patient Family',
      comment: 'Excellent service! The nursing staff was professional and caring. Highly recommended for elderly care.',
      rating: 5,
      image: '👩',
    },
    {
      name: 'Rajesh Kumar',
      role: 'Patient',
      comment: 'Best physiotherapy experience. The therapist was knowledgeable and helped me recover quickly.',
      rating: 5,
      image: '👨',
    },
    {
      name: 'Anita Verma',
      role: 'Patient Family',
      comment: 'Very satisfied with the home nursing service. Professional, punctual, and caring staff.',
      rating: 5,
      image: '👩‍⚕️',
    },
  ];

  return (
    <>
      <SEO 
        title="Home - Professional Healthcare Services at Your Doorstep"
        description="Quality nursing care, physiotherapy, and elder care services in Lucknow. Book trusted healthcare professionals with just a few clicks. Available 24/7."
        keywords="healthcare lucknow, nursing services lucknow, home nursing lucknow, physiotherapy lucknow, elder care lucknow, healthcare services, home care, nursing care, medical services lucknow"
      />
      <div className="min-h-screen">
        {/* Hero Section */}
        <section className="relative overflow-hidden bg-gradient-to-br from-blue-600 via-purple-600 to-pink-600 text-white py-20 md:py-32">
          {/* Animated Background */}
          <div className="absolute inset-0 opacity-20">
            <div className="absolute top-0 left-0 w-96 h-96 bg-white rounded-full mix-blend-overlay filter blur-3xl animate-float"></div>
            <div className="absolute bottom-0 right-0 w-96 h-96 bg-white rounded-full mix-blend-overlay filter blur-3xl animate-float" style={{ animationDelay: '1s' }}></div>
          </div>

          <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center animate-fade-in">
              <div className="inline-block mb-4">
                <span className="px-4 py-2 bg-white/20 backdrop-blur-sm rounded-full text-sm font-semibold border border-white/30">
                  ✨ Professional Healthcare at Your Doorstep
                </span>
              </div>
              <h1 className="text-5xl md:text-7xl font-black mb-6 leading-tight">
                Quality Healthcare
                <br />
                <span className="bg-gradient-to-r from-yellow-300 via-pink-300 to-white bg-clip-text text-transparent">
                  Delivered to You
                </span>
              </h1>
              <p className="text-xl md:text-2xl mb-10 text-blue-100 max-w-3xl mx-auto font-light">
                Book certified nurses, physiotherapists, and caregivers in Lucknow. 
                Experience professional healthcare services at home.
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                <Button size="xl" variant="secondary" onClick={() => window.location.href = '/services'}>
                  <SparklesIcon className="w-5 h-5" />
                  Explore Services
                </Button>
                <Button size="xl" variant="outline" className="bg-white/10 backdrop-blur-sm border-white text-white hover:bg-white hover:text-blue-600">
                  <PhoneIcon className="w-5 h-5" />
                  Call +91-8400001034
                </Button>
              </div>

              {/* Stats */}
              <div className="grid grid-cols-3 gap-8 mt-16 max-w-3xl mx-auto">
                {[
                  { label: 'Happy Patients', value: '5000+' },
                  { label: 'Verified Professionals', value: '150+' },
                  { label: 'Services Delivered', value: '10K+' },
                ].map((stat, idx) => (
                  <div key={idx} className="text-center animate-scale-in" style={{ animationDelay: `${idx * 100}ms` }}>
                    <div className="text-3xl md:text-4xl font-black mb-1">{stat.value}</div>
                    <div className="text-sm md:text-base text-blue-200">{stat.label}</div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Wave Divider */}
          <div className="absolute bottom-0 left-0 right-0">
            <svg viewBox="0 0 1440 120" className="w-full h-auto">
              <path fill="#f9fafb" d="M0,64L80,69.3C160,75,320,85,480,80C640,75,800,53,960,48C1120,43,1280,53,1360,58.7L1440,64L1440,120L1360,120C1280,120,1120,120,960,120C800,120,640,120,480,120C320,120,160,120,80,120L0,120Z"></path>
            </svg>
          </div>
        </section>

        {/* Services Section */}
        <section className="py-20 bg-gradient-to-b from-gray-50 to-white">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center mb-16 animate-fade-in">
              <span className="px-4 py-2 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-full text-sm font-semibold mb-4 inline-block">
                Our Services
              </span>
              <h2 className="text-4xl md:text-5xl font-black text-gray-900 mb-4">
                Premium Healthcare Solutions
              </h2>
              <p className="text-xl text-gray-600 max-w-2xl mx-auto">
                Professional services delivered by qualified and experienced healthcare experts
              </p>
            </div>

            <div className="grid md:grid-cols-3 gap-8">
              {services.map((service, index) => (
                <Card 
                  key={index} 
                  hover 
                  className="text-center overflow-hidden group animate-scale-in"
                  style={{ animationDelay: `${index * 150}ms` }}
                >
                  <div className={`w-20 h-20 mx-auto mb-6 rounded-2xl bg-gradient-to-br ${service.color} flex items-center justify-center transform group-hover:scale-110 group-hover:rotate-6 transition-all duration-300`}>
                    <service.icon className="w-10 h-10 text-white" />
                  </div>
                  <h3 className="text-2xl font-bold text-gray-900 mb-3 group-hover:text-blue-600 transition-colors">
                    {service.name}
                  </h3>
                  <p className="text-gray-600 mb-6 leading-relaxed">
                    {service.description}
                  </p>
                  <div className="bg-gradient-to-r from-blue-50 to-purple-50 px-4 py-3 rounded-xl mb-6">
                    <p className="text-blue-700 font-bold text-lg">
                      {service.price}
                    </p>
                  </div>
                  <Button variant="outline" size="sm" fullWidth>
                    Learn More →
                  </Button>
                </Card>
              ))}
            </div>
          </div>
        </section>

        {/* Features Section */}
        <section className="py-20 bg-white">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center mb-16">
              <span className="px-4 py-2 bg-gradient-to-r from-purple-600 to-pink-600 text-white rounded-full text-sm font-semibold mb-4 inline-block">
                Why Choose Us
              </span>
              <h2 className="text-4xl md:text-5xl font-black text-gray-900 mb-4">
                Excellence in Every Service
              </h2>
            </div>

            <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
              {features.map((feature, index) => (
                <div 
                  key={index} 
                  className="text-center group animate-fade-in"
                  style={{ animationDelay: `${index * 100}ms` }}
                >
                  <div className={`w-16 h-16 mx-auto mb-6 rounded-2xl bg-gradient-to-br ${feature.color} flex items-center justify-center transform group-hover:scale-110 group-hover:rotate-12 transition-all duration-300 shadow-lg`}>
                    <feature.icon className="w-8 h-8 text-white" />
                  </div>
                  <h3 className="text-xl font-bold text-gray-900 mb-3 group-hover:text-blue-600 transition-colors">
                    {feature.title}
                  </h3>
                  <p className="text-gray-600 leading-relaxed">
                    {feature.description}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Testimonials Section */}
        <section className="py-20 bg-gradient-to-br from-gray-50 to-blue-50">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center mb-16">
              <span className="px-4 py-2 bg-gradient-to-r from-blue-600 to-cyan-600 text-white rounded-full text-sm font-semibold mb-4 inline-block">
                Testimonials
              </span>
              <h2 className="text-4xl md:text-5xl font-black text-gray-900 mb-4">
                What Our Patients Say
              </h2>
            </div>

            <div className="grid md:grid-cols-3 gap-8">
              {testimonials.map((testimonial, index) => (
                <Card 
                  key={index} 
                  hover
                  gradient
                  className="animate-slide-in-left"
                  style={{ animationDelay: `${index * 150}ms` }}
                >
                  <div className="flex items-center mb-4">
                    {[...Array(testimonial.rating)].map((_, i) => (
                      <StarIcon key={i} className="w-5 h-5 text-yellow-400" />
                    ))}
                  </div>
                  <p className="text-gray-700 mb-6 italic leading-relaxed">
                    "{testimonial.comment}"
                  </p>
                  <div className="flex items-center">
                    <div className="w-12 h-12 rounded-full bg-gradient-to-br from-blue-500 to-purple-500 flex items-center justify-center text-2xl mr-3">
                      {testimonial.image}
                    </div>
                    <div>
                      <p className="font-bold text-gray-900">{testimonial.name}</p>
                      <p className="text-sm text-gray-500">{testimonial.role}</p>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          </div>
        </section>

        {/* CTA Section */}
        <section className="relative py-20 overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-br from-blue-600 via-purple-600 to-pink-600"></div>
          <div className="absolute inset-0 opacity-30">
            <div className="absolute top-0 left-0 w-96 h-96 bg-white rounded-full mix-blend-overlay filter blur-3xl animate-pulse-slow"></div>
            <div className="absolute bottom-0 right-0 w-96 h-96 bg-white rounded-full mix-blend-overlay filter blur-3xl animate-pulse-slow" style={{ animationDelay: '1s' }}></div>
          </div>

          <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
            <div className="animate-scale-in">
              <h2 className="text-4xl md:text-5xl font-black text-white mb-6">
                Ready to Experience
                <br />
                Premium Healthcare?
              </h2>
              <p className="text-xl mb-10 text-blue-100 max-w-2xl mx-auto">
                Join thousands of satisfied patients who trust us for their healthcare needs
              </p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                <Button 
                  size="xl" 
                  variant="secondary"
                  onClick={() => window.location.href = '/services'}
                >
                  <HeartIcon className="w-5 h-5" />
                  Book Service Now
                </Button>
                <Button 
                  size="xl" 
                  variant="outline"
                  className="bg-white/10 backdrop-blur-sm border-2 border-white text-white hover:bg-white hover:text-blue-600"
                >
                  <PhoneIcon className="w-5 h-5" />
                  Call +91-8400001034
                </Button>
              </div>
            </div>

            {/* Trust Badges */}
            <div className="mt-16 grid grid-cols-2 md:grid-cols-4 gap-6 max-w-4xl mx-auto">
              {[
                { icon: ShieldCheckIcon, text: 'Verified Professionals' },
                { icon: CheckCircleIcon, text: '100% Satisfaction' },
                { icon: ClockIcon, text: '24/7 Support' },
                { icon: HeartIcon, text: 'Trusted by 5000+' },
              ].map((badge, idx) => (
                <div key={idx} className="flex flex-col items-center text-white/90 animate-fade-in" style={{ animationDelay: `${idx * 100}ms` }}>
                  <badge.icon className="w-8 h-8 mb-2" />
                  <p className="text-sm font-semibold">{badge.text}</p>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* How It Works Section */}
        <section className="py-20 bg-white">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="text-center mb-16">
              <span className="px-4 py-2 bg-gradient-to-r from-green-600 to-teal-600 text-white rounded-full text-sm font-semibold mb-4 inline-block">
                Simple Process
              </span>
              <h2 className="text-4xl md:text-5xl font-black text-gray-900 mb-4">
                Book in 3 Easy Steps
              </h2>
            </div>

            <div className="grid md:grid-cols-3 gap-12 max-w-5xl mx-auto">
              {[
                { step: '1', title: 'Choose Service', desc: 'Browse and select from our range of healthcare services' },
                { step: '2', title: 'Schedule Time', desc: 'Pick your preferred date, time, and location in Lucknow' },
                { step: '3', title: 'Get Care', desc: 'Our certified professional arrives at your doorstep' },
              ].map((item, idx) => (
                <div key={idx} className="text-center relative animate-fade-in" style={{ animationDelay: `${idx * 200}ms` }}>
                  <div className="w-20 h-20 mx-auto mb-6 rounded-full bg-gradient-to-br from-blue-600 to-purple-600 flex items-center justify-center text-3xl font-black text-white shadow-2xl transform hover:scale-110 transition-all duration-300">
                    {item.step}
                  </div>
                  <h3 className="text-2xl font-bold text-gray-900 mb-3">{item.title}</h3>
                  <p className="text-gray-600 leading-relaxed">{item.desc}</p>
                  
                  {idx < 2 && (
                    <div className="hidden md:block absolute top-10 -right-6 text-blue-300">
                      <svg className="w-12 h-12" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M10.293 3.293a1 1 0 011.414 0l6 6a1 1 0 010 1.414l-6 6a1 1 0 01-1.414-1.414L14.586 11H3a1 1 0 110-2h11.586l-4.293-4.293a1 1 0 010-1.414z" clipRule="evenodd"></path>
                      </svg>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        </section>
      </div>
    </>
  );
};