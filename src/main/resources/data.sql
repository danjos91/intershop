-- Insert default user
INSERT INTO users (username, password, email, created_at) 
VALUES ('currentUser', 'password123', 'user@example.com', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE username = username;

-- Insert sample items
INSERT INTO items (title, description, price, img_path, stock, created_at) VALUES
('Laptop', 'High-performance laptop with latest specifications', 999.99, 'images/laptop.jpg', 10, CURRENT_TIMESTAMP),
('Smartphone', 'Latest smartphone with advanced features', 699.99, 'images/smartphone.jpg', 15, CURRENT_TIMESTAMP),
('Headphones', 'Wireless noise-canceling headphones', 199.99, 'images/headphones.jpg', 20, CURRENT_TIMESTAMP),
('Tablet', '10-inch tablet with high-resolution display', 399.99, 'images/tablet.jpg', 8, CURRENT_TIMESTAMP),
('Smartwatch', 'Fitness tracking smartwatch', 299.99, 'images/smartwatch.jpg', 12, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE title = title; 