import type { ReactNode, HTMLAttributes } from 'react';

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode;
  className?: string;
  padding?: 'none' | 'sm' | 'md' | 'lg';
  hover?: boolean;
  gradient?: boolean;
}

export const Card = ({ 
  children, 
  className = '', 
  padding = 'md',
  hover = false,
  gradient = false,
  ...rest
}: CardProps) => {
  const paddingClasses = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
  };

  const baseStyles = 'bg-white rounded-2xl shadow-lg border border-gray-100 transition-all duration-300';
  const hoverStyles = hover ? 'hover:shadow-2xl hover:-translate-y-2 cursor-pointer' : '';
  const gradientStyles = gradient ? 'bg-gradient-to-br from-white to-gray-50' : '';

  const combinedStyles = `${baseStyles} ${paddingClasses[padding]} ${hoverStyles} ${gradientStyles} ${className}`;

  return (
    <div className={combinedStyles} {...rest}>
      {children}
    </div>
  );
};