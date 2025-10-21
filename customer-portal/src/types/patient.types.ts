export type Gender = 'MALE' | 'FEMALE' | 'OTHER';

export type BPStatus = 'NORMAL' | 'HIGH' | 'LOW' | 'UNKNOWN';

export type Relationship = 
  | 'SELF' 
  | 'PARENT' 
  | 'CHILD' 
  | 'SPOUSE' 
  | 'SIBLING' 
  | 'GRANDPARENT' 
  | 'GRANDCHILD' 
  | 'OTHER';

export interface Patient {
  id: string;
  customerId: string;
  name: string;
  age: number;
  gender: Gender;
  weight?: number;
  height?: number;
  bloodGroup?: string;
  isDiabetic: boolean;
  bpStatus: BPStatus;
  allergies?: string;
  chronicConditions?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  relationshipToCustomer: Relationship;
  isSensitiveData: boolean;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PatientMedication {
  id: string;
  patientId: string;
  medicationName: string;
  dosage?: string;
  frequency?: string;
  purpose?: string;
  startDate?: string;
  endDate?: string;
  isOngoing: boolean;
  prescribingDoctor?: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PatientDocument {
  id: string;
  patientId: string;
  documentType: DocumentType;
  fileName: string;
  filePath: string;
  fileSize?: number;
  mimeType?: string;
  description?: string;
  documentDate?: string;
  uploadedBy: string;
  isVerified: boolean;
  verifiedBy?: string;
  verifiedAt?: string;
  uploadedAt: string;
}

export type DocumentType = 
  | 'PRESCRIPTION'
  | 'MEDICAL_REPORT'
  | 'LAB_RESULT'
  | 'XRAY'
  | 'SCAN'
  | 'INSURANCE'
  | 'OTHER';

export type ConsentType = 
  | 'TERMS_AND_CONDITIONS'
  | 'PRIVACY_POLICY'
  | 'MEDICAL_DATA_SHARING'
  | 'HIPAA_COMPLIANCE'
  | 'EMERGENCY_TREATMENT'
  | 'DATA_RETENTION';

export interface ConsentRecord {
  id: string;
  userId: string;
  patientId?: string;
  consentType: ConsentType;
  consentVersion: string;
  isAccepted: boolean;
  acceptedAt?: string;
  ipAddress?: string;
  userAgent?: string;
  revokedAt?: string;
  revocationReason?: string;
  createdAt: string;
}

export interface RequiredConsent {
  consentType: ConsentType;
  latestVersion: string;
  isAccepted: boolean;
  title: string;
  description: string;
}

export interface CreatePatientRequest {
  name: string;
  age: number;
  gender: Gender;
  weight?: number;
  height?: number;
  bloodGroup?: string;
  isDiabetic: boolean;
  bpStatus: BPStatus;
  allergies?: string;
  chronicConditions?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  relationshipToCustomer: Relationship;
  isSensitiveData: boolean;
}

