CREATE TABLE class_room (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255),
                            capacity_students BIGINT,
                            non_active BIGINT,
                            active BIGINT
);