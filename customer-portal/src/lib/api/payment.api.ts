import apiClient from './client';

export interface PaymentInitiateRequest {
  bookingId: string;
  amount: number;
  paymentMethod: 'PAYTM' | 'CASH' | 'UPI' | 'CARD';
  paymentTiming: 'ADVANCE' | 'POST_SERVICE';
}

export interface PaymentInitiateResponse {
  transactionId: string;
  orderId: string;
  status: string;
  amount: number;
  currency: string;
  message: string;
  gatewayMode: 'DUMMY' | 'PAYTM' | 'RAZORPAY' | 'STRIPE';
  paytmParams?: {
    MID: string;
    ORDER_ID: string;
    CUST_ID: string;
    INDUSTRY_TYPE_ID: string;
    CHANNEL_ID: string;
    TXN_AMOUNT: string;
    WEBSITE: string;
    CALLBACK_URL: string;
    CHECKSUMHASH: string;
  };
  paytmUrl?: string;
}

export interface PaymentStatus {
  paymentId: string;
  status: 'PENDING' | 'PROCESSING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';
  transactionId?: string;
  paidAt?: string;
  failureReason?: string;
}

/**
 * Payment API Client
 */
export const paymentApi = {
  /**
   * Initiate a payment
   */
  async initiatePayment(request: PaymentInitiateRequest): Promise<PaymentInitiateResponse> {
    const response = await apiClient.post('/payments/initiate', request);
    return response.data;
  },

  /**
   * Get payment status
   */
  async getPaymentStatus(paymentId: string): Promise<PaymentStatus> {
    const response = await apiClient.get(`/payments/${paymentId}/status`);
    return response.data;
  },

  /**
   * Handle payment callback
   */
  async handleCallback(paymentId: string, callbackData: any): Promise<PaymentStatus> {
    const response = await apiClient.post(`/payments/${paymentId}/callback`, callbackData);
    return response.data;
  },

  /**
   * Get payment history for customer
   */
  async getPaymentHistory(page = 0, size = 20): Promise<any> {
    const response = await apiClient.get(`/payments/history?page=${page}&size=${size}`);
    return response.data;
  },

  /**
   * Download invoice
   */
  async downloadInvoice(paymentId: string): Promise<Blob> {
    const response = await apiClient.get(`/payments/${paymentId}/invoice`, {
      responseType: 'blob',
    });
    return response.data;
  },

  /**
   * Request refund
   */
  async requestRefund(paymentId: string, reason: string): Promise<any> {
    const response = await apiClient.post(`/payments/${paymentId}/refund`, { reason });
    return response.data;
  },
};

