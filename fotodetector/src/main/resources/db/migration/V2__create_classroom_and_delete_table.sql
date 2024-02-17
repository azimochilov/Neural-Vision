CREATE TABLE images (
                        id SERIAL PRIMARY KEY,
                        file_name VARCHAR(255) NOT NULL,
                        file_type VARCHAR(255) NOT NULL,
                        data BYTEA NOT NULL
);
