import { useAuth } from '../../lib/auth/AuthContext';
import { Card } from '../../components/ui/Card';
import { Button } from '../../components/ui/Button';

export const DebugPage = () => {
  const { user, token, expiresAt, isAuthenticated } = useAuth();

  const checkToken = () => {
    const auth = localStorage.getItem('lhc_auth');
    console.log('Raw localStorage:', auth);
    
    if (auth) {
      const parsed = JSON.parse(auth);
      console.log('Parsed auth:', {
        hasToken: !!parsed.token,
        tokenPreview: parsed.token?.substring(0, 50) + '...',
        expiresAt: parsed.expiresAt,
        expiryDate: new Date(parsed.expiresAt),
        isExpired: Date.now() > parsed.expiresAt,
        timeRemaining: Math.floor((parsed.expiresAt - Date.now()) / 1000 / 60) + ' minutes'
      });
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-3xl font-bold text-gray-900 mb-8">Auth Debug Info</h1>

        <div className="space-y-6">
          <Card>
            <h2 className="text-xl font-semibold mb-4">Authentication State</h2>
            <div className="space-y-2">
              <p><strong>Is Authenticated:</strong> {isAuthenticated ? '✅ Yes' : '❌ No'}</p>
              <p><strong>Has Token:</strong> {token ? '✅ Yes' : '❌ No'}</p>
              <p><strong>Token Preview:</strong> {token ? token.substring(0, 50) + '...' : 'None'}</p>
              <p><strong>Expires At:</strong> {expiresAt ? new Date(expiresAt).toLocaleString() : 'N/A'}</p>
              <p><strong>Is Expired:</strong> {expiresAt && Date.now() > expiresAt ? '❌ Yes' : '✅ No'}</p>
              <p><strong>Time Remaining:</strong> {expiresAt ? Math.floor((expiresAt - Date.now()) / 1000 / 60) + ' minutes' : 'N/A'}</p>
            </div>
          </Card>

          <Card>
            <h2 className="text-xl font-semibold mb-4">User Info</h2>
            {user ? (
              <div className="space-y-2">
                <p><strong>ID:</strong> {user.id}</p>
                <p><strong>Name:</strong> {user.name}</p>
                <p><strong>Email:</strong> {user.email}</p>
                <p><strong>Phone:</strong> {user.phone}</p>
                <p><strong>Role:</strong> {user.role}</p>
                <p><strong>Status:</strong> {user.status}</p>
              </div>
            ) : (
              <p className="text-gray-500">No user logged in</p>
            )}
          </Card>

          <Card>
            <h2 className="text-xl font-semibold mb-4">Actions</h2>
            <div className="space-x-4">
              <Button onClick={checkToken}>
                Check Token in Console
              </Button>
              <Button variant="outline" onClick={() => {
                localStorage.clear();
                window.location.href = '/';
              }}>
                Clear Storage & Reload
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};
