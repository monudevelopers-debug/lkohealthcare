import { useState } from 'react';
import { XMarkIcon, CreditCardIcon, BanknotesIcon, DevicePhoneMobileIcon, CheckCircleIcon } from '@heroicons/react/24/outline';
import { paymentApi } from '../../lib/api/payment.api';

interface PaymentModalProps {
  bookingId: string;
  amount: number;
  onSuccess: () => void;
  onClose: () => void;
}

export function PaymentModal({ bookingId, amount, onSuccess, onClose }: PaymentModalProps) {
  const [paymentMethod, setPaymentMethod] = useState<'PAYTM' | 'CASH' | 'UPI' | 'CARD'>('PAYTM');
  const [paymentTiming, setPaymentTiming] = useState<'ADVANCE' | 'POST_SERVICE'>('ADVANCE');
  const [isProcessing, setIsProcessing] = useState(false);
  const [error, setError] = useState('');

  const paymentMethods = [
    { 
      value: 'PAYTM' as const, 
      label: 'Paytm', 
      icon: CreditCardIcon,
      description: 'Pay securely via Paytm',
      available: true 
    },
    { 
      value: 'UPI' as const, 
      label: 'UPI', 
      icon: DevicePhoneMobileIcon,
      description: 'Pay via UPI',
      available: true 
    },
    { 
      value: 'CARD' as const, 
      label: 'Credit/Debit Card', 
      icon: CreditCardIcon,
      description: 'Pay via Card',
      available: true 
    },
    { 
      value: 'CASH' as const, 
      label: 'Cash on Service', 
      icon: BanknotesIcon,
      description: 'Pay after service completion',
      available: true 
    },
  ];

  const handlePayment = async () => {
    try {
      setIsProcessing(true);
      setError('');

      const response = await paymentApi.initiatePayment({
        bookingId,
        amount,
        paymentMethod,
        paymentTiming,
      });

      console.log('Payment initiation response:', response);

      // Handle different payment methods
      if (paymentMethod === 'CASH') {
        // Cash payment - just confirm booking
        alert('Booking confirmed! Please keep exact cash ready for the provider.');
        onSuccess();
      } else if (paymentMethod === 'PAYTM' || paymentMethod === 'UPI' || paymentMethod === 'CARD') {
        // Check if we're in DUMMY mode or real Paytm
        if (response.gatewayMode === 'DUMMY') {
          // Dummy gateway - simulate payment
          setTimeout(() => {
            if (response.status === 'SUCCESS') {
              alert('✅ Payment successful! (Test mode)');
              onSuccess();
            } else {
              setError('Payment failed in test mode. Please try again.');
              setIsProcessing(false);
            }
          }, 2000);
        } else if (response.gatewayMode === 'PAYTM' && response.paytmParams) {
          // Real Paytm - redirect to payment gateway
          const form = document.createElement('form');
          form.method = 'POST';
          form.action = response.paytmUrl || 'https://securegw-stage.paytm.in/theia/api/v1/showPaymentPage';
          
          Object.entries(response.paytmParams).forEach(([key, value]) => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = String(value);
            form.appendChild(input);
          });

          document.body.appendChild(form);
          form.submit();
        } else {
          // Fallback - mark as pending and proceed
          alert('Payment initiated. Transaction ID: ' + response.transactionId);
          onSuccess();
        }
      } else {
        // Unknown payment method
        onSuccess();
      }
    } catch (err: any) {
      console.error('Payment error:', err);
      setError(err.response?.data?.message || err.message || 'Payment initiation failed');
      setIsProcessing(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="sticky top-0 bg-white border-b px-6 py-4 flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Complete Payment</h2>
            <p className="text-sm text-gray-500 mt-1">Choose your preferred payment method</p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-6">
          {/* Amount */}
          <div className="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-lg p-6 border border-blue-200">
            <p className="text-sm text-blue-600 font-medium uppercase tracking-wide mb-1">Total Amount</p>
            <p className="text-4xl font-bold text-gray-900">₹{amount.toFixed(2)}</p>
          </div>

          {/* Payment Timing */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3">
              Payment Timing <span className="text-red-500">*</span>
            </label>
            <div className="grid grid-cols-2 gap-4">
              <button
                type="button"
                onClick={() => setPaymentTiming('ADVANCE')}
                className={`p-4 rounded-lg border-2 transition-all ${
                  paymentTiming === 'ADVANCE'
                    ? 'border-blue-500 bg-blue-50 ring-2 ring-blue-200'
                    : 'border-gray-200 hover:border-blue-300'
                }`}
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="font-semibold text-gray-900">Pay Now</span>
                  {paymentTiming === 'ADVANCE' && (
                    <CheckCircleIcon className="w-5 h-5 text-blue-600" />
                  )}
                </div>
                <p className="text-xs text-gray-600">Secure advance payment</p>
              </button>
              <button
                type="button"
                onClick={() => setPaymentTiming('POST_SERVICE')}
                className={`p-4 rounded-lg border-2 transition-all ${
                  paymentTiming === 'POST_SERVICE'
                    ? 'border-blue-500 bg-blue-50 ring-2 ring-blue-200'
                    : 'border-gray-200 hover:border-blue-300'
                }`}
              >
                <div className="flex items-center justify-between mb-2">
                  <span className="font-semibold text-gray-900">Pay After Service</span>
                  {paymentTiming === 'POST_SERVICE' && (
                    <CheckCircleIcon className="w-5 h-5 text-blue-600" />
                  )}
                </div>
                <p className="text-xs text-gray-600">Pay upon completion</p>
              </button>
            </div>
          </div>

          {/* Payment Method */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-3">
              Payment Method <span className="text-red-500">*</span>
            </label>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {paymentMethods.map((method) => {
                const Icon = method.icon;
                return (
                  <button
                    key={method.value}
                    type="button"
                    onClick={() => setPaymentMethod(method.value)}
                    disabled={!method.available}
                    className={`p-4 rounded-lg border-2 transition-all text-left ${
                      paymentMethod === method.value
                        ? 'border-blue-500 bg-blue-50 ring-2 ring-blue-200'
                        : method.available
                        ? 'border-gray-200 hover:border-blue-300'
                        : 'border-gray-100 bg-gray-50 opacity-50 cursor-not-allowed'
                    }`}
                  >
                    <div className="flex items-start gap-3">
                      <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                        paymentMethod === method.value ? 'bg-blue-100' : 'bg-gray-100'
                      }`}>
                        <Icon className={`w-5 h-5 ${
                          paymentMethod === method.value ? 'text-blue-600' : 'text-gray-500'
                        }`} />
                      </div>
                      <div className="flex-1">
                        <div className="flex items-center justify-between mb-1">
                          <span className="font-semibold text-gray-900">{method.label}</span>
                          {paymentMethod === method.value && (
                            <CheckCircleIcon className="w-5 h-5 text-blue-600" />
                          )}
                        </div>
                        <p className="text-xs text-gray-600">{method.description}</p>
                      </div>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Error */}
          {error && (
            <div className="p-4 bg-red-50 border border-red-200 rounded-lg">
              <p className="text-sm text-red-700">{error}</p>
            </div>
          )}

          {/* Important Note for Cash Payment */}
          {paymentMethod === 'CASH' && (
            <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
              <p className="text-sm text-yellow-800">
                <strong>Note:</strong> Please keep exact cash ready for the provider. 
                Payment should be made after service completion.
              </p>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="sticky bottom-0 bg-gray-50 border-t px-6 py-4 flex items-center justify-between">
          <button
            onClick={onClose}
            className="px-6 py-2.5 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors font-medium"
          >
            Cancel
          </button>
          <button
            onClick={handlePayment}
            disabled={isProcessing}
            className="px-8 py-2.5 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-lg hover:from-blue-700 hover:to-indigo-700 transition-all shadow-sm hover:shadow-md font-semibold disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
          >
            {isProcessing ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                Processing...
              </>
            ) : (
              <>
                {paymentTiming === 'ADVANCE' ? 'Proceed to Payment' : 'Confirm Booking'}
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
}

