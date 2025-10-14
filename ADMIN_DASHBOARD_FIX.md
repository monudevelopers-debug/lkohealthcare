# Admin Dashboard 401 Error Fix

## Summary of Changes Made

### 1. Backend Changes
- **SecurityConfig.java**: Added `JwtAccessDeniedHandler` and removed `anonymous().disable()` to allow proper CORS preflight requests
- **JwtRequestFilter.java**: Added detailed logging to show user authorities/roles
- **JwtAccessDeniedHandler.java**: New class to handle 403 Forbidden errors separately from 401 Unauthorized errors
- **Admin Dashboard API Client**: Fixed to use unified axios instance with proper JWT token interceptors

### 2. Frontend Changes
- **admin-dashboard/src/services/api.ts**: 
  - Added `getAdminStats` and `getSystemHealth` methods to the ApiClient class
  - Ensured all admin endpoints use the same authenticated axios instance
  - Fixed token interceptor to properly attach JWT tokens to all requests

## Steps to Fix the Issue

### Step 1: Restart the Backend
```bash
cd /Users/srivastavas07/Desktop/LKO/backend
# Stop any running instances (Ctrl+C in the terminal where it's running)
# Then restart:
mvn spring-boot:run
```

### Step 2: Clear Browser Storage and Login
1. Open your browser's Developer Tools (F12)
2. Go to the **Application** tab
3. In the left sidebar, click on **Local Storage** > `http://localhost:3000`
4. Click **Clear All** to remove any old tokens
5. Refresh the page and login with admin credentials:
   - Email: `admin@lucknowhealthcare.com`
   - Password: `password123`

### Step 3: Verify Token Storage
After logging in:
1. Open Developer Tools > **Application** > **Local Storage**
2. Check that you have a `token` key with a JWT value
3. The token should look like: `eyJhbGciOiJIUzUxMiJ9...` (long string)

### Step 4: Check Backend Logs
After the dashboard loads, check the backend logs for:
```
User authorities: [ROLE_ADMIN]
Authentication set in SecurityContext for user: admin@lucknowhealthcare.com with authorities: [ROLE_ADMIN]
```

If you see `[ROLE_ADMIN]`, the authentication is working correctly.

### Step 5: Check Network Requests
1. Open Developer Tools > **Network** tab
2. Reload the dashboard
3. Look for the `/admin/stats` and `/admin/health` requests
4. Click on each request and check:
   - **Headers** tab > **Request Headers** > Look for `Authorization: Bearer <token>`
   - **Response** tab > Check for error messages

## Common Issues and Solutions

### Issue 1: Token Not Being Sent
**Symptoms**: Network requests don't have `Authorization` header
**Solution**: 
- Clear localStorage and login again
- Check that the login response includes a `token` field
- Verify the token is being saved in localStorage

### Issue 2: Wrong Role in Token
**Symptoms**: Backend logs show different role than `ROLE_ADMIN`
**Solution**:
- Ensure you're logging in with `admin@lucknowhealthcare.com` (not a customer or provider account)
- Check the database to verify the admin user has `role='ADMIN'`

### Issue 3: CORS Preflight Errors
**Symptoms**: OPTIONS requests failing with 401
**Solution**: Already fixed in SecurityConfig.java - restart backend

### Issue 4: Base URL Mismatch
**Symptoms**: Requests going to wrong URL (e.g., `/api/api/admin/stats`)
**Solution**: Already fixed in api.ts - the API_BASE_URL should be `http://localhost:8080/api`

## Testing the Fix

### Quick Test in Browser Console
After logging in, open the browser console and run:
```javascript
// Check if token exists
console.log('Token:', localStorage.getItem('token'));

// Test API call
fetch('http://localhost:8080/api/admin/stats?period=month', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token'),
    'Content-Type': 'application/json'
  }
})
.then(r => r.json())
.then(data => console.log('Response:', data))
.catch(err => console.error('Error:', err));
```

If this returns admin stats data, the API is working correctly.

## Additional Debugging

### Check User Role in Database
```sql
SELECT id, email, name, role, status FROM users WHERE email = 'admin@lucknowhealthcare.com';
```
Should return a user with `role='ADMIN'`

### Enable DEBUG Logging
In `backend/src/main/resources/application.yml`, add:
```yaml
logging:
  level:
    com.lucknow.healthcare.security: DEBUG
    org.springframework.security: DEBUG
```

Then restart the backend and check logs for detailed security information.

## Contact
If issues persist, provide:
1. Browser console logs (errors)
2. Network tab screenshot showing the failing request headers
3. Backend logs showing the authentication attempt
4. Screenshot of localStorage showing the token

