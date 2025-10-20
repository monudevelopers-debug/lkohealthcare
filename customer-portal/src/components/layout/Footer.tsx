import { 
  MapPinIcon, 
  PhoneIcon, 
  EnvelopeIcon,
  HeartIcon
} from '@heroicons/react/24/solid';

export const Footer = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="relative bg-gradient-to-br from-gray-900 via-gray-800 to-gray-900 text-white overflow-hidden">
      {/* Decorative Elements */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-0 left-0 w-96 h-96 bg-blue-500 rounded-full mix-blend-overlay filter blur-3xl animate-pulse-slow"></div>
        <div className="absolute bottom-0 right-0 w-96 h-96 bg-purple-500 rounded-full mix-blend-overlay filter blur-3xl animate-pulse-slow" style={{ animationDelay: '1s' }}></div>
      </div>

      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        <div className="grid md:grid-cols-4 gap-12 mb-12">
          {/* Company Info */}
          <div className="md:col-span-2">
            <div className="flex items-center space-x-3 mb-4">
              <div className="w-12 h-12 bg-gradient-to-br from-blue-600 to-purple-600 rounded-xl flex items-center justify-center shadow-xl">
                <HeartIcon className="w-6 h-6 text-white" />
              </div>
              <h3 className="text-2xl font-black">
                Lucknow Healthcare
              </h3>
            </div>
            <p className="text-gray-300 mb-6 leading-relaxed max-w-md">
              Professional healthcare services at your doorstep. Quality nursing care, 
              physiotherapy, and elder care services delivered with compassion and expertise.
            </p>
            <div className="space-y-3">
              <div className="flex items-start space-x-3 text-gray-300 hover:text-white transition-colors group">
                <MapPinIcon className="w-5 h-5 mt-1 text-blue-400 group-hover:scale-110 transition-transform" />
                <p className="flex-1 text-sm">
                  Behind Meena Market, Near Lekhraj Metro Station,<br />
                  Indira Nagar, Lucknow - 226016
                </p>
              </div>
              <a href="tel:+918400001034" className="flex items-center space-x-3 text-gray-300 hover:text-white transition-colors group">
                <PhoneIcon className="w-5 h-5 text-green-400 group-hover:scale-110 transition-transform" />
                <p className="text-sm font-semibold">+91-8400001034</p>
              </a>
              <a href="mailto:lucknow.services2014@gmail.com" className="flex items-center space-x-3 text-gray-300 hover:text-white transition-colors group">
                <EnvelopeIcon className="w-5 h-5 text-yellow-400 group-hover:scale-110 transition-transform" />
                <p className="text-sm">lucknow.services2014@gmail.com</p>
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-lg font-bold mb-4 text-white">Quick Links</h4>
            <ul className="space-y-3">
              {[
                { name: 'Home', href: '/' },
                { name: 'Services', href: '/services' },
                { name: 'About Us', href: '/about' },
                { name: 'Contact', href: '/contact' },
              ].map((link) => (
                <li key={link.name}>
                  <a 
                    href={link.href} 
                    className="text-gray-300 hover:text-white hover:translate-x-2 inline-block transition-all duration-300"
                  >
                    → {link.name}
                  </a>
                </li>
              ))}
            </ul>
          </div>

          {/* Services */}
          <div>
            <h4 className="text-lg font-bold mb-4 text-white">Our Services</h4>
            <ul className="space-y-3">
              {[
                'Home Nursing',
                'Physiotherapy',
                'Elder Care',
                'Medical Equipment',
                'Emergency Care',
                'Health Checkups',
              ].map((service) => (
                <li key={service}>
                  <a 
                    href="/services" 
                    className="text-gray-300 hover:text-white hover:translate-x-2 inline-block transition-all duration-300"
                  >
                    → {service}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-gray-700 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center space-y-4 md:space-y-0">
            <p className="text-gray-400 text-sm text-center md:text-left">
              © {currentYear} Lucknow Nursing HealthCare Services. All rights reserved.
            </p>
            <div className="flex items-center space-x-2">
              <span className="text-gray-400 text-sm">Crafted with</span>
              <HeartIcon className="w-4 h-4 text-red-500 animate-pulse" />
              <span className="text-gray-400 text-sm">by</span>
              <a 
                href="https://www.connatecoders.com" 
                target="_blank" 
                rel="noopener noreferrer"
                className="font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent hover:from-blue-500 hover:to-purple-500 transition-all"
              >
                Connatecoders
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};