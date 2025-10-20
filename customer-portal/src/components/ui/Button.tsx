import type { ButtonHTMLAttributes, ReactNode } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'gradient';
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  children: ReactNode;
  isLoading?: boolean;
  fullWidth?: boolean;
}

export const Button = ({
  variant = 'primary',
  size = 'md',
  children,
  isLoading = false,
  fullWidth = false,
  className = '',
  disabled,
  ...props
}: ButtonProps) => {
  const baseStyles = 'inline-flex items-center justify-center font-semibold rounded-xl transition-all duration-300 focus:outline-none focus:ring-4 focus:ring-offset-2 transform active:scale-95 shadow-lg hover:shadow-xl';
  
  const variantStyles = {
    primary: 'bg-gradient-to-r from-blue-600 to-blue-700 text-white hover:from-blue-700 hover:to-blue-800 focus:ring-blue-300 disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed',
    secondary: 'bg-white text-gray-900 hover:bg-gray-50 focus:ring-gray-200 border-2 border-gray-200 hover:border-gray-300 disabled:bg-gray-100 disabled:cursor-not-allowed shadow-md',
    outline: 'border-2 border-blue-600 bg-transparent text-blue-600 hover:bg-blue-50 focus:ring-blue-200 disabled:border-gray-300 disabled:text-gray-400 disabled:cursor-not-allowed shadow-none',
    ghost: 'text-gray-700 hover:bg-gray-100 focus:ring-gray-200 disabled:text-gray-400 disabled:cursor-not-allowed shadow-none',
    danger: 'bg-gradient-to-r from-red-600 to-red-700 text-white hover:from-red-700 hover:to-red-800 focus:ring-red-300 disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed',
    gradient: 'bg-gradient-to-r from-purple-600 via-pink-600 to-blue-600 text-white hover:from-purple-700 hover:via-pink-700 hover:to-blue-700 focus:ring-purple-300 disabled:from-gray-400 disabled:to-gray-500 disabled:cursor-not-allowed bg-[length:200%_auto] animate-gradient',
  };

  const sizeStyles = {
    xs: 'px-3 py-1.5 text-xs gap-1',
    sm: 'px-4 py-2 text-sm gap-1.5',
    md: 'px-6 py-3 text-base gap-2',
    lg: 'px-8 py-4 text-lg gap-2',
    xl: 'px-10 py-5 text-xl gap-3',
  };

  const widthStyle = fullWidth ? 'w-full' : '';
  const disabledStyle = (disabled || isLoading) ? 'opacity-60 cursor-not-allowed' : '';

  const combinedStyles = `${baseStyles} ${variantStyles[variant]} ${sizeStyles[size]} ${widthStyle} ${disabledStyle} ${className}`;

  return (
    <button
      className={combinedStyles}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading && (
        <svg className="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
        </svg>
      )}
      {children}
    </button>
  );
};