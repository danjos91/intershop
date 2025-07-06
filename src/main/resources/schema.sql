CREATE TABLE IF NOT EXISTS items (
    id BIG SERIAL PRIMARY KEY,
    title VARCHAR(256) NOT NULL,
    --text TEXT NOT NULL,
    image_path VARCHAR(512),
    --tags TEXT,
    --likes INTEGER DEFAULT 0
 );

CREATE TABLE IF NOT EXISTS orders (
                                        id BIG SERIAL PRIMARY KEY,
                                        item_id BIGINT,
--                                        text TEXT NOT NULL,
                                        FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

INSERT INTO itmes(title, image_path)
VALUES ('Item1',
        none1',
        );
