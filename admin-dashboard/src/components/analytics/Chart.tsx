import React from 'react';
import {
  ResponsiveContainer,
  LineChart,
  Line,
  BarChart,
  Bar,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
} from 'recharts';

interface ChartProps {
  data: any[];
  type: 'line' | 'bar' | 'area';
  height?: number;
  loading?: boolean;
}

const Chart: React.FC<ChartProps> = ({ data = [], type = 'line', height = 240, loading }) => {
  const safeData = Array.isArray(data) ? data : [];

  return (
    <div className="w-full" style={{ height }}>
      {loading ? (
        <div className="h-full flex items-center justify-center text-gray-500">Loading chart…</div>
      ) : (
        <ResponsiveContainer width="100%" height="100%">
          {type === 'line' ? (
            <LineChart data={safeData} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Line type="monotone" dataKey="value" stroke="#2563eb" strokeWidth={2} dot={false} />
            </LineChart>
          ) : type === 'bar' ? (
            <BarChart data={safeData} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value" fill="#22c55e" />
            </BarChart>
          ) : (
            <AreaChart data={safeData} margin={{ top: 5, right: 20, left: 0, bottom: 5 }}>
              <defs>
                <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.4} />
                  <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Area type="monotone" dataKey="value" stroke="#8b5cf6" fillOpacity={1} fill="url(#colorValue)" />
            </AreaChart>
          )}
        </ResponsiveContainer>
      )}
    </div>
  );
};

export default Chart;
