-- This script creates a test user with username: testuser and password: testpass
-- The password is BCrypt encoded (cost factor 10)
-- You can run this in your H2 console or database to create a test user

INSERT INTO users (username, password, email, created_at) 
VALUES ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'test@example.com', CURRENT_TIMESTAMP);

-- Alternative: You can also create users programmatically through your application
-- The password 'testpass' will be automatically encoded by Spring Security
