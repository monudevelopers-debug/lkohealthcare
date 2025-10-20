-- Dummy data for testing the healthcare application
-- This script will be executed after the application starts
-- Password for all users: password123 (hashed with BCrypt)

-- Insert sample service categories
INSERT INTO service_categories (id, name, description, is_active, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'NURSING', 'Professional nursing and home care services', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'ELDERLY', 'Elderly care and senior support services', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'PHYSIOTHERAPY', 'Physical therapy and rehabilitation services', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440004', 'CHILD_CARE', 'Pediatric care and child health services', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440005', 'AMBULANCE', 'Emergency ambulance and medical transport services', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440006', 'General Medicine', 'General medical consultations and treatments', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440007', 'Cardiology', 'Heart and cardiovascular system treatments', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440008', 'Dermatology', 'Skin, hair, and nail treatments', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample users (Admin, Providers, Regular Users)
INSERT INTO users (id, email, password, name, phone, role, status, email_verified, created_at, updated_at) VALUES
-- Admin user
('550e8400-e29b-41d4-a716-446655440101', 'admin@lucknowhealthcare.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Admin User', '+91-9876543210', 'ADMIN', 'ACTIVE', true, NOW(), NOW()),

-- Provider users
('550e8400-e29b-41d4-a716-446655440102', 'dr.sharma@lucknowhealthcare.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Dr. Rajesh Sharma', '+91-9876543211', 'PROVIDER', 'ACTIVE', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440103', 'dr.patel@lucknowhealthcare.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Dr. Priya Patel', '+91-9876543212', 'PROVIDER', 'ACTIVE', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440104', 'dr.singh@lucknowhealthcare.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Dr. Amit Singh', '+91-9876543213', 'PROVIDER', 'ACTIVE', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440105', 'dr.gupta@lucknowhealthcare.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Dr. Sunita Gupta', '+91-9876543214', 'PROVIDER', 'ACTIVE', true, NOW(), NOW()),

-- Regular users
('550e8400-e29b-41d4-a716-446655440106', 'user1@example.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Rahul Kumar', '+91-9876543215', 'CUSTOMER', 'ACTIVE', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440107', 'user2@example.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Priya Sharma', '+91-9876543216', 'CUSTOMER', 'ACTIVE', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440108', 'user3@example.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Amit Verma', '+91-9876543217', 'CUSTOMER', 'ACTIVE', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440109', 'user4@example.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Sneha Agarwal', '+91-9876543218', 'CUSTOMER', 'ACTIVE', true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440110', 'user5@example.com', '$2a$10$WiO.0eFZgQw4OXuoLKAaheF/EbVED8ULOAmegrjqhoiYgHbFkZ3UO', 'Vikram Yadav', '+91-9876543219', 'CUSTOMER', 'ACTIVE', true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample providers
INSERT INTO providers (id, user_id, name, email, phone, specialization, experience, qualification, rating, total_reviews, availability_status, is_verified, consultation_fee, bio, address, city, state, pincode, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440102', 'Nurse Rajesh Sharma', 'nurse.sharma@lucknowhealthcare.com', '+91-9876543211', 'Nursing', 15, 'BSc Nursing, ICU Specialist', 4.8, 150, 'AVAILABLE', true, 800.00, 'Experienced nurse with expertise in home care and patient support.', '123 Medical Center, Hazratganj', 'Lucknow', 'Uttar Pradesh', '226001', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440202', '550e8400-e29b-41d4-a716-446655440103', 'Dr. Priya Patel', 'dr.patel@lucknowhealthcare.com', '+91-9876543212', 'Physiotherapy', 12, 'BPT, MPT', 4.6, 120, 'AVAILABLE', true, 600.00, 'Specialized in physiotherapy and rehabilitation services.', '456 Physio Clinic, Gomti Nagar', 'Lucknow', 'Uttar Pradesh', '226010', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440203', '550e8400-e29b-41d4-a716-446655440104', 'Dr. Amit Singh', 'dr.singh@lucknowhealthcare.com', '+91-9876543213', 'Elderly Care', 18, 'Geriatric Specialist', 4.7, 200, 'AVAILABLE', true, 1000.00, 'Expert in elderly care and senior health management.', '789 Senior Care Center, Aliganj', 'Lucknow', 'Uttar Pradesh', '226024', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440204', '550e8400-e29b-41d4-a716-446655440105', 'Dr. Sunita Gupta', 'dr.gupta@lucknowhealthcare.com', '+91-9876543214', 'Pediatrics', 10, 'MD Pediatrics', 4.9, 180, 'AVAILABLE', true, 500.00, 'Caring pediatrician with expertise in child health and development.', '321 Children''s Hospital, Indira Nagar', 'Lucknow', 'Uttar Pradesh', '226016', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample services
INSERT INTO services (id, category_id, provider_id, name, description, price, duration_minutes, is_active, created_at, updated_at) VALUES
-- Nursing Services
('550e8400-e29b-41d4-a716-446655440301', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440201', 'Home Nursing Care', 'Professional nursing care at home', 800.00, 60, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440302', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440201', 'Post-Surgery Care', 'Nursing care after surgical procedures', 1000.00, 60, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440303', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440201', 'ICU Care at Home', 'Intensive care unit level nursing at home', 1500.00, 120, true, NOW(), NOW()),

-- Elderly Care Services
('550e8400-e29b-41d4-a716-446655440304', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440203', 'Senior Health Checkup', 'Comprehensive health checkup for elderly', 1000.00, 45, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440305', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440203', 'Elderly Home Care', 'Daily care and support for elderly at home', 1200.00, 60, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440306', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440203', 'Dementia Care', 'Specialized care for dementia patients', 1500.00, 60, true, NOW(), NOW()),

-- Physiotherapy Services
('550e8400-e29b-41d4-a716-446655440307', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440202', 'Physical Therapy', 'General physical therapy and rehabilitation', 600.00, 45, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440308', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440202', 'Sports Injury Rehab', 'Rehabilitation for sports injuries', 800.00, 60, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440309', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440202', 'Post-Surgery Rehab', 'Physiotherapy after surgical procedures', 900.00, 60, true, NOW(), NOW()),

-- Child Care Services
('550e8400-e29b-41d4-a716-446655440310', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440204', 'Child Health Checkup', 'Comprehensive health checkup for children', 500.00, 30, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440311', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440204', 'Vaccination', 'Child vaccination and immunization', 300.00, 15, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440312', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440204', 'Growth Monitoring', 'Child growth and development monitoring', 400.00, 20, true, NOW(), NOW()),

-- Ambulance Services
('550e8400-e29b-41d4-a716-446655440313', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440201', 'Emergency Ambulance', 'Emergency medical transport service', 2000.00, 30, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440314', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440201', 'Non-Emergency Transport', 'Non-emergency patient transport', 1000.00, 30, true, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440315', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440201', 'ICU Ambulance', 'Advanced life support ambulance with ICU facilities', 3000.00, 30, true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample bookings
INSERT INTO bookings (id, user_id, provider_id, service_id, booking_date, booking_time, status, notes, special_instructions, total_amount, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440401', '550e8400-e29b-41d4-a716-446655440106', '550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440301', '2024-10-15', '10:00:00', 'CONFIRMED', 'Home nursing care needed', 'Please bring medical reports', 800.00, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440402', '550e8400-e29b-41d4-a716-446655440107', '550e8400-e29b-41d4-a716-446655440203', '550e8400-e29b-41d4-a716-446655440304', '2024-10-16', '14:00:00', 'CONFIRMED', 'Senior health checkup', 'Avoid eating before checkup', 1000.00, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440403', '550e8400-e29b-41d4-a716-446655440108', '550e8400-e29b-41d4-a716-446655440202', '550e8400-e29b-41d4-a716-446655440307', '2024-10-17', '11:00:00', 'PENDING', 'Physical therapy needed', 'Bring previous medical history', 600.00, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440404', '550e8400-e29b-41d4-a716-446655440109', '550e8400-e29b-41d4-a716-446655440204', '550e8400-e29b-41d4-a716-446655440310', '2024-10-18', '09:00:00', 'CONFIRMED', 'Child health checkup', 'Bring vaccination card', 500.00, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440405', '550e8400-e29b-41d4-a716-446655440110', '550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440313', '2024-10-19', '15:30:00', 'COMPLETED', 'Emergency ambulance service', 'Quick response needed', 2000.00, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample payments
INSERT INTO payments (id, booking_id, user_id, amount, payment_method, status, transaction_id, payment_date, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440501', '550e8400-e29b-41d4-a716-446655440401', '550e8400-e29b-41d4-a716-446655440106', 800.00, 'CREDIT_CARD', 'COMPLETED', 'TXN_001', NOW(), NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440502', '550e8400-e29b-41d4-a716-446655440402', '550e8400-e29b-41d4-a716-446655440107', 1000.00, 'UPI', 'COMPLETED', 'TXN_002', NOW(), NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440503', '550e8400-e29b-41d4-a716-446655440403', '550e8400-e29b-41d4-a716-446655440108', 600.00, 'DEBIT_CARD', 'PENDING', 'TXN_003', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440504', '550e8400-e29b-41d4-a716-446655440404', '550e8400-e29b-41d4-a716-446655440109', 500.00, 'NET_BANKING', 'COMPLETED', 'TXN_004', NOW(), NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440505', '550e8400-e29b-41d4-a716-446655440405', '550e8400-e29b-41d4-a716-446655440110', 2000.00, 'UPI', 'COMPLETED', 'TXN_005', NOW(), NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert sample reviews
INSERT INTO reviews (id, user_id, provider_id, booking_id, rating, comment, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440601', '550e8400-e29b-41d4-a716-446655440106', '550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440401', 5, 'Excellent home nursing care. Very professional and caring.', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440602', '550e8400-e29b-41d4-a716-446655440107', '550e8400-e29b-41d4-a716-446655440203', '550e8400-e29b-41d4-a716-446655440402', 5, 'Outstanding elderly care service. Dr. Singh was very attentive and knowledgeable.', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440603', '550e8400-e29b-41d4-a716-446655440109', '550e8400-e29b-41d4-a716-446655440204', '550e8400-e29b-41d4-a716-446655440404', 5, 'Dr. Gupta is amazing with children. My daughter was comfortable throughout the visit.', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440604', '550e8400-e29b-41d4-a716-446655440110', '550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440405', 5, 'Quick and efficient ambulance service. Arrived on time and staff were very professional.', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Update provider ratings based on reviews
UPDATE providers SET 
    rating = (SELECT AVG(rating) FROM reviews WHERE provider_id = providers.id),
    total_reviews = (SELECT COUNT(*) FROM reviews WHERE provider_id = providers.id)
WHERE id IN ('550e8400-e29b-41d4-a716-446655440201', '550e8400-e29b-41d4-a716-446655440202', '550e8400-e29b-41d4-a716-446655440204');
