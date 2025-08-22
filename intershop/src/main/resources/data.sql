INSERT INTO users (username, password, email, created_at) 
VALUES ('currentUser', 'password123', 'user@example.com', CURRENT_TIMESTAMP);
VALUES ('admin', 'admin', 'admin@example.com', CURRENT_TIMESTAMP);
VALUES ('user1', 'user1', 'user1@example.com', CURRENT_TIMESTAMP);
VALUES ('user2', 'user2', 'user2@example.com', CURRENT_TIMESTAMP);

INSERT INTO items (title, description, price, img_path, stock, created_at) VALUES
('Laptop', 'High-performance laptop with latest specifications', 999.99, 'images/laptop.jpg', 10, CURRENT_TIMESTAMP),
('Smartphone', 'Latest smartphone with advanced features', 699.99, 'images/smartphone.jpg', 15, CURRENT_TIMESTAMP),
('Headphones', 'Wireless noise-canceling headphones', 199.99, 'images/headphones.jpg', 20, CURRENT_TIMESTAMP),
('Tablet', '10-inch tablet with high-resolution display', 399.99, 'images/tablet.jpg', 8, CURRENT_TIMESTAMP),
('Smartwatch', 'Fitness tracking smartwatch', 299.99, 'images/smartwatch.jpg', 12, CURRENT_TIMESTAMP),
('Laptop2', 'High-performance laptop with latest specifications', 999.99, 'images/laptop.jpg', 10, CURRENT_TIMESTAMP),
('Smartphone2', 'Latest smartphone with advanced features', 699.99, 'images/smartphone.jpg', 15, CURRENT_TIMESTAMP),
('Headphones2', 'Wireless noise-canceling headphones', 199.99, 'images/headphones.jpg', 20, CURRENT_TIMESTAMP),
('Tablet2', '10-inch tablet with high-resolution display', 399.99, 'images/tablet.jpg', 8, CURRENT_TIMESTAMP),
('Smartwatch2', 'Fitness tracking smartwatch', 299.99, 'images/smartwatch.jpg', 12, CURRENT_TIMESTAMP),
('Laptop3', 'High-performance laptop with latest specifications', 999.99, 'images/laptop.jpg', 10, CURRENT_TIMESTAMP),
('Smartphone3', 'Latest smartphone with advanced features', 699.99, 'images/smartphone.jpg', 15, CURRENT_TIMESTAMP),
('Headphones3', 'Wireless noise-canceling headphones', 199.99, 'images/headphones.jpg', 20, CURRENT_TIMESTAMP),
('Tablet3', '10-inch tablet with high-resolution display', 399.99, 'images/tablet.jpg', 8, CURRENT_TIMESTAMP),
('Smartwatch3', 'Fitness tracking smartwatch', 299.99, 'images/smartwatch.jpg', 12, CURRENT_TIMESTAMP); 