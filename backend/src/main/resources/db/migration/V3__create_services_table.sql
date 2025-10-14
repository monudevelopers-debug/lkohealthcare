-- Create services table for individual healthcare services
CREATE TABLE services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id UUID NOT NULL REFERENCES service_categories(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    duration INTEGER NOT NULL, -- Duration in hours
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_service_category_id ON services(category_id);
CREATE INDEX idx_service_name ON services(name);
CREATE INDEX idx_service_active ON services(is_active);
CREATE INDEX idx_service_price ON services(price);

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_services_updated_at 
    BEFORE UPDATE ON services 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Insert initial services
INSERT INTO services (category_id, name, description, price, duration) VALUES
-- Nursing Care Services
((SELECT id FROM service_categories WHERE name = 'Nursing Care'), 'Basic Nursing Care', 'General nursing care for 8 hours', 500.00, 8),
((SELECT id FROM service_categories WHERE name = 'Nursing Care'), 'Advanced Nursing Care', 'Specialized nursing care for 12 hours', 800.00, 12),
((SELECT id FROM service_categories WHERE name = 'Nursing Care'), '24-Hour Nursing Care', 'Round-the-clock nursing care', 1500.00, 24),

-- Elderly Care Services
((SELECT id FROM service_categories WHERE name = 'Elderly Care'), 'Elderly Companion Care', 'Companion care for elderly patients', 400.00, 8),
((SELECT id FROM service_categories WHERE name = 'Elderly Care'), 'Elderly Medical Care', 'Medical care for elderly patients', 600.00, 8),
((SELECT id FROM service_categories WHERE name = 'Elderly Care'), 'Elderly Personal Care', 'Personal care assistance for elderly', 350.00, 6),

-- Child Care Services
((SELECT id FROM service_categories WHERE name = 'Child Care'), 'Pediatric Nursing', 'Specialized nursing for children', 450.00, 8),
((SELECT id FROM service_categories WHERE name = 'Child Care'), 'Child Care Assistant', 'General childcare assistance', 300.00, 6),
((SELECT id FROM service_categories WHERE name = 'Child Care'), 'Special Needs Child Care', 'Care for children with special needs', 550.00, 8),

-- Physiotherapy Services
((SELECT id FROM service_categories WHERE name = 'Physiotherapy'), 'Home Physiotherapy', 'Physical therapy at home', 400.00, 1),
((SELECT id FROM service_categories WHERE name = 'Physiotherapy'), 'Post-Surgery Rehabilitation', 'Rehabilitation after surgery', 500.00, 1.5),
((SELECT id FROM service_categories WHERE name = 'Physiotherapy'), 'Sports Injury Therapy', 'Therapy for sports-related injuries', 450.00, 1),

-- Ambulance Services
((SELECT id FROM service_categories WHERE name = 'Ambulance Services'), 'Basic Ambulance', 'Basic medical transportation', 200.00, 1),
((SELECT id FROM service_categories WHERE name = 'Ambulance Services'), 'Advanced Life Support', 'ALS ambulance with medical equipment', 500.00, 1),
((SELECT id FROM service_categories WHERE name = 'Ambulance Services'), 'Emergency Ambulance', 'Emergency medical transportation', 300.00, 1);
