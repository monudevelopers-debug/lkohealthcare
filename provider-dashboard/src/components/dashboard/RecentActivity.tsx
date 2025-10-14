import React from 'react';

const RecentActivity: React.FC = () => {
  const items = [
    { id: 1, text: 'New booking created by Rahul Kumar', time: '2m ago' },
    { id: 2, text: 'Payment completed for ECG Test', time: '15m ago' },
    { id: 3, text: 'Review added for Dr. Sharma', time: '1h ago' },
  ];

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h2 className="text-lg font-semibold text-gray-900 mb-4">Recent Activity</h2>
      <div className="space-y-3">
        {items.map((item) => (
          <div key={item.id} className="flex items-start justify-between">
            <div className="text-sm text-gray-800">{item.text}</div>
            <div className="text-xs text-gray-500">{item.time}</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default RecentActivity;
