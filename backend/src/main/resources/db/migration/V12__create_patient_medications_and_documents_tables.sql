-- V12: Create patient_medications and patient_documents tables
-- These tables store medical history and documentation for patients

-- Patient Medications Table
CREATE TABLE patient_medications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100), -- e.g., "Twice daily", "As needed", "Every 8 hours"
    purpose TEXT,
    start_date DATE,
    end_date DATE,
    is_ongoing BOOLEAN DEFAULT TRUE,
    prescribing_doctor VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_medication_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- Indexes for patient_medications
CREATE INDEX idx_patient_medications_patient_id ON patient_medications(patient_id);
CREATE INDEX idx_patient_medications_is_ongoing ON patient_medications(is_ongoing);
CREATE INDEX idx_patient_medications_patient_ongoing ON patient_medications(patient_id, is_ongoing);

-- Patient Documents Table
CREATE TABLE patient_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL CHECK (document_type IN ('PRESCRIPTION', 'MEDICAL_REPORT', 'LAB_RESULT', 'XRAY', 'SCAN', 'INSURANCE', 'OTHER')),
    file_name VARCHAR(255) NOT NULL,
    file_path TEXT NOT NULL, -- Local storage path or S3 URL
    file_size BIGINT, -- in bytes
    mime_type VARCHAR(100),
    description TEXT,
    document_date DATE, -- Date on the document (not upload date)
    uploaded_by UUID, -- User who uploaded (customer_id)
    is_verified BOOLEAN DEFAULT FALSE, -- Admin/Provider verification flag
    verified_by UUID, -- Admin/Provider who verified
    verified_at TIMESTAMP,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_document_patient FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_document_verified_by FOREIGN KEY (verified_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Indexes for patient_documents
CREATE INDEX idx_patient_documents_patient_id ON patient_documents(patient_id);
CREATE INDEX idx_patient_documents_document_type ON patient_documents(document_type);
CREATE INDEX idx_patient_documents_uploaded_at ON patient_documents(uploaded_at);
CREATE INDEX idx_patient_documents_patient_type ON patient_documents(patient_id, document_type);
CREATE INDEX idx_patient_documents_is_verified ON patient_documents(is_verified);

-- Comments for documentation
COMMENT ON TABLE patient_medications IS 'Stores current and past medications for patients';
COMMENT ON COLUMN patient_medications.is_ongoing IS 'TRUE if patient is currently taking this medication';
COMMENT ON COLUMN patient_medications.frequency IS 'How often the medication should be taken';

COMMENT ON TABLE patient_documents IS 'Stores uploaded medical documents for patients';
COMMENT ON COLUMN patient_documents.file_path IS 'Local file system path or cloud storage URL';
COMMENT ON COLUMN patient_documents.file_size IS 'File size in bytes for storage management';
COMMENT ON COLUMN patient_documents.is_verified IS 'Flag for admin/provider to mark document as reviewed and authentic';

