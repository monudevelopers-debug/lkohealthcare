import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  Settings as SettingsIcon,
  Save,
  RefreshCw,
  AlertTriangle,
  CheckCircle,
  Mail,
  Phone,
  Globe,
  Shield,
  Bell,
  Database,
  Server
} from 'lucide-react';

import { getSystemSettings, updateSystemSettings, testEmailConfiguration, testDatabaseConnection } from '../services/api';

const Settings: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'general' | 'email' | 'security' | 'notifications' | 'system'>('general');
  const [isSaving, setIsSaving] = useState(false);
  const [testResults, setTestResults] = useState<{[key: string]: boolean}>({});
  
  const queryClient = useQueryClient();

  // Fetch system settings
  const { data: settings, isLoading } = useQuery(
    'system-settings',
    getSystemSettings,
  );

  // Update settings mutation
  const updateSettingsMutation = useMutation(
    (settingsData: any) => updateSystemSettings(settingsData),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('system-settings');
        setIsSaving(false);
      },
    }
  );

  // Test email configuration
  const testEmailMutation = useMutation(
    testEmailConfiguration,
    {
      onSuccess: () => {
        setTestResults(prev => ({ ...prev, email: true }));
      },
      onError: () => {
        setTestResults(prev => ({ ...prev, email: false }));
      },
    }
  );

  // Test database connection
  const testDatabaseMutation = useMutation(
    testDatabaseConnection,
    {
      onSuccess: () => {
        setTestResults(prev => ({ ...prev, database: true }));
      },
      onError: () => {
        setTestResults(prev => ({ ...prev, database: false }));
      },
    }
  );

  const handleSave = (formData: FormData) => {
    setIsSaving(true);
    const settingsData = Object.fromEntries(formData.entries());
    updateSettingsMutation.mutate(settingsData);
  };

  const tabs = [
    { id: 'general', label: 'General', icon: SettingsIcon },
    { id: 'email', label: 'Email', icon: Mail },
    { id: 'security', label: 'Security', icon: Shield },
    { id: 'notifications', label: 'Notifications', icon: Bell },
    { id: 'system', label: 'System', icon: Server },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">System Settings</h1>
          <p className="text-gray-600">Configure system-wide settings and preferences</p>
        </div>
        <div className="flex items-center space-x-3">
          <button className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 flex items-center gap-2">
            <RefreshCw className="w-4 h-4" />
            Reset
          </button>
          <button className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2">
            <Save className="w-4 h-4" />
            Save Changes
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Sidebar */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow-sm border">
            <nav className="p-4 space-y-2">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`w-full flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-colors ${
                    activeTab === tab.id
                      ? 'bg-blue-100 text-blue-700'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <tab.icon className="w-4 h-4 mr-3" />
                  {tab.label}
                </button>
              ))}
            </nav>
          </div>
        </div>

        {/* Main Content */}
        <div className="lg:col-span-3">
          {isLoading ? (
            <div className="bg-white rounded-lg shadow-sm border p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              <p className="mt-2 text-gray-600">Loading settings...</p>
            </div>
          ) : (
            <div className="space-y-6">
              {/* General Settings */}
              {activeTab === 'general' && (
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">General Settings</h2>
                  <form onSubmit={(e) => { e.preventDefault(); handleSave(new FormData(e.currentTarget)); }}>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Application Name
                        </label>
                        <input
                          type="text"
                          name="appName"
                          defaultValue={settings?.appName || 'Lucknow Healthcare Services'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Application URL
                        </label>
                        <input
                          type="url"
                          name="appUrl"
                          defaultValue={settings?.appUrl || 'http://localhost:8080'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Support Email
                        </label>
                        <input
                          type="email"
                          name="supportEmail"
                          defaultValue={settings?.supportEmail || 'support@lucknowhealthcare.com'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Support Phone
                        </label>
                        <input
                          type="tel"
                          name="supportPhone"
                          defaultValue={settings?.supportPhone || '+91-9876543210'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                    </div>
                    <div className="mt-6">
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Application Description
                      </label>
                      <textarea
                        name="appDescription"
                        rows={3}
                        defaultValue={settings?.appDescription || 'Comprehensive healthcare services platform'}
                        className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                    </div>
                  </form>
                </div>
              )}

              {/* Email Settings */}
              {activeTab === 'email' && (
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">Email Configuration</h2>
                  <form onSubmit={(e) => { e.preventDefault(); handleSave(new FormData(e.currentTarget)); }}>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          SMTP Host
                        </label>
                        <input
                          type="text"
                          name="smtpHost"
                          defaultValue={settings?.smtpHost || 'smtp.gmail.com'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          SMTP Port
                        </label>
                        <input
                          type="number"
                          name="smtpPort"
                          defaultValue={settings?.smtpPort || 587}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          SMTP Username
                        </label>
                        <input
                          type="email"
                          name="smtpUsername"
                          defaultValue={settings?.smtpUsername || ''}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          SMTP Password
                        </label>
                        <input
                          type="password"
                          name="smtpPassword"
                          defaultValue={settings?.smtpPassword || ''}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                      </div>
                    </div>
                    <div className="mt-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-sm font-medium text-gray-700">Test Email Configuration</h3>
                          <p className="text-sm text-gray-500">Send a test email to verify settings</p>
                        </div>
                        <button
                          type="button"
                          onClick={() => testEmailMutation.mutate()}
                          disabled={testEmailMutation.isLoading}
                          className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 flex items-center gap-2"
                        >
                          {testEmailMutation.isLoading ? (
                            <RefreshCw className="w-4 h-4 animate-spin" />
                          ) : (
                            <Mail className="w-4 h-4" />
                          )}
                          Test Email
                        </button>
                      </div>
                      {testResults.email !== undefined && (
                        <div className={`mt-2 flex items-center ${
                          testResults.email ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {testResults.email ? (
                            <CheckCircle className="w-4 h-4 mr-2" />
                          ) : (
                            <AlertTriangle className="w-4 h-4 mr-2" />
                          )}
                          {testResults.email ? 'Email configuration is working' : 'Email configuration failed'}
                        </div>
                      )}
                    </div>
                  </form>
                </div>
              )}

              {/* Security Settings */}
              {activeTab === 'security' && (
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">Security Settings</h2>
                  <form onSubmit={(e) => { e.preventDefault(); handleSave(new FormData(e.currentTarget)); }}>
                    <div className="space-y-6">
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          JWT Secret Key
                        </label>
                        <input
                          type="password"
                          name="jwtSecret"
                          defaultValue={settings?.jwtSecret || ''}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <p className="text-sm text-gray-500 mt-1">Keep this secret and secure</p>
                      </div>
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            JWT Expiration (minutes)
                          </label>
                          <input
                            type="number"
                            name="jwtExpiration"
                            defaultValue={settings?.jwtExpiration || 15}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            Refresh Token Expiration (days)
                          </label>
                          <input
                            type="number"
                            name="refreshExpiration"
                            defaultValue={settings?.refreshExpiration || 7}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          BCrypt Rounds
                        </label>
                        <input
                          type="number"
                          name="bcryptRounds"
                          defaultValue={settings?.bcryptRounds || 12}
                          min="4"
                          max="15"
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <p className="text-sm text-gray-500 mt-1">Higher values are more secure but slower</p>
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          CORS Allowed Origins
                        </label>
                        <textarea
                          name="corsOrigins"
                          rows={3}
                          defaultValue={settings?.corsOrigins || 'http://localhost:3000,http://localhost:3001,http://localhost:3002'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <p className="text-sm text-gray-500 mt-1">Comma-separated list of allowed origins</p>
                      </div>
                    </div>
                  </form>
                </div>
              )}

              {/* Notifications Settings */}
              {activeTab === 'notifications' && (
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">Notification Settings</h2>
                  <form onSubmit={(e) => { e.preventDefault(); handleSave(new FormData(e.currentTarget)); }}>
                    <div className="space-y-6">
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-sm font-medium text-gray-700">Email Notifications</h3>
                          <p className="text-sm text-gray-500">Enable email notifications for users</p>
                        </div>
                        <input
                          type="checkbox"
                          name="emailNotifications"
                          defaultChecked={settings?.emailNotifications !== false}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                      </div>
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-sm font-medium text-gray-700">SMS Notifications</h3>
                          <p className="text-sm text-gray-500">Enable SMS notifications for users</p>
                        </div>
                        <input
                          type="checkbox"
                          name="smsNotifications"
                          defaultChecked={settings?.smsNotifications === true}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                      </div>
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-sm font-medium text-gray-700">Push Notifications</h3>
                          <p className="text-sm text-gray-500">Enable push notifications for mobile apps</p>
                        </div>
                        <input
                          type="checkbox"
                          name="pushNotifications"
                          defaultChecked={settings?.pushNotifications === true}
                          className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Notification Templates
                        </label>
                        <select
                          name="notificationTemplates"
                          defaultValue={settings?.notificationTemplates || 'default'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          <option value="default">Default Templates</option>
                          <option value="custom">Custom Templates</option>
                        </select>
                      </div>
                    </div>
                  </form>
                </div>
              )}

              {/* System Settings */}
              {activeTab === 'system' && (
                <div className="bg-white rounded-lg shadow-sm border p-6">
                  <h2 className="text-lg font-semibold text-gray-900 mb-4">System Configuration</h2>
                  <form onSubmit={(e) => { e.preventDefault(); handleSave(new FormData(e.currentTarget)); }}>
                    <div className="space-y-6">
                      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            Database URL
                          </label>
                          <input
                            type="text"
                            name="databaseUrl"
                            defaultValue={settings?.databaseUrl || 'jdbc:postgresql://localhost:5432/lucknow_healthcare'}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-2">
                            Redis URL
                          </label>
                          <input
                            type="text"
                            name="redisUrl"
                            defaultValue={settings?.redisUrl || 'redis://localhost:6379'}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                      </div>
                      <div className="flex items-center justify-between">
                        <div>
                          <h3 className="text-sm font-medium text-gray-700">Test Database Connection</h3>
                          <p className="text-sm text-gray-500">Verify database connectivity</p>
                        </div>
                        <button
                          type="button"
                          onClick={() => testDatabaseMutation.mutate()}
                          disabled={testDatabaseMutation.isLoading}
                          className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50 flex items-center gap-2"
                        >
                          {testDatabaseMutation.isLoading ? (
                            <RefreshCw className="w-4 h-4 animate-spin" />
                          ) : (
                            <Database className="w-4 h-4" />
                          )}
                          Test Database
                        </button>
                      </div>
                      {testResults.database !== undefined && (
                        <div className={`flex items-center ${
                          testResults.database ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {testResults.database ? (
                            <CheckCircle className="w-4 h-4 mr-2" />
                          ) : (
                            <AlertTriangle className="w-4 h-4 mr-2" />
                          )}
                          {testResults.database ? 'Database connection is working' : 'Database connection failed'}
                        </div>
                      )}
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                          Log Level
                        </label>
                        <select
                          name="logLevel"
                          defaultValue={settings?.logLevel || 'INFO'}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                          <option value="DEBUG">DEBUG</option>
                          <option value="INFO">INFO</option>
                          <option value="WARN">WARN</option>
                          <option value="ERROR">ERROR</option>
                        </select>
                      </div>
                    </div>
                  </form>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Settings;
