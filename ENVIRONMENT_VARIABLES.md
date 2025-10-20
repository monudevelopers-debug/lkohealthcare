# Environment Variables Configuration

## Twilio SMS Configuration

Set these environment variables before running the backend:

```bash
export TWILIO_ACCOUNT_SID=your_twilio_account_sid_here
export TWILIO_AUTH_TOKEN=your_twilio_auth_token_here
export TWILIO_PHONE_NUMBER=your_twilio_phone_number_here
```

Or set them in your IDE run configuration.

**Note:** Get your Twilio credentials from: https://console.twilio.com/

## Customer Portal (.env.development)

Located at: `/customer-portal/.env.development`

Update this file with your actual Twilio credentials for frontend OTP functionality.

