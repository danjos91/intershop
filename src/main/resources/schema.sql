CREATE TABLE IF NOT EXISTS items (
                                        id BIG SERIAL PRIMARY KEY,
                                        name VARCHAR(256) NOT NULL,
                                        description TEXT,
                                        image_path VARCHAR(512),
                                        --tags TEXT,
                                        --likes INTEGER DEFAULT 0
 );

CREATE TABLE IF NOT EXISTS orders (
                                        id BIG SERIAL PRIMARY KEY,
                                        user_id BIGINT,
--                                        text TEXT NOT NULL,
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
                                        id BIG SERIAL PRIMARY KEY,
                                        name VARCHAR(256) NOT NULL,
);

INSERT INTO items(title, image_path)
VALUES ('Item1',
        none1',
        );
