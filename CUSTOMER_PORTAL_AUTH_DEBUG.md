# 🔍 Customer Portal Authentication Debug Guide

## ❓ Issue: Patient Tab Logs Out User

### Symptoms:
- User can login successfully
- Dashboard loads fine
- Clicking "My Patients" tab logs the user out
- 401 Unauthorized error

---

## 🧪 Debugging Steps

### Step 1: Check Browser Console

Open browser console and check for:
```
401 Unauthorized - API: /patients
401 Error Response: {...}
```

Look for the exact error message.

### Step 2: Verify JWT Token

In browser console:
```javascript
// Check if token exists
const auth = localStorage.getItem('lhc_auth');
console.log(JSON.parse(auth));

// You should see:
// {
//   token: "eyJhbGci...",
//   user: { id: "...", email: "...", role: "CUSTOMER", ... },
//   expiresAt: 1234567890
// }
```

### Step 3: Check Token in Network Tab

1. Open Network tab
2. Click "My Patients"
3. Look for request to `/api/patients`
4. Check Headers → Authorization
5. Should see: `Bearer eyJhbGci...`

---

## 🔧 Possible Issues & Solutions

### Issue 1: JWT Token Expired

**Symptom:** Token in localStorage is expired

**Solution:**
```javascript
// Clear localStorage and login again
localStorage.clear();
// Then login again
```

### Issue 2: Role Not Matching

**Check:** JWT might have `role: "USER"` instead of `role: "CUSTOMER"`

**Solution:** Verify in database that the user has role = 'CUSTOMER'

```sql
SELECT id, email, role FROM users WHERE email = 'your@email.com';
```

If role is wrong, update it:
```sql
UPDATE users SET role = 'CUSTOMER' WHERE email = 'your@email.com';
```

### Issue 3: Backend Not Reading Role Correctly

**Check:** SecurityConfig expects "ROLE_CUSTOMER" in authorities

**Debug:** Check backend logs when accessing `/api/patients`:
```
Look for: "JWT Token validated successfully for user: ..."
Look for: "User authorities: [ROLE_CUSTOMER]"
```

If you see `[ROLE_USER]` instead of `[ROLE_CUSTOMER]`, the JWT is not including the correct role.

### Issue 4: JWT Not Being Sent

**Check:** Authorization header is being added by interceptor

**Debug in client.ts:**
```typescript
apiClient.interceptors.request.use(
  (config) => {
    const auth = localStorage.getItem('lhc_auth');
    console.log('🔑 Auth in localStorage:', auth ? 'EXISTS' : 'MISSING');
    
    if (auth) {
      try {
        const { token } = JSON.parse(auth);
        console.log('🎫 Token:', token ? 'EXISTS' : 'MISSING');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
          console.log('✅ Authorization header set');
        }
      } catch (error) {
        console.error('❌ Failed to parse auth token:', error);
      }
    }
    return config;
  }
);
```

---

## ✅ Quick Fix (Most Common)

### The issue is usually: **Expired Token or Wrong Role**

**Solution:**
```javascript
// In browser console:
// 1. Clear old token
localStorage.clear();

// 2. Refresh page and login again

// 3. After login, verify:
const auth = JSON.parse(localStorage.getItem('lhc_auth'));
console.log('User:', auth.user.email);
console.log('Role:', auth.user.role); // Should be "CUSTOMER"
console.log('Token expires:', new Date(auth.expiresAt));
```

---

## 🔍 Backend Verification

### Check if Backend is Running:
```bash
curl http://localhost:8080/api/health
```

### Test Login API:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@customer.com",
    "password": "password"
  }'
```

Expected response:
```json
{
  "token": "eyJhbGci...",
  "user": {
    "id": "...",
    "email": "test@customer.com",
    "role": "CUSTOMER",
    ...
  },
  "expiresIn": 900000,
  "message": "Login successful"
}
```

### Test Patients API with Token:
```bash
curl http://localhost:8080/api/patients \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

Expected: Should return `[]` or list of patients, NOT 401.

---

## 🎯 Most Likely Fix

The issue is that when you registered/logged in earlier, the backend was storing users with `role = "USER"` but now we need `role = "CUSTOMER"` for the customer portal.

### Fix Database:
```sql
-- Update all non-admin, non-provider users to CUSTOMER
UPDATE users 
SET role = 'CUSTOMER' 
WHERE role = 'USER' 
  AND email NOT LIKE '%admin%' 
  AND email NOT LIKE '%provider%';

-- Or update specific user
UPDATE users 
SET role = 'CUSTOMER' 
WHERE email = 'your@email.com';
```

### Then:
1. Clear browser localStorage
2. Login again
3. New JWT will have correct role
4. Patients tab will work!

---

## 🔒 Security Config Check

Verify `/patients/**` endpoint is configured:

```java
// In SecurityConfig.java
.requestMatchers("/patients/**").hasAnyRole("ADMIN", "USER", "CUSTOMER")
.requestMatchers("/consents/**").hasAnyRole("ADMIN", "USER", "CUSTOMER")
```

✅ This is already configured correctly in your code.

---

## 🚀 After Fixing

Once you login successfully and navigate to patients:

**Expected:**
- Page loads
- "No Patients Yet" message appears
- "Add Patient" button works
- Can create patients
- No logout

**If Still Issues:**
- Check backend console logs
- Check browser console logs
- Verify JWT token in Network tab
- Ensure backend is running on port 8080
- Ensure customer portal is on port 5175

---

## 📞 Need More Help?

Share:
1. Browser console errors
2. Backend console logs when accessing /patients
3. Result of: `SELECT email, role FROM users;`

