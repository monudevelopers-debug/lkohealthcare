# Environment Variables Configuration

## Backend Configuration

**File:** `/backend/.env` (already created with Twilio credentials)

The `.env` file is in `.gitignore` and contains:
- Twilio Account SID
- Twilio Auth Token  
- Twilio Phone Number
- Database credentials
- JWT secret

**Note:** The `.env` file is NOT committed to git for security.

## Customer Portal Configuration

**File:** `/customer-portal/.env.development` (already configured)

Contains:
- API URL
- Firebase configuration
- Twilio credentials
- Company details

**Note:** `.env.development` is also in `.gitignore`.

## Running the Backend

The backend will automatically load variables from `.env` file.

Just run:
```bash
cd backend
java -jar target/healthcare-1.0.0.jar
```

## For Production Deployment

Set environment variables in your hosting platform (e.g., Heroku, AWS, etc.):
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_PHONE_NUMBER`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

