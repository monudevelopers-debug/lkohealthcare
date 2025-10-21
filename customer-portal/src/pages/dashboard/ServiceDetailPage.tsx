import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useService } from '../../lib/hooks/useServices';
import { useCreateBooking } from '../../lib/hooks/useBookings';
import { useAuth } from '../../lib/auth/AuthContext';
import { patientsApi } from '../../lib/api/patients.api';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';
import { Input } from '../../components/ui/Input';
import { formatCurrency } from '../../lib/utils/formatDate';
import { validateLucknowAddress } from '../../lib/utils/addressValidation';
import { 
  ArrowLeftIcon, 
  CheckIcon, 
  UserIcon, 
  PlusIcon, 
  ExclamationCircleIcon 
} from '@heroicons/react/24/outline';
import type { Patient } from '../../types/patient.types';

export const ServiceDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  const { data: service, isLoading } = useService(id!);
  const createBooking = useCreateBooking();

  // Fetch patients
  const { data: patients = [] } = useQuery({
    queryKey: ['patients'],
    queryFn: () => patientsApi.getPatients(),
    enabled: isAuthenticated,
  });

  const [selectedPatientId, setSelectedPatientId] = useState<string>('');
  const [bookingData, setBookingData] = useState({
    scheduledDateTime: '',
    address: '',
    notes: '',
  });
  const [error, setError] = useState('');
  const [addressError, setAddressError] = useState('');

  const handleAddressChange = (value: string) => {
    setBookingData({ ...bookingData, address: value });
    if (value.length >= 10) {
      const validation = validateLucknowAddress(value);
      setAddressError(validation.error || '');
    } else {
      setAddressError('');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!isAuthenticated || !user) {
      setError('Please login to book a service');
      return;
    }

    if (!service) {
      setError('Service not found');
      return;
    }

    // Validate patient selection
    if (!selectedPatientId) {
      setError('Please select a patient for this booking');
      return;
    }

    // Validate address
    const addressValidation = validateLucknowAddress(bookingData.address);
    if (!addressValidation.valid) {
      setAddressError(addressValidation.error || '');
      return;
    }

    setError('');
    
    try {
      // Parse datetime into date and time
      const datetime = new Date(bookingData.scheduledDateTime);
      const scheduledDate = datetime.toISOString().split('T')[0]; // YYYY-MM-DD
      const scheduledTime = datetime.toTimeString().split(' ')[0]; // HH:MM:SS
      
      // Calculate duration in hours (round up from minutes)
      const durationHours = Math.ceil(service.duration / 60);
      
      // Combine address and notes with patient info
      const specialInstructions = `Patient ID: ${selectedPatientId}\nAddress: ${bookingData.address}${bookingData.notes ? `\n\nNotes: ${bookingData.notes}` : ''}`;
      
      await createBooking.mutateAsync({
        userId: user.id,
        serviceId: id!,
        patientId: selectedPatientId, // Include patient ID
        scheduledDate: scheduledDate,
        scheduledTime: scheduledTime,
        duration: durationHours,
        totalAmount: service.price,
        specialInstructions: specialInstructions,
      });
      
      navigate('/bookings');
    } catch (err: any) {
      console.error('Booking error:', err);
      setError(err.response?.data?.message || 'Failed to create booking. Please try again.');
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
      </div>
    );
  }

  if (!service) {
    return (
      <div className="max-w-7xl mx-auto px-4 py-12 text-center">
        <p className="text-gray-500">Service not found</p>
        <Button onClick={() => navigate('/services')} className="mt-4">
          Back to Services
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Back Button */}
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate('/services')}
          className="mb-4"
        >
          <ArrowLeftIcon className="h-4 w-4 mr-2" />
          Back to Services
        </Button>

        <div className="grid lg:grid-cols-2 gap-8">
          {/* Service Details */}
          <div>
            <Card>
              <div className="mb-4">
                <span className="inline-block px-3 py-1 bg-primary-100 text-primary-700 text-sm rounded-full">
                  {service.category.name}
                </span>
              </div>

              <h1 className="text-3xl font-bold text-gray-900 mb-4">
                {service.name}
              </h1>

              <div className="flex items-baseline gap-2 mb-6">
                <span className="text-3xl font-bold text-primary-600">
                  {formatCurrency(service.price)}
                </span>
                <span className="text-gray-500">• {service.duration} minutes</span>
              </div>

              <div className="mb-6">
                <h2 className="text-lg font-semibold text-gray-900 mb-2">Description</h2>
                <p className="text-gray-600">{service.description}</p>
              </div>

              {service.features && service.features.length > 0 && (
                <div className="mb-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-2">Features</h2>
                  <ul className="space-y-2">
                    {service.features.map((feature, index) => (
                      <li key={index} className="flex items-start">
                        <CheckIcon className="h-5 w-5 text-green-500 mr-2 flex-shrink-0 mt-0.5" />
                        <span className="text-gray-600">{feature}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {service.requirements && service.requirements.length > 0 && (
                <div>
                  <h2 className="text-lg font-semibold text-gray-900 mb-2">Requirements</h2>
                  <ul className="space-y-2">
                    {service.requirements.map((req, index) => (
                      <li key={index} className="flex items-start">
                        <span className="text-primary-600 mr-2">•</span>
                        <span className="text-gray-600">{req}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </Card>
          </div>

          {/* Booking Form */}
          <div>
            <Card>
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Book This Service</h2>

              {error && (
                <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start space-x-3">
                  <ExclamationCircleIcon className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
                  <p className="text-red-700 text-sm">{error}</p>
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-6">
                {/* Patient Selection - FIRST STEP */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-3">
                    Select Patient <span className="text-red-500">*</span>
                  </label>
                  
                  {patients.length === 0 ? (
                    // No patients - prompt to add
                    <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                      <UserIcon className="w-12 h-12 text-gray-400 mx-auto mb-3" />
                      <p className="text-gray-600 mb-4">
                        You need to add a patient before booking a service
                      </p>
                      <Button
                        type="button"
                        onClick={() => navigate('/patients')}
                        className="mx-auto"
                      >
                        <PlusIcon className="w-4 h-4 mr-2" />
                        Add Patient
                      </Button>
                    </div>
                  ) : (
                    // Patient selector
                    <div className="space-y-3">
                      {patients.map((patient) => (
                        <div
                          key={patient.id}
                          onClick={() => setSelectedPatientId(patient.id)}
                          className={`border-2 rounded-lg p-4 cursor-pointer transition-all ${
                            selectedPatientId === patient.id
                              ? 'border-blue-500 bg-blue-50'
                              : 'border-gray-200 hover:border-blue-300'
                          }`}
                        >
                          <div className="flex items-center space-x-3">
                            <div className={`w-10 h-10 rounded-full flex items-center justify-center ${
                              selectedPatientId === patient.id ? 'bg-blue-500' : 'bg-gray-200'
                            }`}>
                              {selectedPatientId === patient.id ? (
                                <CheckIcon className="w-6 h-6 text-white" />
                              ) : (
                                <UserIcon className="w-5 h-5 text-gray-500" />
                              )}
                            </div>
                            <div className="flex-1">
                              <p className="font-medium text-gray-900">{patient.name}</p>
                              <p className="text-sm text-gray-500">
                                {patient.age} years • {patient.gender} • {patient.relationshipToCustomer}
                              </p>
                            </div>
                          </div>
                        </div>
                      ))}
                      
                      <button
                        type="button"
                        onClick={() => navigate('/patients')}
                        className="w-full border-2 border-dashed border-gray-300 rounded-lg p-4 text-center hover:border-blue-400 hover:bg-blue-50 transition-colors"
                      >
                        <PlusIcon className="w-5 h-5 text-gray-400 mx-auto mb-1" />
                        <span className="text-sm text-gray-600">Add New Patient</span>
                      </button>
                    </div>
                  )}
                </div>
                <Input
                  type="datetime-local"
                  label="Scheduled Date & Time"
                  value={bookingData.scheduledDateTime}
                  onChange={(e) => setBookingData({ ...bookingData, scheduledDateTime: e.target.value })}
                  min={new Date().toISOString().slice(0, 16)}
                  required
                />

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Service Address *
                  </label>
                  <textarea
                    value={bookingData.address}
                    onChange={(e) => handleAddressChange(e.target.value)}
                    required
                    rows={3}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    placeholder="Enter your complete address in Lucknow with pin code (226xxx)"
                  />
                  {addressError && (
                    <p className="mt-1 text-sm text-red-600">{addressError}</p>
                  )}
                  <p className="mt-1 text-sm text-gray-500">
                    We only serve in Lucknow area. Please include your pin code (226xxx)
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Additional Notes (Optional)
                  </label>
                  <textarea
                    value={bookingData.notes}
                    onChange={(e) => setBookingData({ ...bookingData, notes: e.target.value })}
                    rows={3}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
                    placeholder="Any special requirements or instructions..."
                  />
                </div>

                <div className="bg-gray-50 p-4 rounded-lg">
                  <div className="flex justify-between items-center mb-2">
                    <span className="text-gray-600">Service Price:</span>
                    <span className="font-semibold">{formatCurrency(service.price)}</span>
                  </div>
                  <div className="flex justify-between items-center text-lg font-bold">
                    <span>Total Amount:</span>
                    <span className="text-primary-600">{formatCurrency(service.price)}</span>
                  </div>
                </div>

                <Button
                  type="submit"
                  className="w-full"
                  isLoading={createBooking.isPending}
                  disabled={!!addressError || !isAuthenticated}
                >
                  {isAuthenticated ? 'Confirm Booking' : 'Please Login to Book'}
                </Button>

                <p className="text-xs text-gray-500 text-center">
                  By booking, you agree to our terms and conditions
                </p>
              </form>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};
