import { apiClient } from './client';
import type { 
  Patient, 
  CreatePatientRequest, 
  PatientMedication,
  ConsentRecord,
  RequiredConsent,
  ConsentType
} from '../../types/patient.types';

/**
 * Patient API Client
 */
export const patientsApi = {
  /**
   * Get all patients for current customer
   */
  async getPatients(page = -1, size = 20): Promise<Patient[]> {
    const params = new URLSearchParams();
    if (page >= 0) {
      params.append('page', page.toString());
      params.append('size', size.toString());
    }
    const response = await apiClient.get(`/patients?${params}`);
    
    // If paginated, return content array, otherwise return full array
    return Array.isArray(response.data) ? response.data : response.data.content;
  },

  /**
   * Get patient by ID
   */
  async getPatientById(id: string): Promise<Patient> {
    const response = await apiClient.get(`/patients/${id}`);
    return response.data;
  },

  /**
   * Create a new patient
   */
  async createPatient(patient: CreatePatientRequest): Promise<Patient> {
    const response = await apiClient.post('/patients', patient);
    return response.data;
  },

  /**
   * Update patient
   */
  async updatePatient(id: string, patient: Partial<CreatePatientRequest>): Promise<Patient> {
    const response = await apiClient.put(`/patients/${id}`, patient);
    return response.data;
  },

  /**
   * Delete patient (soft delete)
   */
  async deletePatient(id: string): Promise<void> {
    await apiClient.delete(`/patients/${id}`);
  },

  /**
   * Search patients by name
   */
  async searchPatients(name: string, page = 0, size = 20): Promise<Patient[]> {
    const response = await apiClient.get(`/patients/search`, {
      params: { name, page, size }
    });
    return response.data.content;
  },

  /**
   * Get patient medications
   */
  async getPatientMedications(patientId: string): Promise<PatientMedication[]> {
    const response = await apiClient.get(`/patients/${patientId}/medications`);
    return response.data;
  },

  /**
   * Get ongoing medications
   */
  async getOngoingMedications(patientId: string): Promise<PatientMedication[]> {
    const response = await apiClient.get(`/patients/${patientId}/medications/ongoing`);
    return response.data;
  },

  /**
   * Add medication
   */
  async addMedication(patientId: string, medication: Partial<PatientMedication>): Promise<PatientMedication> {
    const response = await apiClient.post(`/patients/${patientId}/medications`, medication);
    return response.data;
  },

  /**
   * Update medication
   */
  async updateMedication(
    patientId: string, 
    medicationId: string, 
    medication: Partial<PatientMedication>
  ): Promise<PatientMedication> {
    const response = await apiClient.put(
      `/patients/${patientId}/medications/${medicationId}`, 
      medication
    );
    return response.data;
  },

  /**
   * Stop medication
   */
  async stopMedication(patientId: string, medicationId: string): Promise<PatientMedication> {
    const response = await apiClient.post(`/patients/${patientId}/medications/${medicationId}/stop`);
    return response.data;
  },

  /**
   * Delete medication
   */
  async deleteMedication(patientId: string, medicationId: string): Promise<void> {
    await apiClient.delete(`/patients/${patientId}/medications/${medicationId}`);
  },
};

/**
 * Consent API Client
 */
export const consentsApi = {
  /**
   * Get required consents
   */
  async getRequiredConsents(): Promise<RequiredConsent[]> {
    const response = await apiClient.get('/consents/required');
    return response.data;
  },

  /**
   * Accept a consent
   */
  async acceptConsent(
    consentType: ConsentType, 
    consentVersion?: string,
    patientId?: string
  ): Promise<ConsentRecord> {
    const response = await apiClient.post('/consents/accept', {
      consentType,
      consentVersion,
      patientId,
    });
    return response.data;
  },

  /**
   * Get user's consents
   */
  async getUserConsents(): Promise<ConsentRecord[]> {
    const response = await apiClient.get('/consents');
    return response.data;
  },

  /**
   * Get valid consents
   */
  async getValidConsents(): Promise<ConsentRecord[]> {
    const response = await apiClient.get('/consents/valid');
    return response.data;
  },

  /**
   * Validate if user has specific consent
   */
  async validateConsent(consentType: ConsentType): Promise<boolean> {
    const response = await apiClient.get(`/consents/validate?type=${consentType}`);
    return response.data.hasValidConsent;
  },

  /**
   * Revoke a consent
   */
  async revokeConsent(consentId: string, reason: string): Promise<ConsentRecord> {
    const response = await apiClient.post(`/consents/${consentId}/revoke`, { reason });
    return response.data;
  },
};

