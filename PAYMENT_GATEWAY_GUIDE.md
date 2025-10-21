# 💳 Payment Gateway Implementation Guide

## 🧪 Option 1: Dummy Payment Gateway (For Development)

### Overview
For development and testing, we'll implement a **simulated payment gateway** that mimics real payment flows without actual money transfer.

### Features
- ✅ Payment initiation
- ✅ Success/Failure simulation
- ✅ Transaction ID generation
- ✅ Payment verification
- ✅ Refund simulation
- ✅ Invoice generation

### Implementation

#### 1. Backend Configuration
```yaml
# application.yml
payment:
  gateway:
    mode: DUMMY # Change to RAZORPAY in production
    dummy:
      success-rate: 90 # 90% payments succeed, 10% fail (for testing)
      processing-time: 3000 # 3 seconds delay to simulate real gateway
```

#### 2. Payment Service Interface
```java
public interface PaymentGateway {
    PaymentInitiationResponse initiatePayment(PaymentRequest request);
    PaymentVerificationResponse verifyPayment(String transactionId);
    RefundResponse processRefund(String transactionId, Double amount);
}
```

#### 3. Dummy Payment Gateway Implementation
```java
@Service
@ConditionalOnProperty(name = "payment.gateway.mode", havingValue = "DUMMY")
public class DummyPaymentGateway implements PaymentGateway {
    
    @Value("${payment.gateway.dummy.success-rate:90}")
    private int successRate;
    
    @Value("${payment.gateway.dummy.processing-time:3000}")
    private long processingTime;
    
    private Map<String, PaymentTransaction> transactions = new ConcurrentHashMap<>();
    
    @Override
    public PaymentInitiationResponse initiatePayment(PaymentRequest request) {
        // Generate dummy transaction ID
        String transactionId = "DUMMY_TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Simulate processing delay
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Randomly succeed or fail based on success rate
        boolean isSuccess = new Random().nextInt(100) < successRate;
        
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(transactionId);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(isSuccess ? "SUCCESS" : "FAILED");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setPaymentMethod(request.getPaymentMethod());
        
        transactions.put(transactionId, transaction);
        
        return PaymentInitiationResponse.builder()
            .transactionId(transactionId)
            .status(isSuccess ? "SUCCESS" : "FAILED")
            .amount(request.getAmount())
            .message(isSuccess ? "Payment successful" : "Payment failed - Insufficient funds")
            .timestamp(LocalDateTime.now())
            .build();
    }
    
    @Override
    public PaymentVerificationResponse verifyPayment(String transactionId) {
        PaymentTransaction transaction = transactions.get(transactionId);
        
        if (transaction == null) {
            return PaymentVerificationResponse.builder()
                .transactionId(transactionId)
                .status("NOT_FOUND")
                .message("Transaction not found")
                .build();
        }
        
        return PaymentVerificationResponse.builder()
            .transactionId(transactionId)
            .status(transaction.getStatus())
            .amount(transaction.getAmount())
            .paymentMethod(transaction.getPaymentMethod())
            .timestamp(transaction.getTimestamp())
            .build();
    }
    
    @Override
    public RefundResponse processRefund(String transactionId, Double amount) {
        PaymentTransaction transaction = transactions.get(transactionId);
        
        if (transaction == null || !transaction.getStatus().equals("SUCCESS")) {
            return RefundResponse.builder()
                .success(false)
                .message("Cannot refund - Transaction not found or not successful")
                .build();
        }
        
        // Simulate refund processing
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String refundId = "DUMMY_REFUND_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        return RefundResponse.builder()
            .success(true)
            .refundId(refundId)
            .amount(amount)
            .message("Refund processed successfully")
            .processedAt(LocalDateTime.now())
            .build();
    }
}
```

#### 4. Frontend - Dummy Payment Modal Component
```tsx
// DummyPaymentModal.tsx
import React, { useState } from 'react';
import { CreditCard, CheckCircle, XCircle } from 'lucide-react';

interface DummyPaymentModalProps {
  amount: number;
  onSuccess: (transactionId: string) => void;
  onFailure: (error: string) => void;
  onClose: () => void;
}

export const DummyPaymentModal: React.FC<DummyPaymentModalProps> = ({
  amount,
  onSuccess,
  onFailure,
  onClose,
}) => {
  const [processing, setProcessing] = useState(false);
  const [cardNumber, setCardNumber] = useState('');
  const [cvv, setCvv] = useState('');
  const [expiry, setExpiry] = useState('');

  const handlePayment = async () => {
    setProcessing(true);

    try {
      const response = await fetch('/api/payments/initiate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          amount,
          paymentMethod: 'CARD',
          cardNumber,
          cvv,
          expiry,
        }),
      });

      const data = await response.json();

      // Simulate 3 second processing
      await new Promise((resolve) => setTimeout(resolve, 3000));

      if (data.status === 'SUCCESS') {
        onSuccess(data.transactionId);
      } else {
        onFailure(data.message);
      }
    } catch (error) {
      onFailure('Payment processing failed');
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 w-full max-w-md">
        <div className="mb-4">
          <h3 className="text-lg font-semibold">Dummy Payment Gateway</h3>
          <p className="text-sm text-yellow-600">⚠️ Development Mode - No real money transfer</p>
        </div>

        <div className="mb-4 p-4 bg-gray-50 rounded">
          <div className="flex justify-between items-center">
            <span className="text-gray-600">Amount to Pay:</span>
            <span className="text-2xl font-bold">₹{amount.toFixed(2)}</span>
          </div>
        </div>

        <div className="space-y-4 mb-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Card Number
            </label>
            <input
              type="text"
              placeholder="4111 1111 1111 1111"
              value={cardNumber}
              onChange={(e) => setCardNumber(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
              maxLength={19}
            />
            <p className="text-xs text-gray-500 mt-1">
              Use 4111111111111111 for success, 4000000000000002 for failure
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Expiry
              </label>
              <input
                type="text"
                placeholder="MM/YY"
                value={expiry}
                onChange={(e) => setExpiry(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md"
                maxLength={5}
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                CVV
              </label>
              <input
                type="text"
                placeholder="123"
                value={cvv}
                onChange={(e) => setCvv(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md"
                maxLength={3}
              />
            </div>
          </div>
        </div>

        <div className="flex gap-3">
          <button
            onClick={onClose}
            disabled={processing}
            className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50 disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            onClick={handlePayment}
            disabled={processing || !cardNumber || !cvv || !expiry}
            className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 flex items-center justify-center gap-2"
          >
            {processing ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                Processing...
              </>
            ) : (
              <>
                <CreditCard className="w-4 h-4" />
                Pay ₹{amount.toFixed(2)}
              </>
            )}
          </button>
        </div>

        <div className="mt-4 p-3 bg-blue-50 rounded text-sm">
          <p className="font-medium text-blue-900 mb-1">Test Cards:</p>
          <ul className="text-blue-700 space-y-1 text-xs">
            <li>✅ Success: 4111 1111 1111 1111</li>
            <li>❌ Failure: 4000 0000 0000 0002</li>
            <li>📝 Expiry: Any future date (e.g., 12/25)</li>
            <li>🔒 CVV: Any 3 digits (e.g., 123)</li>
          </ul>
        </div>
      </div>
    </div>
  );
};
```

---

## 💰 Option 2: Razorpay Integration (For Production)

### Step 1: Create Razorpay Account

#### 🔗 Sign Up
1. Go to: https://razorpay.com/
2. Click "Sign Up" (Top right)
3. Choose "Business Account"
4. Enter details:
   - Email: your-business@email.com
   - Mobile: Your business number
   - Create password

#### 📋 KYC Requirements
After signup, you'll need:

**1. Business Details:**
- Business name: Lucknow Healthcare Services
- Business type: Healthcare Services / Medical Services
- Business model: Service Marketplace
- Website: (your domain if you have one)

**2. Business Documents:**
- **PAN Card** (Mandatory)
  - Individual PAN (for proprietorship)
  - OR Company PAN (for Pvt Ltd/LLP)
- **Business Proof** (Choose one):
  - GST Certificate (Recommended)
  - Shop & Establishment Certificate
  - MSME/Udyog Aadhar Certificate
  - Business Registration Certificate

**3. Bank Account:**
- Business bank account number
- IFSC code
- Bank statement (last 3 months)
- Cancelled cheque

**4. Identity Proof (Authorized Signatory):**
- Aadhar Card
- PAN Card
- Passport
- Driving License

#### ⏱️ Approval Timeline
- **Test Mode:** Instant (immediately after signup)
- **Live Mode:** 2-3 business days after KYC submission

### Step 2: Get API Keys

#### Test Mode (Immediate Use)
```
1. Login to Razorpay Dashboard
2. Go to Settings → API Keys
3. Click "Generate Test Key"
4. Copy:
   - Key ID: rzp_test_XXXXXXXXXXXX
   - Key Secret: XXXXXXXXXXXXXXXXXXXX
```

#### Live Mode (After Approval)
```
1. Complete KYC verification
2. Wait for approval email
3. Go to Settings → API Keys
4. Switch to "Live Mode"
5. Click "Generate Live Key"
6. Copy:
   - Key ID: rzp_live_XXXXXXXXXXXX
   - Key Secret: XXXXXXXXXXXXXXXXXXXX
```

### Step 3: Backend Implementation

#### Configuration
```yaml
# application.yml
payment:
  gateway:
    mode: RAZORPAY
    razorpay:
      key-id: ${RAZORPAY_KEY_ID}
      key-secret: ${RAZORPAY_KEY_SECRET}
      webhook-secret: ${RAZORPAY_WEBHOOK_SECRET}
```

#### Maven Dependency
```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.razorpay</groupId>
    <artifactId>razorpay-java</artifactId>
    <version>1.4.6</version>
</dependency>
```

#### Service Implementation
```java
@Service
@ConditionalOnProperty(name = "payment.gateway.mode", havingValue = "RAZORPAY")
public class RazorpayPaymentGateway implements PaymentGateway {
    
    @Value("${payment.gateway.razorpay.key-id}")
    private String keyId;
    
    @Value("${payment.gateway.razorpay.key-secret}")
    private String keySecret;
    
    private RazorpayClient razorpayClient;
    
    @PostConstruct
    public void init() throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }
    
    @Override
    public PaymentInitiationResponse initiatePayment(PaymentRequest request) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", request.getAmount() * 100); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "BOOKING_" + request.getBookingId());
            
            JSONObject notes = new JSONObject();
            notes.put("booking_id", request.getBookingId().toString());
            notes.put("customer_id", request.getCustomerId().toString());
            orderRequest.put("notes", notes);
            
            Order order = razorpayClient.orders.create(orderRequest);
            
            return PaymentInitiationResponse.builder()
                .orderId(order.get("id"))
                .amount(request.getAmount())
                .currency("INR")
                .keyId(keyId)
                .status("CREATED")
                .build();
                
        } catch (RazorpayException e) {
            throw new PaymentException("Failed to initiate payment", e);
        }
    }
    
    @Override
    public PaymentVerificationResponse verifyPayment(String razorpayOrderId, 
                                                     String razorpayPaymentId, 
                                                     String razorpaySignature) {
        try {
            // Verify signature
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", razorpayOrderId);
            options.put("razorpay_payment_id", razorpayPaymentId);
            options.put("razorpay_signature", razorpaySignature);
            
            boolean isValidSignature = Utils.verifyPaymentSignature(options, keySecret);
            
            if (isValidSignature) {
                Payment payment = razorpayClient.payments.fetch(razorpayPaymentId);
                
                return PaymentVerificationResponse.builder()
                    .transactionId(razorpayPaymentId)
                    .orderId(razorpayOrderId)
                    .status(payment.get("status").toString().toUpperCase())
                    .amount(Double.valueOf(payment.get("amount").toString()) / 100)
                    .paymentMethod(payment.get("method"))
                    .isVerified(true)
                    .build();
            } else {
                return PaymentVerificationResponse.builder()
                    .status("VERIFICATION_FAILED")
                    .isVerified(false)
                    .message("Invalid payment signature")
                    .build();
            }
            
        } catch (RazorpayException e) {
            throw new PaymentException("Failed to verify payment", e);
        }
    }
    
    @Override
    public RefundResponse processRefund(String paymentId, Double amount) {
        try {
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amount * 100); // Amount in paise
            
            Payment payment = razorpayClient.payments.fetch(paymentId);
            Refund refund = payment.refund(refundRequest);
            
            return RefundResponse.builder()
                .success(true)
                .refundId(refund.get("id"))
                .amount(amount)
                .status(refund.get("status"))
                .processedAt(LocalDateTime.now())
                .build();
                
        } catch (RazorpayException e) {
            throw new PaymentException("Failed to process refund", e);
        }
    }
}
```

### Step 4: Frontend Implementation

#### Razorpay Checkout Script
```tsx
// useRazorpay hook
import { useEffect } from 'react';

export const useRazorpay = () => {
  useEffect(() => {
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    document.body.appendChild(script);

    return () => {
      document.body.removeChild(script);
    };
  }, []);
};
```

#### Payment Component
```tsx
// RazorpayCheckout.tsx
import React from 'react';
import { useRazorpay } from './useRazorpay';

interface RazorpayCheckoutProps {
  amount: number;
  bookingId: string;
  onSuccess: (response: any) => void;
  onFailure: (error: any) => void;
}

export const RazorpayCheckout: React.FC<RazorpayCheckoutProps> = ({
  amount,
  bookingId,
  onSuccess,
  onFailure,
}) => {
  useRazorpay();

  const handlePayment = async () => {
    try {
      // Step 1: Create order on backend
      const response = await fetch('/api/payments/initiate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({ amount, bookingId }),
      });

      const { orderId, keyId, currency } = await response.json();

      // Step 2: Open Razorpay checkout
      const options = {
        key: keyId,
        amount: amount * 100, // Amount in paise
        currency: currency,
        name: 'Lucknow Healthcare',
        description: 'Healthcare Service Payment',
        order_id: orderId,
        handler: async (response: any) => {
          // Step 3: Verify payment on backend
          const verifyResponse = await fetch('/api/payments/verify', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${localStorage.getItem('token')}`,
            },
            body: JSON.stringify({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            }),
          });

          const verifyData = await verifyResponse.json();
          
          if (verifyData.isVerified) {
            onSuccess(verifyData);
          } else {
            onFailure('Payment verification failed');
          }
        },
        prefill: {
          name: localStorage.getItem('userName') || '',
          email: localStorage.getItem('userEmail') || '',
          contact: localStorage.getItem('userPhone') || '',
        },
        theme: {
          color: '#3B82F6',
        },
        modal: {
          ondismiss: () => {
            onFailure('Payment cancelled by user');
          },
        },
      };

      const razorpay = new (window as any).Razorpay(options);
      razorpay.open();
    } catch (error) {
      onFailure(error);
    }
  };

  return (
    <button
      onClick={handlePayment}
      className="w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
    >
      Pay ₹{amount.toFixed(2)}
    </button>
  );
};
```

### Step 5: Webhook Setup (Important!)

Webhooks notify your server about payment status changes.

#### 1. Create Webhook Endpoint
```java
@RestController
@RequestMapping("/api/webhooks/razorpay")
public class RazorpayWebhookController {
    
    @Value("${payment.gateway.razorpay.webhook-secret}")
    private String webhookSecret;
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        
        try {
            // Verify webhook signature
            boolean isValid = verifyWebhookSignature(payload, signature, webhookSecret);
            
            if (!isValid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
            }
            
            // Parse payload
            JSONObject jsonPayload = new JSONObject(payload);
            String event = jsonPayload.getString("event");
            JSONObject paymentEntity = jsonPayload.getJSONObject("payload")
                                                   .getJSONObject("payment")
                                                   .getJSONObject("entity");
            
            String paymentId = paymentEntity.getString("id");
            String status = paymentEntity.getString("status");
            
            // Handle different events
            switch (event) {
                case "payment.captured":
                    paymentService.handlePaymentSuccess(paymentId);
                    break;
                case "payment.failed":
                    paymentService.handlePaymentFailure(paymentId);
                    break;
                case "refund.processed":
                    paymentService.handleRefundProcessed(paymentId);
                    break;
            }
            
            return ResponseEntity.ok("Webhook processed");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Webhook processing failed");
        }
    }
    
    private boolean verifyWebhookSignature(String payload, String signature, String secret) {
        try {
            String expectedSignature = Utils.getHash(payload, secret);
            return expectedSignature.equals(signature);
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### 2. Register Webhook in Razorpay Dashboard
```
1. Go to Settings → Webhooks
2. Click "Create Webhook"
3. Enter URL: https://yourdomain.com/api/webhooks/razorpay
4. Select events:
   - payment.captured
   - payment.failed
   - payment.authorized
   - refund.processed
5. Copy "Webhook Secret" and add to application.yml
```

### Step 6: Testing

#### Test Cards (Razorpay Test Mode)
```
✅ Success:
Card: 4111 1111 1111 1111
CVV: Any 3 digits
Expiry: Any future date

✅ Success (Domestic):
Card: 5104 0600 0000 0008
CVV: Any 3 digits
Expiry: Any future date

❌ Failure (Insufficient Funds):
Card: 4000 0000 0000 0002

❌ Failure (Card Declined):
Card: 4000 0000 0000 0069
```

---

## 📊 Pricing Comparison

### Razorpay
- **Transaction Fee:** 2% + GST (2.36% total)
- **Setup Fee:** ₹0
- **Annual Fee:** ₹0
- **Settlement:** T+3 days (3 days after transaction)
- **Example:** ₹1000 booking → You receive ₹976.40

### Alternatives

#### 1. Paytm
- **Fee:** 2% + GST
- **Settlement:** T+1 day
- **Pros:** Fast settlement, good for small businesses
- **Cons:** Less developer-friendly API

#### 2. Cashfree
- **Fee:** 1.95% + GST (cheaper!)
- **Settlement:** T+2 days
- **Pros:** Lower fees, good API
- **Cons:** Requires 6-month bank statement

#### 3. Stripe (International)
- **Fee:** 2.9% + ₹2 per transaction
- **Settlement:** T+7 days
- **Pros:** Best developer experience, global reach
- **Cons:** Higher fees, slower settlement in India

---

## 🚀 Recommended Approach

### Phase 1: Development (Now)
✅ **Use Dummy Payment Gateway**
- No registration needed
- Immediate start
- Test all flows
- Free

### Phase 2: Testing (In parallel)
✅ **Register Razorpay Test Mode**
- Create account (5 minutes)
- Get test keys (instant)
- Integrate & test
- Free

### Phase 3: Production (When ready)
✅ **Activate Razorpay Live Mode**
- Complete KYC (1 day)
- Wait for approval (2-3 days)
- Get live keys
- Go live!

---

## ✅ Next Steps

1. **Now:** I'll implement Dummy Payment Gateway in Phase 1
2. **You:** Create Razorpay account and start KYC process (in parallel)
3. **Later:** Switch from DUMMY to RAZORPAY mode (just config change!)

Ready to begin Phase 1 implementation? 🚀

