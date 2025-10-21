import { useState } from 'react';
import { Plus, Search, Edit, Trash2, User, Heart, Pill, FileText } from 'lucide-react';

// Placeholder for now - will connect to API later
export function PatientsPage() {
  const [searchTerm, setSearchTerm] = useState('');

  // Placeholder data
  const patients: any[] = [];

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
        <button className="inline-flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
          <Plus className="w-5 h-5 mr-2" />
          Add Patient
        </button>
      </div>

      {/* Search Bar */}
      <div className="bg-white rounded-lg shadow-sm p-4">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
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
      {patients.length === 0 ? (
        // Empty State
        <div className="bg-white rounded-lg shadow-sm p-12 text-center">
          <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <User className="w-8 h-8 text-blue-600" />
          </div>
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            No Patients Yet
          </h3>
          <p className="text-gray-600 mb-6 max-w-md mx-auto">
            Add your first patient to start managing their healthcare information and
            booking services.
          </p>
          <button className="inline-flex items-center justify-center px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
            <Plus className="w-5 h-5 mr-2" />
            Add Your First Patient
          </button>
        </div>
      ) : (
        // Patient Cards
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {patients.map((patient) => (
            <div
              key={patient.id}
              className="bg-white rounded-lg shadow-sm hover:shadow-md transition-shadow p-6"
            >
              {/* Patient Header */}
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center space-x-3">
                  <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
                    <User className="w-6 h-6 text-blue-600" />
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">{patient.name}</h3>
                    <p className="text-sm text-gray-500">
                      {patient.age} years • {patient.gender}
                    </p>
                  </div>
                </div>
              </div>

              {/* Patient Info */}
              <div className="space-y-2 mb-4">
                <div className="flex items-center text-sm text-gray-600">
                  <span className="font-medium">Relationship:</span>
                  <span className="ml-2">{patient.relationship}</span>
                </div>
                {patient.bloodGroup && (
                  <div className="flex items-center text-sm text-gray-600">
                    <span className="font-medium">Blood Group:</span>
                    <span className="ml-2">{patient.bloodGroup}</span>
                  </div>
                )}
              </div>

              {/* Medical Indicators */}
              <div className="flex items-center space-x-2 mb-4">
                {patient.hasMedications && (
                  <span className="inline-flex items-center px-2 py-1 bg-orange-100 text-orange-700 text-xs rounded-full">
                    <Pill className="w-3 h-3 mr-1" />
                    Medications
                  </span>
                )}
                {patient.hasConditions && (
                  <span className="inline-flex items-center px-2 py-1 bg-red-100 text-red-700 text-xs rounded-full">
                    <Heart className="w-3 h-3 mr-1" />
                    Conditions
                  </span>
                )}
                {patient.hasDocuments && (
                  <span className="inline-flex items-center px-2 py-1 bg-blue-100 text-blue-700 text-xs rounded-full">
                    <FileText className="w-3 h-3 mr-1" />
                    Documents
                  </span>
                )}
              </div>

              {/* Actions */}
              <div className="flex items-center space-x-2 pt-4 border-t">
                <button className="flex-1 inline-flex items-center justify-center px-3 py-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-colors text-sm font-medium">
                  <Edit className="w-4 h-4 mr-1" />
                  Edit
                </button>
                <button className="inline-flex items-center justify-center px-3 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors">
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

