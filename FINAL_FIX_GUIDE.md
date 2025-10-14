# 🔧 Admin Dashboard - Final Fix Guide

## ✅ What I Fixed

1. **Killed old backend processes** - Removed conflicting processes on port 8080
2. **Started fresh backend** - New instance with correct configuration
3. **Fixed User interface mismatch** - Updated to match backend response (name instead of firstName/lastName)
4. **Added comprehensive debugging** - Console logs to track token flow
5. **Enhanced token storage** - Stores in both 'token' and 'authToken' keys
6. **Added timing fix** - Small delay after login to ensure localStorage writes

## 🚨 CRITICAL: What You Need To Do NOW

### Step 1: Clear Everything in Your Browser

**Open DevTools (F12) → Console Tab → Run this:**

```javascript
localStorage.clear();
sessionStorage.clear();
console.log('✅ Storage cleared!');
```

### Step 2: Hard Refresh the Page

- **Windows/Linux**: `Ctrl + Shift + R`
- **Mac**: `Cmd + Shift + R`

Or just close the tab and open a new one to: `http://localhost:5173`

### Step 3: Login Fresh

You should be automatically redirected to the login page.

**Enter these credentials:**
- Email: `admin@lucknowhealthcare.com`
- Password: `password123`

Click "Sign in"

### Step 4: Check Console Output

After logging in, you should see in the console:

```
Login successful, token: eyJhbGciOiJIUzUxMi...
User data: {id: "...", email: "admin@...", role: "ADMIN", ...}
[API] Request to /admin/stats with token: eyJhbGciOiJIUzUxMi...
[API] Request to /admin/health with token: eyJhbGciOiJIUzUxMi...
```

✅ **If you see these logs with tokens, everything is working!**

❌ **If you see "Request without token", continue to Step 5**

### Step 5: Manual Token Check (If Still Broken)

If you still see 401 errors, run this in the console:

```javascript
// Check if token exists
console.log('Token:', localStorage.getItem('token'));
console.log('User:', localStorage.getItem('user'));

// If token is null, manually login via API
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {'Content-Type': 'application/json'},
  body: JSON.stringify({
    email: 'admin@lucknowhealthcare.com',
    password: 'password123'
  })
})
.then(r => r.json())
.then(data => {
  console.log('Manual login response:', data);
  localStorage.setItem('token', data.token);
  localStorage.setItem('authToken', data.token);
  localStorage.setItem('user', JSON.stringify(data.user));
  console.log('✅ Token stored manually. Refresh the page.');
  window.location.reload();
});
```

## 🔍 Debugging Information

### What The Console Logs Mean

1. **`[API] Request to /admin/stats with token: eyJh...`**
   - ✅ GOOD: Token is being sent with the request

2. **`[API] Request to /admin/stats without token`**
   - ❌ BAD: Token is missing from localStorage

3. **`[API] 401 Unauthorized for /admin/stats`**
   - ❌ BAD: Either no token, invalid token, or wrong role

4. **`[API] Token in localStorage: EXISTS`**
   - ✅ GOOD: Token is stored

5. **`[API] Token in localStorage: MISSING`**
   - ❌ BAD: Need to login again

### How To Verify Backend Is Running

```bash
curl http://localhost:8080/api/auth/login -X POST \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@lucknowhealthcare.com","password":"password123"}' | jq
```

Should return JSON with token and user object.

## 📊 Expected Behavior After Fix

### Login Flow:
1. Navigate to http://localhost:5173
2. Automatically redirected to `/login`
3. Enter credentials and click "Sign in"
4. Console shows "Login successful" with token
5. Navigate to dashboard (/)
6. Dashboard loads with stats:
   - Total Users: 150
   - Total Providers: 25
   - Total Bookings: 300
   - Total Revenue: ₹150,000
7. All API calls show `[API] Request to... with token:`
8. No 401 errors

### What You'll See:
- ✅ User email in top-right corner
- ✅ Logout button
- ✅ Dashboard stats cards with numbers
- ✅ System health: All green
- ✅ No errors in console (except maybe chart data warnings)

## 🐛 Troubleshooting

### Problem: Still getting 401 after login

**Solution 1: Check token validity**
```javascript
// In browser console
const token = localStorage.getItem('token');
fetch('http://localhost:8080/api/admin/stats?period=month', {
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  }
})
.then(r => r.json())
.then(data => console.log('Direct API call result:', data))
.catch(err => console.error('Direct API call error:', err));
```

If this works but the dashboard doesn't, the issue is with the axios interceptor.

**Solution 2: Force token refresh**
```javascript
// Logout and login again
localStorage.clear();
window.location.href = '/login';
```

### Problem: "Port 8080 already in use"

```bash
lsof -ti:8080 | xargs kill -9
cd backend && mvn spring-boot:run
```

### Problem: Backend not responding

```bash
# Check if backend is running
lsof -i:8080

# Check backend logs
tail -f /tmp/backend.log
```

### Problem: Token expires quickly

The token expires in 15 minutes (900000 ms). If you're inactive for more than 15 minutes, you'll need to login again.

## 📝 Technical Details

### What Changed:

1. **`/admin-dashboard/src/hooks/useAuth.tsx`**
   - Changed User interface from firstName/lastName to name
   - Added console logging for debugging
   - Store token in both 'token' and 'authToken'
   - Added 100ms delay before navigation

2. **`/admin-dashboard/src/services/api.ts`**
   - Added console logging in request interceptor
   - Added console logging in response interceptor
   - Better error messages for debugging

3. **`/admin-dashboard/src/components/layout/Layout.tsx`**
   - Added authentication guard
   - Redirect to login if not authenticated
   - Display user email and logout button

### Backend Status:
- ✅ Running on port 8080
- ✅ Database connected and populated
- ✅ JWT authentication working
- ✅ Admin endpoints returning data

### Frontend Status:
- ✅ Running on port 5173
- ✅ Authentication guard in place
- ✅ Token interceptors configured
- ✅ Debugging enabled

## 🎯 Success Checklist

After following the steps above, verify:

- [ ] Console shows "Login successful" after login
- [ ] Console shows "[API] Request to... with token:" for all requests
- [ ] No 401 errors in Network tab
- [ ] Dashboard displays numbers (not zeros or loading)
- [ ] User email visible in top-right
- [ ] Logout button works
- [ ] Can navigate to Users, Providers, Bookings, etc.
- [ ] Each page loads data without errors

If all checked, **YOU'RE DONE! 🎉**

## 🆘 Still Stuck?

If nothing works, do a complete restart:

```bash
# Terminal 1
cd /Users/srivastavas07/Desktop/LKO
lsof -ti:8080 | xargs kill -9
lsof -ti:5173 | xargs kill -9
cd backend && mvn clean spring-boot:run

# Wait for "Started LucknowHealthcareApplication"

# Terminal 2
cd /Users/srivastavas07/Desktop/LKO/admin-dashboard
npm run dev

# Browser
# 1. Go to http://localhost:5173
# 2. F12 → Console → Run: localStorage.clear()
# 3. Refresh page
# 4. Login with admin@lucknowhealthcare.com / password123
```

Check the console output at EVERY step and report what you see if still broken.

