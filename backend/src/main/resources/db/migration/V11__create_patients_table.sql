-- V11: Create patients table for patient management
-- This table stores information about patients who receive healthcare services
-- Multiple patients can belong to one customer (family members)

CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL CHECK (age > 0 AND age <= 150),
    gender VARCHAR(20) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    weight DECIMAL(5,2) CHECK (weight > 0 AND weight <= 500), -- in kg
    height DECIMAL(5,2) CHECK (height > 0 AND height <= 300), -- in cm
    blood_group VARCHAR(10) CHECK (blood_group IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-', 'UNKNOWN')),
    is_diabetic BOOLEAN DEFAULT FALSE,
    bp_status VARCHAR(20) DEFAULT 'NORMAL' CHECK (bp_status IN ('NORMAL', 'HIGH', 'LOW', 'UNKNOWN')),
    allergies TEXT,
    chronic_conditions TEXT,
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    relationship_to_customer VARCHAR(50) NOT NULL CHECK (relationship_to_customer IN ('SELF', 'PARENT', 'CHILD', 'SPOUSE', 'SIBLING', 'GRANDPARENT', 'GRANDCHILD', 'OTHER')),
    is_sensitive_data BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_patient_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes for better query performance
CREATE INDEX idx_patients_customer_id ON patients(customer_id);
CREATE INDEX idx_patients_is_active ON patients(is_active);
CREATE INDEX idx_patients_created_at ON patients(created_at);
CREATE INDEX idx_patients_customer_active ON patients(customer_id, is_active);

-- Comments for documentation
COMMENT ON TABLE patients IS 'Stores patient information for healthcare service recipients';
COMMENT ON COLUMN patients.customer_id IS 'Reference to the customer (user) who manages this patient';
COMMENT ON COLUMN patients.relationship_to_customer IS 'Relationship of patient to the customer (e.g., SELF, CHILD, PARENT)';
COMMENT ON COLUMN patients.is_sensitive_data IS 'Flag to mark patient data as extra sensitive requiring additional privacy measures';
COMMENT ON COLUMN patients.is_active IS 'Soft delete flag - FALSE means patient record is archived';

