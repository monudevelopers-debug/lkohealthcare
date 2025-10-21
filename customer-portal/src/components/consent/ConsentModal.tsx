import { useState } from 'react';
import { 
  XMarkIcon, 
  ShieldCheckIcon, 
  DocumentTextIcon, 
  LockClosedIcon, 
  ExclamationTriangleIcon 
} from '@heroicons/react/24/outline';
import type { ConsentType, RequiredConsent } from '../../types/patient.types';

interface ConsentModalProps {
  consents: RequiredConsent[];
  onAcceptAll: (acceptedConsents: ConsentType[]) => Promise<void>;
  onClose: () => void;
  isLoading?: boolean;
}

export function ConsentModal({ consents, onAcceptAll, onClose, isLoading }: ConsentModalProps) {
  const [acceptedConsents, setAcceptedConsents] = useState<Set<ConsentType>>(new Set());
  const [showDetails, setShowDetails] = useState<ConsentType | null>(null);

  const allAccepted = consents.every(c => acceptedConsents.has(c.consentType) || c.isAccepted);

  const handleToggleConsent = (consentType: ConsentType) => {
    const newAccepted = new Set(acceptedConsents);
    if (newAccepted.has(consentType)) {
      newAccepted.delete(consentType);
    } else {
      newAccepted.add(consentType);
    }
    setAcceptedConsents(newAccepted);
  };

  const handleAcceptAll = async () => {
    try {
      const consentsToAccept = Array.from(acceptedConsents);
      await onAcceptAll(consentsToAccept);
    } catch (error) {
      console.error('Error accepting consents:', error);
    }
  };

  const getConsentIcon = (type: ConsentType) => {
    switch (type) {
      case 'TERMS_AND_CONDITIONS':
        return DocumentTextIcon;
      case 'PRIVACY_POLICY':
        return ShieldCheckIcon;
      case 'MEDICAL_DATA_SHARING':
      case 'HIPAA_COMPLIANCE':
        return LockClosedIcon;
      default:
        return ExclamationTriangleIcon;
    }
  };

  const getConsentContent = (type: ConsentType): string => {
    switch (type) {
      case 'TERMS_AND_CONDITIONS':
        return `
By accepting these Terms and Conditions, you agree to:

1. Use Lucknow Healthcare services responsibly and lawfully
2. Provide accurate and truthful information
3. Maintain confidentiality of your account credentials
4. Follow healthcare provider instructions and recommendations
5. Pay for services as per agreed terms
6. Respect healthcare providers' time and schedules
7. Notify us of cancellations within the specified time frame
8. Accept liability for any misuse of services

We reserve the right to:
- Suspend or terminate accounts for policy violations
- Modify services and terms with prior notice
- Share information with authorities when legally required

Last updated: January 2025
        `;
      
      case 'PRIVACY_POLICY':
        return `
Your Privacy Matters to Us

We collect and process:
- Personal information (name, contact details, address)
- Medical information (health conditions, allergies, medications)
- Payment information (transaction records)
- Usage data (app interactions, preferences)

How we use your data:
- To provide and improve healthcare services
- To match you with appropriate healthcare providers
- To process payments and generate invoices
- To send important updates and reminders
- To comply with legal and regulatory requirements

Data protection:
- All data is encrypted in transit and at rest
- Medical data is only shared with assigned providers
- We never sell your personal information
- You can request data deletion at any time

Your rights:
- Access your data
- Correct inaccurate information
- Request data deletion
- Export your data
- Opt-out of marketing communications

Last updated: January 2025
        `;
      
      case 'MEDICAL_DATA_SHARING':
        return `
Medical Data Sharing Consent

By accepting, you authorize us to:

1. Share patient medical information with assigned healthcare providers
2. Information shared includes:
   - Patient demographics (name, age, gender)
   - Medical history (conditions, allergies, medications)
   - Uploaded medical documents (prescriptions, reports)
   - Emergency contact information

3. Data sharing scope:
   - ONLY with providers assigned to your specific booking
   - ONLY for the duration of active service
   - Automatically revoked after service completion

4. Your control:
   - You can mark specific patient data as "sensitive"
   - You can revoke consent at any time
   - You can view who accessed what data and when

5. Provider obligations:
   - Providers are legally bound to maintain confidentiality
   - Data cannot be shared with third parties
   - Data must be used solely for treatment purposes

Last updated: January 2025
        `;
      
      case 'HIPAA_COMPLIANCE':
        return `
HIPAA-Like Medical Data Protection

We implement healthcare industry-standard protections:

1. Administrative Safeguards:
   - Access controls and user authentication
   - Regular security audits and assessments
   - Employee training on data protection
   - Incident response procedures

2. Physical Safeguards:
   - Secure data centers with restricted access
   - Encrypted backups and redundancy
   - Device and media security controls

3. Technical Safeguards:
   - End-to-end encryption
   - Secure transmission protocols (HTTPS, TLS)
   - Audit logging and monitoring
   - Automatic session timeouts

4. Your Protected Health Information (PHI):
   - Medical history and conditions
   - Treatment information
   - Payment and billing data
   - Any health-related identifiable information

5. Your Rights:
   - Right to access your medical records
   - Right to request corrections
   - Right to accounting of disclosures
   - Right to request restrictions on sharing
   - Right to file complaints

6. Breach Notification:
   - We will notify you within 60 days of any data breach
   - You will receive information about what was exposed
   - We will provide steps to protect yourself

Last updated: January 2025
        `;
      
      default:
        return 'Consent details';
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Privacy & Consent</h2>
            <p className="text-gray-600 mt-1">Please review and accept to continue</p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {showDetails ? (
            // Show detailed consent text
            <div>
              <button
                onClick={() => setShowDetails(null)}
                className="text-blue-600 hover:text-blue-700 mb-4 text-sm font-medium"
              >
                ← Back to all consents
              </button>
              <div className="prose prose-sm max-w-none">
                <h3 className="text-lg font-semibold mb-4">
                  {consents.find(c => c.consentType === showDetails)?.title}
                </h3>
                <pre className="whitespace-pre-wrap text-sm text-gray-700 font-sans">
                  {getConsentContent(showDetails)}
                </pre>
              </div>
            </div>
          ) : (
            // Show consent checklist
            <div className="space-y-4">
              {consents.map((consent) => {
                const Icon = getConsentIcon(consent.consentType);
                const isAccepted = acceptedConsents.has(consent.consentType) || consent.isAccepted;
                const isAlreadyAccepted = consent.isAccepted;

                return (
                  <div
                    key={consent.consentType}
                    className={`border rounded-lg p-4 transition-colors ${
                      isAccepted ? 'border-green-300 bg-green-50' : 'border-gray-200 hover:border-blue-300'
                    }`}
                  >
                    <div className="flex items-start space-x-3">
                      <div className="flex-shrink-0">
                        {isAlreadyAccepted ? (
                          <div className="w-5 h-5 bg-green-500 rounded-full flex items-center justify-center">
                            <svg className="w-3 h-3 text-white" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
                            </svg>
                          </div>
                        ) : (
                          <input
                            type="checkbox"
                            checked={isAccepted}
                            onChange={() => handleToggleConsent(consent.consentType)}
                            className="w-5 h-5 text-blue-600 rounded border-gray-300 focus:ring-2 focus:ring-blue-500"
                            disabled={isAlreadyAccepted}
                          />
                        )}
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center space-x-2 mb-1">
                          <Icon className="w-5 h-5 text-blue-600" />
                          <h3 className="font-semibold text-gray-900">{consent.title}</h3>
                          {isAlreadyAccepted && (
                            <span className="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded">
                              Accepted
                            </span>
                          )}
                        </div>
                        <p className="text-sm text-gray-600 mb-2">{consent.description}</p>
                        <button
                          onClick={() => setShowDetails(consent.consentType)}
                          className="text-sm text-blue-600 hover:text-blue-700 font-medium"
                        >
                          Read full text →
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>

        {/* Footer */}
        {!showDetails && (
          <div className="border-t p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-2 text-sm text-gray-600">
                <ExclamationTriangleIcon className="w-4 h-4" />
                <span>
                  {consents.filter(c => !c.isAccepted).length} consent(s) required
                </span>
              </div>
              <div className="flex items-center space-x-3">
                <button
                  onClick={onClose}
                  className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
                  disabled={isLoading}
                >
                  Cancel
                </button>
                <button
                  onClick={handleAcceptAll}
                  disabled={!allAccepted || isLoading || acceptedConsents.size === 0}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center space-x-2"
                >
                  {isLoading && (
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  )}
                  <span>Accept & Continue</span>
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

