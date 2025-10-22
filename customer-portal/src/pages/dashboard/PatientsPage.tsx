import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  PlusIcon, 
  MagnifyingGlassIcon, 
  PencilIcon, 
  TrashIcon, 
  UserIcon, 
  HeartIcon,
  BeakerIcon,
  DocumentTextIcon,
  ExclamationTriangleIcon,
  CalendarIcon
} from '@heroicons/react/24/outline';
import { AddPatientForm } from '../../components/patients/AddPatientForm';
import { patientsApi } from '../../lib/api/patients.api';
import { usePatient } from '../../lib/context/PatientContext';
import type { Patient } from '../../types/patient.types';

export function PatientsPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [showAddForm, setShowAddForm] = useState(false);
  const [viewPatient, setViewPatient] = useState<Patient | null>(null);
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { setSelectedPatient } = usePatient();

  const handleBookForPatient = (patient: Patient) => {
    setSelectedPatient(patient);
    navigate('/services');
  };

  // Fetch patients
  const { data: patients = [], isLoading, refetch } = useQuery({
    queryKey: ['patients'],
    queryFn: () => patientsApi.getPatients(),
  });

  // Delete patient mutation
  const deleteMutation = useMutation({
    mutationFn: (patientId: string) => patientsApi.deletePatient(patientId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['patients'] });
    },
  });

  const handleDeletePatient = async (patientId: string, patientName: string) => {
    if (window.confirm(`Are you sure you want to delete ${patientName}? This action cannot be undone.`)) {
      try {
        await deleteMutation.mutateAsync(patientId);
      } catch (error) {
        console.error('Error deleting patient:', error);
        alert('Failed to delete patient');
      }
    }
  };

  const handleAddSuccess = () => {
    setShowAddForm(false);
    refetch();
  };

  // Filter patients by search term
  const filteredPatients = patients.filter(patient =>
    patient.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Patients</h1>
          <p className="text-gray-600 mt-1">
            Manage family members and their medical information
          </p>
        </div>
        <button
          onClick={() => setShowAddForm(true)}
          className="inline-flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <PlusIcon className="w-5 h-5 mr-2" />
          Add Patient
        </button>
      </div>

      {/* Search Bar */}
      <div className="bg-white rounded-lg shadow-sm p-4">
        <div className="relative">
          <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            type="text"
            placeholder="Search patients by name..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
      </div>

      {/* Privacy Notice */}
      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
        <div className="flex items-start space-x-3">
          <div className="flex-shrink-0">
            <svg
              className="w-5 h-5 text-blue-600"
              fill="currentColor"
              viewBox="0 0 20 20"
            >
              <path
                fillRule="evenodd"
                d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                clipRule="evenodd"
              />
            </svg>
          </div>
          <div className="flex-1">
            <h3 className="text-sm font-medium text-blue-900">
              Your Privacy Matters
            </h3>
            <p className="text-sm text-blue-700 mt-1">
              All patient information is encrypted and securely stored. Medical data is
              only shared with assigned healthcare providers during active bookings.
            </p>
          </div>
        </div>
      </div>

      {/* Patients List */}
      {isLoading ? (
        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading patients...</p>
        </div>
      ) : filteredPatients.length === 0 ? (
        // Empty State
        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
          <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <UserIcon className="w-8 h-8 text-blue-600" />
          </div>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            No Patients Yet
          </h3>
          <p className="text-gray-600 mb-6 max-w-md mx-auto">
            Add your first patient to start managing their healthcare information and
            booking services.
          </p>
          <button
            onClick={() => setShowAddForm(true)}
            className="inline-flex items-center justify-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <PlusIcon className="w-5 h-5 mr-2" />
            Add Your First Patient
          </button>
        </div>
      ) : (
        // Patient Cards
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredPatients.map((patient) => {
            const hasMedicalInfo = patient.isDiabetic || patient.bpStatus !== 'NORMAL' || 
                                   patient.allergies || patient.chronicConditions;
            
            return (
              <div
                key={patient.id}
                className="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow p-6"
              >
                {/* Patient Header */}
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                      <UserIcon className="w-6 h-6 text-blue-600" />
                    </div>
                    <div>
                      <h3 className="font-semibold text-gray-900">{patient.name}</h3>
                      <p className="text-sm text-gray-500">
                        {patient.age} years • {patient.gender}
                      </p>
                    </div>
                  </div>
                  {patient.isSensitiveData && (
                    <span className="inline-flex items-center px-2 py-1 bg-purple-100 text-purple-700 text-xs rounded-full">
                      <DocumentTextIcon className="w-3 h-3 mr-1" />
                      Sensitive
                    </span>
                  )}
                </div>

                {/* Patient Info */}
                <div className="space-y-2 mb-4">
                  <div className="flex items-center text-sm text-gray-600">
                    <span className="font-medium">Relationship:</span>
                    <span className="ml-2 capitalize">{patient.relationshipToCustomer.toLowerCase()}</span>
                  </div>
                  {patient.bloodGroup && patient.bloodGroup !== 'UNKNOWN' && (
                    <div className="flex items-center text-sm text-gray-600">
                      <span className="font-medium">Blood Group:</span>
                      <span className="ml-2">{patient.bloodGroup}</span>
                    </div>
                  )}
                  {patient.weight && (
                    <div className="flex items-center text-sm text-gray-600">
                      <span className="font-medium">Weight:</span>
                      <span className="ml-2">{patient.weight} kg</span>
                    </div>
                  )}
                </div>

                {/* Medical Indicators */}
                {hasMedicalInfo && (
                  <div className="flex items-center flex-wrap gap-2 mb-4">
                    {patient.isDiabetic && (
                      <span className="inline-flex items-center px-2 py-1 bg-orange-100 text-orange-700 text-xs rounded-full">
                        Diabetic
                      </span>
                    )}
                    {patient.bpStatus !== 'NORMAL' && (
                      <span className="inline-flex items-center px-2 py-1 bg-red-100 text-red-700 text-xs rounded-full">
                        BP: {patient.bpStatus}
                      </span>
                    )}
                    {patient.allergies && (
                      <span className="inline-flex items-center px-2 py-1 bg-yellow-100 text-yellow-700 text-xs rounded-full">
                        <ExclamationTriangleIcon className="w-3 h-3 mr-1" />
                        Allergies
                      </span>
                    )}
                    {patient.chronicConditions && (
                      <span className="inline-flex items-center px-2 py-1 bg-red-100 text-red-700 text-xs rounded-full">
                        <HeartIcon className="w-3 h-3 mr-1" />
                        Conditions
                      </span>
                    )}
                  </div>
                )}

                {/* Actions */}
                <div className="flex flex-col gap-2 pt-4 border-t">
                  <button
                    onClick={() => handleBookForPatient(patient)}
                    className="w-full inline-flex items-center justify-center px-4 py-2.5 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-lg hover:from-blue-700 hover:to-indigo-700 transition-all shadow-sm hover:shadow-md text-sm font-semibold"
                  >
                    <CalendarIcon className="w-4 h-4 mr-2" />
                    Book Service for {patient.name}
                  </button>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => setViewPatient(patient)}
                      className="flex-1 inline-flex items-center justify-center px-3 py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors text-sm font-medium"
                    >
                      <PencilIcon className="w-4 h-4 mr-1" />
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeletePatient(patient.id, patient.name)}
                      disabled={deleteMutation.isPending}
                      className="inline-flex items-center justify-center px-3 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors disabled:opacity-50"
                    >
                      <TrashIcon className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Add Patient Modal */}
      {showAddForm && (
        <AddPatientForm
          onSuccess={handleAddSuccess}
          onClose={() => setShowAddForm(false)}
        />
      )}
    </div>
  );
}

