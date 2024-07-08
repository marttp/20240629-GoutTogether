CREATE TABLE IF NOT EXISTS "refresh_token" (
    id SERIAL PRIMARY KEY,
    token VARCHAR(40) NOT NULL,
    issued_date TIMESTAMP WITH TIME ZONE NOT NULL,
    usage VARCHAR(7) NOT NULL,
    resource_id INTEGER NOT NULL,
    is_expired BOOLEAN NOT NULL
);