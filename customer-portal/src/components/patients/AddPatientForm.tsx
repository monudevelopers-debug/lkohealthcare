import { useState } from 'react';
import { X, User, Heart, AlertCircle, Phone, Shield } from 'lucide-react';
import { ConsentModal } from '../consent/ConsentModal';
import { patientsApi, consentsApi } from '../../lib/api/patients.api';
import type { CreatePatientRequest, Gender, BPStatus, Relationship, ConsentType } from '../../types/patient.types';

interface AddPatientFormProps {
  onSuccess: () => void;
  onClose: () => void;
}

export function AddPatientForm({ onSuccess, onClose }: AddPatientFormProps) {
  const [step, setStep] = useState<'form' | 'consent'>('form');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [requiredConsents, setRequiredConsents] = useState<any[]>([]);

  const [formData, setFormData] = useState<CreatePatientRequest>({
    name: '',
    age: 0,
    gender: 'MALE',
    weight: undefined,
    height: undefined,
    bloodGroup: '',
    isDiabetic: false,
    bpStatus: 'NORMAL',
    allergies: '',
    chronicConditions: '',
    emergencyContactName: '',
    emergencyContactPhone: '',
    emergencyContactRelation: '',
    relationshipToCustomer: 'SELF',
    isSensitiveData: false,
  });

  const handleChange = (field: keyof CreatePatientRequest, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    // Validation
    if (!formData.name || formData.name.trim().length < 2) {
      setError('Please enter a valid name (minimum 2 characters)');
      return;
    }

    if (formData.age < 1 || formData.age > 150) {
      setError('Please enter a valid age (1-150)');
      return;
    }

    if (formData.emergencyContactPhone && !/^[0-9]{10}$/.test(formData.emergencyContactPhone)) {
      setError('Emergency contact phone must be 10 digits');
      return;
    }

    try {
      setIsLoading(true);

      // Check required consents
      const consents = await consentsApi.getRequiredConsents();
      const unaceptedConsents = consents.filter(c => !c.isAccepted);

      if (unaceptedConsents.length > 0) {
        setRequiredConsents(consents);
        setStep('consent');
        setIsLoading(false);
        return;
      }

      // Create patient
      await patientsApi.createPatient(formData);
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to create patient');
      setIsLoading(false);
    }
  };

  const handleAcceptConsents = async (acceptedConsents: ConsentType[]) => {
    try {
      setIsLoading(true);

      // Accept all consents
      for (const consentType of acceptedConsents) {
        await consentsApi.acceptConsent(consentType);
      }

      // Now create the patient
      await patientsApi.createPatient(formData);
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to create patient');
      setIsLoading(false);
      setStep('form');
    }
  };

  if (step === 'consent') {
    return (
      <ConsentModal
        consents={requiredConsents}
        onAcceptAll={handleAcceptConsents}
        onClose={() => setStep('form')}
        isLoading={isLoading}
      />
    );
  }

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-3xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-white border-b px-6 py-4 flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Add New Patient</h2>
            <p className="text-gray-600 text-sm mt-1">Add a family member or patient</p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4 flex items-start space-x-3">
              <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
              <p className="text-red-700 text-sm">{error}</p>
            </div>
          )}

          {/* Basic Information */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <User className="w-5 h-5 mr-2 text-blue-600" />
              Basic Information
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Full Name <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={formData.name}
                  onChange={(e) => handleChange('name', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Enter patient's full name"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Age <span className="text-red-500">*</span>
                </label>
                <input
                  type="number"
                  required
                  min="0"
                  max="150"
                  value={formData.age || ''}
                  onChange={(e) => handleChange('age', parseInt(e.target.value) || 0)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Age in years"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Gender <span className="text-red-500">*</span>
                </label>
                <select
                  required
                  value={formData.gender}
                  onChange={(e) => handleChange('gender', e.target.value as Gender)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Relationship <span className="text-red-500">*</span>
                </label>
                <select
                  required
                  value={formData.relationshipToCustomer}
                  onChange={(e) => handleChange('relationshipToCustomer', e.target.value as Relationship)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="SELF">Self</option>
                  <option value="PARENT">Parent</option>
                  <option value="CHILD">Child</option>
                  <option value="SPOUSE">Spouse</option>
                  <option value="SIBLING">Sibling</option>
                  <option value="GRANDPARENT">Grandparent</option>
                  <option value="GRANDCHILD">Grandchild</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Blood Group
                </label>
                <select
                  value={formData.bloodGroup || ''}
                  onChange={(e) => handleChange('bloodGroup', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="">Select blood group</option>
                  <option value="A+">A+</option>
                  <option value="A-">A-</option>
                  <option value="B+">B+</option>
                  <option value="B-">B-</option>
                  <option value="AB+">AB+</option>
                  <option value="AB-">AB-</option>
                  <option value="O+">O+</option>
                  <option value="O-">O-</option>
                  <option value="UNKNOWN">Unknown</option>
                </select>
              </div>
            </div>
          </div>

          {/* Physical Attributes */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Physical Attributes</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Weight (kg)
                </label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  max="500"
                  value={formData.weight || ''}
                  onChange={(e) => handleChange('weight', parseFloat(e.target.value) || undefined)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Weight in kilograms"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Height (cm)
                </label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  max="300"
                  value={formData.height || ''}
                  onChange={(e) => handleChange('height', parseFloat(e.target.value) || undefined)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Height in centimeters"
                />
              </div>
            </div>
          </div>

          {/* Medical Information */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <Heart className="w-5 h-5 mr-2 text-red-600" />
              Medical Information
            </h3>
            <div className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-center space-x-3">
                  <input
                    type="checkbox"
                    id="isDiabetic"
                    checked={formData.isDiabetic}
                    onChange={(e) => handleChange('isDiabetic', e.target.checked)}
                    className="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                  />
                  <label htmlFor="isDiabetic" className="text-sm font-medium text-gray-700">
                    Diabetic
                  </label>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Blood Pressure Status
                  </label>
                  <select
                    value={formData.bpStatus}
                    onChange={(e) => handleChange('bpStatus', e.target.value as BPStatus)}
                    className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    <option value="NORMAL">Normal</option>
                    <option value="HIGH">High</option>
                    <option value="LOW">Low</option>
                    <option value="UNKNOWN">Unknown</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Allergies
                </label>
                <textarea
                  value={formData.allergies || ''}
                  onChange={(e) => handleChange('allergies', e.target.value)}
                  rows={2}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="List any known allergies (medications, food, etc.)"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Chronic Conditions
                </label>
                <textarea
                  value={formData.chronicConditions || ''}
                  onChange={(e) => handleChange('chronicConditions', e.target.value)}
                  rows={2}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="List any chronic medical conditions"
                />
              </div>
            </div>
          </div>

          {/* Emergency Contact */}
          <div>
            <h3 className="text-lg font-semibold text-gray-900 mb-4 flex items-center">
              <Phone className="w-5 h-5 mr-2 text-green-600" />
              Emergency Contact
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Contact Name
                </label>
                <input
                  type="text"
                  value={formData.emergencyContactName || ''}
                  onChange={(e) => handleChange('emergencyContactName', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="Emergency contact person"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Contact Phone
                </label>
                <input
                  type="tel"
                  value={formData.emergencyContactPhone || ''}
                  onChange={(e) => handleChange('emergencyContactPhone', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="10-digit phone number"
                  maxLength={10}
                />
              </div>

              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Relationship to Emergency Contact
                </label>
                <input
                  type="text"
                  value={formData.emergencyContactRelation || ''}
                  onChange={(e) => handleChange('emergencyContactRelation', e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  placeholder="e.g., Father, Mother, Spouse, Friend"
                />
              </div>
            </div>
          </div>

          {/* Privacy Settings */}
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
            <div className="flex items-start space-x-3">
              <Shield className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
              <div className="flex-1">
                <div className="flex items-center space-x-3">
                  <input
                    type="checkbox"
                    id="isSensitiveData"
                    checked={formData.isSensitiveData}
                    onChange={(e) => handleChange('isSensitiveData', e.target.checked)}
                    className="w-4 h-4 text-blue-600 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                  />
                  <label htmlFor="isSensitiveData" className="text-sm font-medium text-blue-900">
                    Mark as Sensitive Data
                  </label>
                </div>
                <p className="text-sm text-blue-700 mt-1 ml-7">
                  Enable extra privacy protections for this patient's medical information
                </p>
              </div>
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end space-x-3 pt-4 border-t">
            <button
              type="button"
              onClick={onClose}
              disabled={isLoading}
              className="px-6 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 flex items-center space-x-2"
            >
              {isLoading && (
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
              )}
              <span>{isLoading ? 'Creating...' : 'Add Patient'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

