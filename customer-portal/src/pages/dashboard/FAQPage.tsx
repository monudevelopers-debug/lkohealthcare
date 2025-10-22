import { useState } from 'react';
import { ChevronDownIcon, QuestionMarkCircleIcon } from '@heroicons/react/24/outline';

export function FAQPage() {
  const [openIndex, setOpenIndex] = useState<number | null>(null);

  const faqs = [
    {
      question: 'What areas do you serve in Lucknow?',
      answer: 'We provide healthcare services across all areas of Lucknow, including Indira Nagar, Gomti Nagar, Hazratganj, Alambagh, and surrounding regions. Our office is located behind Meena Market, Near Lekhraj Metro Station, Indira Nagar.'
    },
    {
      question: 'How do I book a healthcare service?',
      answer: 'Simply create an account, add patient details, select the service you need, choose a convenient date and time, and complete the booking. You can pay in advance or after service completion.'
    },
    {
      question: 'What payment methods do you accept?',
      answer: 'We accept Paytm, UPI, Credit/Debit Cards, and Cash on Service. You can choose to pay in advance or after the service is completed.'
    },
    {
      question: 'Can I cancel or reschedule a booking?',
      answer: 'Yes, you can cancel bookings before they are confirmed by a provider. Cancellation and refund policies apply based on the timing of cancellation. Please check your booking details for specific policies.'
    },
    {
      question: 'How are providers assigned to my booking?',
      answer: 'Our admin team assigns the best available provider based on the service type, provider availability, qualifications, and proximity to your location. You will be notified once a provider is assigned.'
    },
    {
      question: 'What if I need to add multiple family members?',
      answer: 'You can add multiple patients (family members) in the "My Patients" section. For each booking, select which patient needs the service.'
    },
    {
      question: 'Is my medical data safe?',
      answer: 'Absolutely! All patient medical information is encrypted and stored securely. We follow HIPAA-like compliance standards. Your data is only shared with assigned providers during active bookings.'
    },
    {
      question: 'What happens if the provider is late?',
      answer: 'We strive for punctuality. Once a provider is assigned, you can see their estimated arrival time. If there are any delays, the provider will notify you directly.'
    },
    {
      question: 'Can I request a specific provider?',
      answer: 'Currently, providers are automatically assigned based on availability and expertise. However, you can rate providers after service completion, which helps us improve our assignments.'
    },
    {
      question: 'How do refunds work?',
      answer: 'Refunds are processed based on our cancellation policy. If you paid in advance and cancel within the allowed timeframe, refunds are initiated within 5-7 business days to your original payment method.'
    },
    {
      question: 'Do you provide emergency services?',
      answer: 'For medical emergencies, please call 108 (ambulance) or visit the nearest hospital. Our services are for non-emergency, scheduled healthcare needs like nursing, physiotherapy, and home care.'
    },
    {
      question: 'Can I download invoices for my payments?',
      answer: 'Yes! All successful payments generate invoices that you can download from the Payment History page. Invoices include all transaction details for your records.'
    }
  ];

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
            <QuestionMarkCircleIcon className="w-8 h-8 text-blue-600" />
          </div>
          <h1 className="text-4xl font-bold text-gray-900 mb-4">Frequently Asked Questions</h1>
          <p className="text-xl text-gray-600">Find answers to common questions about our services</p>
        </div>

        {/* FAQ List */}
        <div className="space-y-4">
          {faqs.map((faq, index) => (
            <div
              key={index}
              className="bg-white rounded-lg shadow-sm overflow-hidden transition-all duration-200"
            >
              <button
                onClick={() => setOpenIndex(openIndex === index ? null : index)}
                className="w-full px-6 py-4 flex items-center justify-between text-left hover:bg-gray-50 transition-colors"
              >
                <span className="font-semibold text-gray-900 pr-4">{faq.question}</span>
                <ChevronDownIcon
                  className={`w-5 h-5 text-gray-500 flex-shrink-0 transition-transform duration-200 ${
                    openIndex === index ? 'transform rotate-180' : ''
                  }`}
                />
              </button>
              {openIndex === index && (
                <div className="px-6 py-4 bg-gray-50 border-t border-gray-100">
                  <p className="text-gray-700 leading-relaxed">{faq.answer}</p>
                </div>
              )}
            </div>
          ))}
        </div>

        {/* Contact CTA */}
        <div className="mt-12 bg-gradient-to-r from-blue-600 to-indigo-600 rounded-xl p-8 text-center text-white">
          <h3 className="text-2xl font-bold mb-2">Still have questions?</h3>
          <p className="text-blue-100 mb-6">Our support team is here to help you</p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
            <a
              href="tel:+918400001034"
              className="inline-flex items-center px-6 py-3 bg-white text-blue-600 rounded-lg hover:bg-gray-100 transition-colors font-semibold"
            >
              📞 Call: +91-8400001034
            </a>
            <a
              href="mailto:lucknow.services2014@gmail.com"
              className="inline-flex items-center px-6 py-3 bg-blue-700 text-white rounded-lg hover:bg-blue-800 transition-colors font-semibold"
            >
              ✉️ Email Us
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}

