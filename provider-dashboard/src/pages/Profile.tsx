import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { 
  User, 
  Mail, 
  Phone, 
  MapPin, 
  Star,
  Award,
  Calendar,
  Edit,
  Save,
  X,
  Camera,
  Upload,
  CheckCircle,
  AlertCircle
} from 'lucide-react';

import { getProviderProfile, updateProviderProfile, updateAvailability } from '../services/api';

const Profile: React.FC = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [showImageUpload, setShowImageUpload] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  
  const queryClient = useQueryClient();

  // Fetch provider profile
  const { data: profile, isLoading } = useQuery(
    'provider-profile',
    getProviderProfile,
  );


  // Update profile mutation
  const updateProfileMutation = useMutation(
    (profileData: any) => updateProviderProfile(profileData),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('provider-profile');
        setIsEditing(false);
      },
    }
  );

  // Update availability mutation
  const updateAvailabilityMutation = useMutation(
    (isAvailable: boolean) => updateAvailability(isAvailable),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('provider-profile');
      },
      onError: (error: any) => {
        console.error('Failed to update availability:', error);
        // The error will be handled by the UI
      },
    }
  );

  // Upload image mutation - TODO: Implement when backend endpoint is ready
  // const uploadImageMutation = useMutation(
  //   (file: File) => uploadProviderImage(file),
  //   {
  //     onSuccess: () => {
  //       queryClient.invalidateQueries('provider-profile');
  //       setShowImageUpload(false);
  //       setSelectedFile(null);
  //       setPreviewUrl(null);
  //     },
  //   }
  // );

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      const url = URL.createObjectURL(file);
      setPreviewUrl(url);
    }
  };

  const handleImageUpload = () => {
    // TODO: Implement when backend endpoint is ready
    console.log('Image upload not yet implemented');
    // if (selectedFile) {
    //   uploadImageMutation.mutate(selectedFile);
    // }
  };

  const handleSave = (formData: FormData) => {
    const profileData = {
      name: formData.get('name'),
      email: formData.get('email'),
      phone: formData.get('phone'),
      address: formData.get('address'),
      qualifications: formData.get('qualifications'),
      experience: parseInt(formData.get('experience') as string),
      bio: formData.get('bio'),
      specializations: formData.get('specializations')?.toString().split(',').map(s => s.trim()),
    };
    updateProfileMutation.mutate(profileData);
  };

  const handleAvailabilityToggle = () => {
    if (profile) {
      const isCurrentlyAvailable = profile.availabilityStatus === 'AVAILABLE';
      updateAvailabilityMutation.mutate(!isCurrentlyAvailable);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">My Profile</h1>
          <p className="text-gray-600">Manage your professional profile and information</p>
        </div>
        <div className="flex items-center space-x-3">
          {!isEditing ? (
            <button
              onClick={() => setIsEditing(true)}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center gap-2"
            >
              <Edit className="w-4 h-4" />
              Edit Profile
            </button>
          ) : (
            <div className="flex items-center space-x-2">
              <button
                onClick={() => setIsEditing(false)}
                className="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 flex items-center gap-2"
              >
                <X className="w-4 h-4" />
                Cancel
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Profile Card */}
        <div className="lg:col-span-1">
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="text-center">
              <div className="relative inline-block">
                <div className="w-24 h-24 rounded-full bg-blue-100 flex items-center justify-center mx-auto mb-4">
                  {profile?.profileImage ? (
                    <img
                      src={profile.profileImage}
                      alt="Profile"
                      className="w-24 h-24 rounded-full object-cover"
                    />
                  ) : (
                    <User className="w-12 h-12 text-blue-600" />
                  )}
                </div>
                <button
                  onClick={() => setShowImageUpload(true)}
                  className="absolute bottom-0 right-0 w-8 h-8 bg-blue-600 text-white rounded-full flex items-center justify-center hover:bg-blue-700"
                >
                  <Camera className="w-4 h-4" />
                </button>
              </div>
              <h2 className="text-xl font-semibold text-gray-900">{profile?.name}</h2>
              <p className="text-gray-600">{profile?.email}</p>
              <div className="flex items-center justify-center mt-2">
                <Star className="w-4 h-4 text-yellow-500 mr-1" />
                <span className="text-sm font-medium">{profile?.rating || 0}</span>
                <span className="text-sm text-gray-500 ml-1">/ 5.0</span>
              </div>
            </div>

            <div className="mt-6 space-y-4">
              <div className="flex items-center text-sm text-gray-600">
                <Phone className="w-4 h-4 mr-3" />
                {profile?.phone || 'Not provided'}
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <MapPin className="w-4 h-4 mr-3" />
                {profile?.address || 'Not provided'}
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <Award className="w-4 h-4 mr-3" />
                {profile?.experience || 0} years experience
              </div>
              <div className="flex items-center text-sm text-gray-600">
                <Calendar className="w-4 h-4 mr-3" />
                Joined {profile?.createdAt ? new Date(profile.createdAt).toLocaleDateString() : 'N/A'}
              </div>
            </div>

            <div className="mt-6 pt-6 border-t border-gray-200">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-medium text-gray-700 mb-2">Availability Status</h3>
              </div>
              
              {/* Error Message */}
              {updateAvailabilityMutation.isError && (
                <div className="mb-3 p-3 bg-red-50 border border-red-200 rounded-lg">
                  <div className="flex items-center">
                    <AlertCircle className="w-4 h-4 text-red-600 mr-2" />
                    <div className="text-sm text-red-800">
                      <p className="font-medium">Cannot change availability status</p>
                      <p className="text-xs mt-1">
                        You have active bookings that need to be completed or cancelled first.
                        <a href="/bookings" className="text-blue-600 hover:text-blue-800 underline ml-1">
                          Manage bookings
                        </a>
                      </p>
                    </div>
                  </div>
                </div>
              )}
              
              <div className="flex items-center justify-between mt-2">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                  profile?.availabilityStatus === 'AVAILABLE'
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {profile?.availabilityStatus === 'AVAILABLE' ? 'Available' : 'Unavailable'}
                </span>
                <button
                  onClick={handleAvailabilityToggle}
                  disabled={updateAvailabilityMutation.isLoading}
                  className={`px-3 py-1.5 text-xs font-medium rounded-lg transition-colors ${
                    profile?.availabilityStatus === 'AVAILABLE'
                      ? 'bg-red-100 text-red-700 hover:bg-red-200 disabled:bg-red-50'
                      : 'bg-green-100 text-green-700 hover:bg-green-200 disabled:bg-green-50'
                  } disabled:cursor-not-allowed`}
                  style={{ pointerEvents: 'auto' }}
                >
                  {updateAvailabilityMutation.isLoading 
                    ? 'Updating...' 
                    : (profile?.availabilityStatus === 'AVAILABLE' ? 'Go Offline' : 'Go Online')}
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Profile Form */}
        <div className="lg:col-span-2">
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-6">
              {isEditing ? 'Edit Profile Information' : 'Profile Information'}
            </h2>
            
            {isEditing ? (
              <form onSubmit={(e) => { e.preventDefault(); handleSave(new FormData(e.currentTarget)); }}>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Full Name
                    </label>
                    <input
                      type="text"
                      name="name"
                      required
                      defaultValue={profile?.name || ''}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Email
                    </label>
                    <input
                      type="email"
                      name="email"
                      required
                      defaultValue={profile?.email || ''}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Phone
                    </label>
                    <input
                      type="tel"
                      name="phone"
                      defaultValue={profile?.phone || ''}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Experience (years)
                    </label>
                    <input
                      type="number"
                      name="experience"
                      required
                      min="0"
                      defaultValue={profile?.experience || 0}
                      className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                  </div>
                </div>
                
                <div className="mt-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Address
                  </label>
                  <textarea
                    name="address"
                    rows={3}
                    defaultValue={profile?.address || ''}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div className="mt-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Qualifications
                  </label>
                  <textarea
                    name="qualifications"
                    rows={3}
                    defaultValue={profile?.qualifications || ''}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter your qualifications and certifications"
                  />
                </div>

                <div className="mt-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Specializations
                  </label>
                  <input
                    type="text"
                    name="specializations"
                    defaultValue={profile?.specializations?.join(', ') || ''}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Enter specializations separated by commas"
                  />
                </div>

                <div className="mt-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Bio
                  </label>
                  <textarea
                    name="bio"
                    rows={4}
                    defaultValue={profile?.bio || ''}
                    className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Tell us about yourself and your experience"
                  />
                </div>

                <div className="mt-6 flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={() => setIsEditing(false)}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={updateProfileMutation.isLoading}
                    className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50 flex items-center gap-2"
                  >
                    <Save className="w-4 h-4" />
                    {updateProfileMutation.isLoading ? 'Saving...' : 'Save Changes'}
                  </button>
                </div>
              </form>
            ) : (
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Bio
                  </label>
                  <p className="text-sm text-gray-900">
                    {profile?.bio || 'No bio provided'}
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Qualifications
                  </label>
                  <p className="text-sm text-gray-900">
                    {profile?.qualifications || 'No qualifications provided'}
                  </p>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Specializations
                  </label>
                  <div className="flex flex-wrap gap-2">
                    {profile?.specializations?.map((spec, index) => (
                      <span
                        key={index}
                        className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
                      >
                        {spec}
                      </span>
                    )) || (
                      <span className="text-sm text-gray-500">No specializations</span>
                    )}
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Image Upload Modal */}
      {showImageUpload && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h3 className="text-lg font-medium text-gray-900 mb-4">
              Upload Profile Image
            </h3>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Select Image
                </label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleFileSelect}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              {previewUrl && (
                <div className="text-center">
                  <img
                    src={previewUrl}
                    alt="Preview"
                    className="w-32 h-32 rounded-full object-cover mx-auto"
                  />
                </div>
              )}
              <div className="flex justify-end space-x-3">
                <button
                  onClick={() => setShowImageUpload(false)}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  onClick={handleImageUpload}
                  disabled={!selectedFile}
                  className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:opacity-50 flex items-center gap-2"
                >
                  <Upload className="w-4 h-4" />
                  Upload Image (Coming Soon)
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Profile;
