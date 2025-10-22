// Debug script for frontend button issues
// Run this in the browser console on the provider dashboard

console.log('🔍 Starting frontend button debug...');

// Check if React Query is available
if (typeof window !== 'undefined' && window.ReactQueryDevtools) {
    console.log('✅ React Query DevTools detected');
} else {
    console.log('❌ React Query DevTools not found');
}

// Check localStorage for token
const token = localStorage.getItem('token');
const user = localStorage.getItem('user');

console.log('🔑 Authentication check:');
console.log('Token exists:', !!token);
console.log('User exists:', !!user);
console.log('Token preview:', token ? token.substring(0, 50) + '...' : 'None');

// Check if API client is working
if (typeof window !== 'undefined') {
    // Try to make a simple API call
    fetch('http://localhost:8080/api/providers/me', {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        console.log('🌐 API connectivity test:');
        console.log('Status:', response.status);
        console.log('OK:', response.ok);
        return response.json();
    })
    .then(data => {
        console.log('✅ API call successful:', data);
    })
    .catch(error => {
        console.log('❌ API call failed:', error);
    });
}

// Check for common issues
console.log('🔍 Checking for common issues:');

// Check if buttons exist
setTimeout(() => {
    const buttons = document.querySelectorAll('button');
    console.log('🔘 Buttons found:', buttons.length);
    
    const acceptButtons = document.querySelectorAll('button[onclick*="accept"], button:contains("Accept")');
    console.log('✅ Accept buttons found:', acceptButtons.length);
    
    const rejectButtons = document.querySelectorAll('button[onclick*="reject"], button:contains("Reject")');
    console.log('❌ Reject buttons found:', rejectButtons.length);
    
    // Check for React components
    const reactRoot = document.querySelector('#root');
    if (reactRoot) {
        console.log('⚛️ React root found');
        
        // Try to find React components
        const reactElements = reactRoot.querySelectorAll('[data-reactroot]');
        console.log('⚛️ React elements found:', reactElements.length);
    }
    
    // Check for console errors
    const originalError = console.error;
    console.error = function(...args) {
        console.log('🚨 Console error detected:', ...args);
        originalError.apply(console, args);
    };
    
}, 2000);

console.log('🔍 Debug script loaded. Check the console for results.');
