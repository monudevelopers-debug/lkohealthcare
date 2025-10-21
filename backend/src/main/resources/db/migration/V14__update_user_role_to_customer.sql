-- V14: Update existing USER role to CUSTOMER for customer portal users
-- This migration ensures backward compatibility with existing users

-- Update all users with role 'USER' to 'CUSTOMER' (excluding admins and providers)
UPDATE users 
SET role = 'CUSTOMER' 
WHERE role = 'USER' 
  AND email NOT LIKE '%admin%' 
  AND email NOT LIKE '%provider%'
  AND id NOT IN (SELECT DISTINCT user_id FROM providers WHERE user_id IS NOT NULL);

-- Add comment for documentation
COMMENT ON COLUMN users.role IS 'User role: CUSTOMER for regular users, PROVIDER for healthcare providers, ADMIN for administrators';

