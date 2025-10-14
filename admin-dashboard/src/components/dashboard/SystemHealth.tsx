import React from 'react';

interface Props {
  health: any;
  loading?: boolean;
}

const SystemHealth: React.FC<Props> = ({ health, loading }) => {
  if (loading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border p-6">Loading system health…</div>
    );
  }

  if (!health) {
    return (
      <div className="bg-white rounded-lg shadow-sm border p-6">No system health data</div>
    );
  }

  const rows = [
    { label: 'Database', value: health.database ?? 'unknown' },
    { label: 'Redis', value: health.redis ?? 'unknown' },
    { label: 'Email', value: health.email ?? 'unknown' },
    { label: 'Overall', value: health.overall ?? (health.isHealthy ? 'healthy' : 'unhealthy') },
    { label: 'Last Checked', value: health.lastChecked ?? '-' },
  ];

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h2 className="text-lg font-semibold text-gray-900 mb-4">System Health</h2>
      <div className="space-y-2">
        {rows.map((r) => (
          <div key={r.label} className="flex items-center justify-between text-sm">
            <span className="text-gray-600">{r.label}</span>
            <span className="font-medium text-gray-900">{String(r.value)}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SystemHealth;
