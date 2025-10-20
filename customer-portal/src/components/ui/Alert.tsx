import { XMarkIcon } from '@heroicons/react/24/outline';
import type { ReactNode } from 'react';

interface AlertProps {
  type?: 'success' | 'error' | 'warning' | 'info';
  children: ReactNode;
  onClose?: () => void;
  className?: string;
}

export const Alert = ({ type = 'info', children, onClose, className = '' }: AlertProps) => {
  const styles = {
    success: 'bg-green-50 border-green-200 text-green-800',
    error: 'bg-red-50 border-red-200 text-red-800',
    warning: 'bg-yellow-50 border-yellow-200 text-yellow-800',
    info: 'bg-blue-50 border-blue-200 text-blue-800',
  };

  return (
    <div className={`p-4 border rounded-lg flex items-start justify-between ${styles[type]} ${className}`}>
      <div className="flex-1">{children}</div>
      {onClose && (
        <button onClick={onClose} className="ml-3 flex-shrink-0">
          <XMarkIcon className="h-5 w-5" />
        </button>
      )}
    </div>
  );
};
