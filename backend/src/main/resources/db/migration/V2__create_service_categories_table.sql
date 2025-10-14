-- Create service_categories table for organizing healthcare services
CREATE TABLE service_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX idx_service_category_name ON service_categories(name);
CREATE INDEX idx_service_category_active ON service_categories(is_active);

-- Create trigger to automatically update updated_at timestamp
CREATE TRIGGER update_service_categories_updated_at 
    BEFORE UPDATE ON service_categories 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Insert initial service categories
INSERT INTO service_categories (name, description) VALUES
('Nursing Care', 'Professional nursing services for home care'),
('Elderly Care', 'Specialized care services for elderly patients'),
('Child Care', 'Pediatric nursing and childcare services'),
('Physiotherapy', 'Physical therapy and rehabilitation services'),
('Ambulance Services', 'Emergency medical transportation services'),
('Medical Equipment', 'Rental and purchase of medical equipment');
