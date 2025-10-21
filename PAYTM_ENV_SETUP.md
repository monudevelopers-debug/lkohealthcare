# 🔧 Paytm Payment Gateway - Environment Setup

## ✅ CREDENTIALS ALREADY CONFIGURED!

All credentials are maintained in: `/Users/srivastavas07/Desktop/LKO/backend/.env`

This file contains **ALL** application credentials in one consolidated location:
- ✅ Paytm Payment Gateway (Test Credentials)
- ✅ Database Connection
- ✅ JWT Secrets
- ✅ Email (Gmail SMTP)
- ✅ Twilio SMS
- ✅ Redis
- ✅ Application URLs
- ✅ File Upload Config
- ✅ Razorpay & Stripe (for future)

## 📝 .env File Content

The `.env` file in `/Users/srivastavas07/Desktop/LKO/backend/` contains:

```env
# Paytm Payment Gateway Configuration
PAYTM_MERCHANT_ID=CYdegU38869454615092
PAYTM_MERCHANT_KEY=5x1myc2MuoUqtEvZ
PAYTM_WEBSITE=WEBSTAGING
PAYTM_INDUSTRY_TYPE=Retail
PAYTM_CHANNEL_ID_WEB=WEB
PAYTM_CHANNEL_ID_MOBILE=WAP
PAYTM_CALLBACK_URL=http://localhost:8080/api/payments/paytm/callback

# Payment Gateway Mode
PAYMENT_GATEWAY_MODE=PAYTM

# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/lucknow_healthcare
DB_USERNAME=healthcare_user
DB_PASSWORD=healthcare_password

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-here-change-in-production-this-key-must-be-at-least-512-bits-long-for-hs512-algorithm
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Email Configuration
MAIL_USERNAME=monudevelopers@gmail.com
MAIL_PASSWORD=SHIvam@7426

# Twilio Configuration
TWILIO_ACCOUNT_SID=your_account_sid_here
TWILIO_AUTH_TOKEN=your_auth_token_here
TWILIO_PHONE_NUMBER=+1234567890

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379

# Application URLs
APP_URL=http://localhost:8080
FRONTEND_URL=http://localhost:5175
PROVIDER_DASHBOARD_URL=http://localhost:5174
ADMIN_DASHBOARD_URL=http://localhost:5173

# File Upload
UPLOAD_DIR=./uploads
MAX_FILE_SIZE=5242880
```

## 📌 Add .env to .gitignore

Add this line to `.gitignore`:
```
.env
*.env
!.env.example
```

## ✅ Manual Steps:

```bash
cd /Users/srivastavas07/Desktop/LKO/backend

# Create .env file
cat > .env << 'EOF'
PAYTM_MERCHANT_ID=CYdegU38869454615092
PAYTM_MERCHANT_KEY=5x1myc2MuoUqtEvZ
PAYTM_WEBSITE=WEBSTAGING
PAYTM_INDUSTRY_TYPE=Retail
PAYTM_CHANNEL_ID_WEB=WEB
PAYTM_CHANNEL_ID_MOBILE=WAP
PAYTM_CALLBACK_URL=http://localhost:8080/api/payments/paytm/callback
PAYMENT_GATEWAY_MODE=PAYTM
MAIL_USERNAME=monudevelopers@gmail.com
MAIL_PASSWORD=SHIvam@7426
EOF

# Verify it was created
cat .env
```

Now I'll implement the Paytm integration!

