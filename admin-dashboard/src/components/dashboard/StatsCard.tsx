import React from 'react';
import type { LucideIcon } from 'lucide-react';

interface StatsCardProps {
  title: string;
  value: React.ReactNode;
  icon: LucideIcon;
  color?: 'blue' | 'green' | 'purple' | 'orange' | string;
  change?: number;
  trend?: 'up' | 'down' | 'stable';
  loading?: boolean;
}

const colorMap: Record<string, { bg: string; text: string }> = {
  blue: { bg: 'bg-blue-100', text: 'text-blue-600' },
  green: { bg: 'bg-green-100', text: 'text-green-600' },
  purple: { bg: 'bg-purple-100', text: 'text-purple-600' },
  orange: { bg: 'bg-orange-100', text: 'text-orange-600' },
};

const StatsCard: React.FC<StatsCardProps> = ({ title, value, icon: Icon, color = 'blue', change, trend, loading }) => {
  const palette = colorMap[color] || colorMap.blue;

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-gray-600">{title}</p>
          <p className="mt-1 text-2xl font-bold text-gray-900">{loading ? '—' : value}</p>
        </div>
        <div className={`p-3 rounded-lg ${palette.bg}`}>
          <Icon className={`w-6 h-6 ${palette.text}`} />
        </div>
      </div>
      {typeof change !== 'undefined' && (
        <div className="mt-3 text-sm">
          <span className={trend === 'down' ? 'text-red-600' : trend === 'up' ? 'text-green-600' : 'text-gray-600'}>
            {change > 0 ? '+' : ''}{change}%
          </span>
          <span className="text-gray-500 ml-2">vs previous</span>
        </div>
      )}
    </div>
  );
};

export default StatsCard;
