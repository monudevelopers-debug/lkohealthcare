import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCategories, useServices, useServicesByCategory, useSearchServices } from '../../lib/hooks/useServices';
import { useDebounce } from '../../lib/hooks/useDebounce';
import { usePatient } from '../../lib/context/PatientContext';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { formatCurrency } from '../../lib/utils/formatDate';
import { MagnifyingGlassIcon, ClockIcon, TagIcon, UserIcon, ExclamationTriangleIcon } from '@heroicons/react/24/outline';

export const ServicesPage = () => {
  const navigate = useNavigate();
  const { selectedPatient } = usePatient();
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const debouncedSearch = useDebounce(searchQuery, 300);

  const { data: categories, isLoading: categoriesLoading } = useCategories();
  const { data: allServices, isLoading: servicesLoading } = useServices();
  const { data: categoryServices } = useServicesByCategory(selectedCategory);
  const { data: searchResults } = useSearchServices(debouncedSearch);

  const displayServices = debouncedSearch.length > 2 
    ? searchResults 
    : selectedCategory 
      ? categoryServices 
      : allServices;

  const isLoading = categoriesLoading || servicesLoading;

  return (
    <div className="min-h-screen py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-12 animate-fade-in">
          <span className="px-4 py-2 bg-gradient-to-r from-blue-600 to-purple-600 text-white rounded-full text-sm font-semibold mb-4 inline-block">
            Our Services
          </span>
          <h1 className="text-4xl md:text-5xl font-black text-gray-900 mb-4">
            Healthcare Services in Lucknow
          </h1>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            Professional healthcare delivered by qualified experts
          </p>
        </div>

        {/* Patient Selection Notice */}
        {!selectedPatient && (
          <div className="mb-8 bg-gradient-to-r from-amber-50 to-orange-50 border-2 border-amber-200 rounded-xl p-6">
            <div className="flex items-start gap-4">
              <div className="flex-shrink-0">
                <div className="w-12 h-12 bg-amber-100 rounded-full flex items-center justify-center">
                  <ExclamationTriangleIcon className="w-6 h-6 text-amber-600" />
                </div>
              </div>
              <div className="flex-1">
                <h3 className="text-lg font-semibold text-gray-900 mb-2">
                  📋 Select a Patient First
                </h3>
                <p className="text-gray-700 mb-4">
                  For a better booking experience, please select which patient needs the service. 
                  This helps our providers prepare and bring appropriate equipment.
                </p>
                <Button
                  onClick={() => navigate('/patients')}
                  className="bg-amber-600 hover:bg-amber-700 text-white"
                >
                  <UserIcon className="w-4 h-4 mr-2" />
                  Select or Add Patient
                </Button>
              </div>
            </div>
          </div>
        )}

        {/* Search Bar */}
        <div className="mb-8 max-w-2xl mx-auto animate-scale-in">
          <Input
            type="text"
            placeholder="Search for services... (e.g., nursing, physiotherapy)"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            icon={<MagnifyingGlassIcon className="w-5 h-5" />}
            className="text-lg"
          />
        </div>

        {/* Category Filters */}
        <div className="mb-12 animate-slide-in-left">
          <div className="flex flex-wrap gap-3 justify-center">
            <Button
              variant={selectedCategory === null ? 'primary' : 'secondary'}
              size="sm"
              onClick={() => setSelectedCategory(null)}
              className="shadow-md"
            >
              <TagIcon className="w-4 h-4" />
              All Services
            </Button>
            {categories?.map((category) => (
              <Button
                key={category.id}
                variant={selectedCategory === category.id ? 'primary' : 'secondary'}
                size="sm"
                onClick={() => setSelectedCategory(category.id)}
                className="shadow-md"
              >
                {category.name}
              </Button>
            ))}
          </div>
        </div>

        {/* Services Grid */}
        {isLoading ? (
          <div className="flex flex-col justify-center items-center py-20">
            <div className="w-16 h-16 border-4 border-blue-200 border-t-blue-600 rounded-full animate-spin mb-4"></div>
            <p className="text-gray-600 font-medium">Loading services...</p>
          </div>
        ) : displayServices && displayServices.length > 0 ? (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {displayServices.map((service, index) => (
              <div key={service.id} style={{ animationDelay: `${index * 50}ms` }}>
              <Card 
                hover
                gradient
                className="animate-scale-in"
              >
                <div className="flex flex-col h-full">
                  {/* Category Badge */}
                  <div className="mb-4">
                    <span className="inline-flex items-center px-3 py-1 bg-gradient-to-r from-blue-500 to-purple-500 text-white text-xs font-bold rounded-full shadow-md">
                      {service.category.name}
                    </span>
                  </div>
                  
                  {/* Service Name */}
                  <h3 className="text-2xl font-bold text-gray-900 mb-3 group-hover:text-blue-600 transition-colors">
                    {service.name}
                  </h3>
                  
                  {/* Description */}
                  <p className="text-gray-600 mb-6 flex-grow leading-relaxed">
                    {service.shortDescription || service.description}
                  </p>
                  
                  {/* Duration */}
                  <div className="flex items-center text-sm text-gray-500 mb-4">
                    <ClockIcon className="w-4 h-4 mr-2" />
                    <span>{service.duration} minutes</span>
                  </div>
                  
                  {/* Price and Action */}
                  <div className="flex items-center justify-between mt-auto pt-4 border-t border-gray-100">
                    <div>
                      <p className="text-sm text-gray-500 mb-1">Starting from</p>
                      <p className="text-3xl font-black bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                        {formatCurrency(service.price)}
                      </p>
                    </div>
                    <Button
                      size="md"
                      variant="primary"
                      onClick={() => window.location.href = `/services/${service.id}`}
                      className="shadow-lg"
                    >
                      Book Now →
                    </Button>
                  </div>
                </div>
              </Card>
              </div>
            ))}
          </div>
        ) : (
          <Card className="text-center py-20">
            <div className="text-6xl mb-4">🔍</div>
            <p className="text-gray-500 text-xl font-medium">
              {debouncedSearch.length > 2 
                ? 'No services found matching your search.' 
                : 'No services available.'}
            </p>
            <Button 
              variant="primary" 
              className="mt-6"
              onClick={() => {
                setSearchQuery('');
                setSelectedCategory(null);
              }}
            >
              Clear Filters
            </Button>
          </Card>
        )}
      </div>
    </div>
  );
};