import React, { useState } from 'react';
import { ChevronDownIcon, ChevronUpIcon } from '@heroicons/react/24/outline';

export const FAQ: React.FC = () => {
  const [openIndex, setOpenIndex] = useState<number | null>(null);

  const faqs = [
    {
      question: 'How do I get verified as a provider?',
      answer: 'Submit your professional documents (degree, license, experience certificates) through our secure portal. Our verification team reviews applications within 2-3 business days. You\'ll receive email notifications about your verification status.',
    },
    {
      question: 'When do I receive payments?',
      answer: 'Payments are processed automatically after each completed service. You can withdraw your earnings weekly through our secure payment gateway. All transactions are tracked in your provider dashboard.',
    },
    {
      question: 'Can I set my own availability?',
      answer: 'Absolutely! You have complete control over your schedule. Set your available hours, block dates for personal time, and accept or decline bookings based on your availability. The system respects your preferences.',
    },
    {
      question: 'What if I need to cancel a booking?',
      answer: 'You can cancel bookings up to 2 hours before the scheduled time without penalty. For last-minute cancellations, we work with you to find alternative solutions. We understand that emergencies happen.',
    },
    {
      question: 'How are patients matched to me?',
      answer: 'Our smart matching system considers your specialization, location, availability, and patient preferences. Patients can also specifically request you if they\'ve worked with you before. You\'ll receive booking requests to accept or decline.',
    },
    {
      question: 'What support is available for providers?',
      answer: 'We provide 24/7 support through phone, chat, and email. Our dedicated provider success team helps with onboarding, technical issues, and professional development. We also offer training sessions and webinars.',
    },
    {
      question: 'Are there any fees for providers?',
      answer: 'We charge a small commission (5-8%) only on completed bookings. There are no registration fees, monthly subscriptions, or hidden charges. You keep the majority of your earnings.',
    },
    {
      question: 'Can I work part-time?',
      answer: 'Yes! Many of our providers work part-time. You can set your availability for specific days/hours and accept as many or as few bookings as you want. Perfect for those balancing other commitments.',
    },
  ];

  const toggleFAQ = (index: number) => {
    setOpenIndex(openIndex === index ? null : index);
  };

  return (
    <section className="py-20 bg-gray-50">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
            Frequently Asked Questions
          </h2>
          <p className="text-xl text-gray-600">
            Everything you need to know about joining Lucknow Healthcare
          </p>
        </div>

        <div className="space-y-4">
          {faqs.map((faq, index) => (
            <div
              key={index}
              className="bg-white rounded-xl shadow-lg overflow-hidden animate-fade-in-up hover:shadow-xl transition-all duration-300"
              style={{ animationDelay: `${index * 0.1}s` }}
            >
              <button
                onClick={() => toggleFAQ(index)}
                className="w-full px-6 py-6 text-left flex items-center justify-between hover:bg-gray-50 transition-colors"
              >
                <h3 className="text-lg font-semibold text-gray-900 pr-4">
                  {faq.question}
                </h3>
                {openIndex === index ? (
                  <ChevronUpIcon className="w-6 h-6 text-blue-600 flex-shrink-0" />
                ) : (
                  <ChevronDownIcon className="w-6 h-6 text-gray-400 flex-shrink-0" />
                )}
              </button>
              
              {openIndex === index && (
                <div className="px-6 pb-6">
                  <p className="text-gray-600 leading-relaxed">
                    {faq.answer}
                  </p>
                </div>
              )}
            </div>
          ))}
        </div>

        {/* Contact Support */}
        <div className="mt-12 text-center">
          <div className="bg-blue-600 rounded-xl p-8 text-white">
            <h3 className="text-2xl font-bold mb-4">
              Still have questions?
            </h3>
            <p className="text-blue-100 mb-6">
              Our provider support team is here to help you get started
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <a
                href="tel:+919876543210"
                className="bg-white text-blue-600 font-bold px-6 py-3 rounded-lg hover:bg-gray-100 transition-colors"
              >
                📞 Call: +91 98765 43210
              </a>
              <a
                href="mailto:providers@lucknowhealthcare.com"
                className="bg-blue-500 text-white font-bold px-6 py-3 rounded-lg hover:bg-blue-400 transition-colors"
              >
                ✉️ Email Support
              </a>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
