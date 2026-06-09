-- Insert Users
INSERT INTO users (name, email, cohort_name, created_at, updated_at) VALUES
('John Doe', 'john@example.com', 'EarlyAdopter', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Jane Smith', 'jane@example.com', 'Standard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Membership Plans
INSERT INTO membership_plans (duration, base_price, created_at, updated_at) VALUES
('MONTHLY', 9.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('QUARTERLY', 24.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('YEARLY', 89.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Membership Tiers
INSERT INTO membership_tiers (name, min_orders, min_order_value_in_month, eligible_cohort, created_at, updated_at) VALUES
('SILVER', 0, 0.00, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('GOLD', 5, 100.00, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PLATINUM', 10, 250.00, 'EarlyAdopter', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Membership Benefits
INSERT INTO membership_benefits (name, description, created_at, updated_at) VALUES
('Free Delivery', 'Free delivery on eligible orders', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Extra Discount', 'Extra discount on selected items', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Priority Support', 'Priority support for premium members', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Early Access', 'Early access to sales', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Tier Benefits (Mapping)
-- SILVER Tier Benefits
INSERT INTO tier_benefits (tier_id, benefit_id, config_value, created_at, updated_at) VALUES
((SELECT id FROM membership_tiers WHERE name = 'SILVER'), (SELECT id FROM membership_benefits WHERE name = 'Free Delivery'), 'Standard', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- GOLD Tier Benefits
INSERT INTO tier_benefits (tier_id, benefit_id, config_value, created_at, updated_at) VALUES
((SELECT id FROM membership_tiers WHERE name = 'GOLD'), (SELECT id FROM membership_benefits WHERE name = 'Free Delivery'), 'Express', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM membership_tiers WHERE name = 'GOLD'), (SELECT id FROM membership_benefits WHERE name = 'Extra Discount'), '5', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PLATINUM Tier Benefits
INSERT INTO tier_benefits (tier_id, benefit_id, config_value, created_at, updated_at) VALUES
((SELECT id FROM membership_tiers WHERE name = 'PLATINUM'), (SELECT id FROM membership_benefits WHERE name = 'Free Delivery'), 'Same Day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM membership_tiers WHERE name = 'PLATINUM'), (SELECT id FROM membership_benefits WHERE name = 'Extra Discount'), '10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM membership_tiers WHERE name = 'PLATINUM'), (SELECT id FROM membership_benefits WHERE name = 'Priority Support'), '24/7', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
((SELECT id FROM membership_tiers WHERE name = 'PLATINUM'), (SELECT id FROM membership_benefits WHERE name = 'Early Access'), '24 Hours', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

