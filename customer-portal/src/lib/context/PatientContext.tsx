import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { Patient } from '../../types/patient.types';

interface PatientContextType {
  selectedPatient: Patient | null;
  setSelectedPatient: (patient: Patient | null) => void;
  clearSelectedPatient: () => void;
}

const PatientContext = createContext<PatientContextType | undefined>(undefined);

export function PatientProvider({ children }: { children: ReactNode }) {
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(() => {
    // Load from sessionStorage on mount
    const saved = sessionStorage.getItem('selected_patient');
    return saved ? JSON.parse(saved) : null;
  });

  // Persist to sessionStorage whenever it changes
  useEffect(() => {
    if (selectedPatient) {
      sessionStorage.setItem('selected_patient', JSON.stringify(selectedPatient));
    } else {
      sessionStorage.removeItem('selected_patient');
    }
  }, [selectedPatient]);

  const clearSelectedPatient = () => {
    setSelectedPatient(null);
    sessionStorage.removeItem('selected_patient');
  };

  return (
    <PatientContext.Provider value={{ selectedPatient, setSelectedPatient, clearSelectedPatient }}>
      {children}
    </PatientContext.Provider>
  );
}

export function usePatient() {
  const context = useContext(PatientContext);
  if (context === undefined) {
    throw new Error('usePatient must be used within a PatientProvider');
  }
  return context;
}

