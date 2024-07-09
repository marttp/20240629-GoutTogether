CREATE TABLE IF NOT EXISTS "refresh_token" (
    id SERIAL PRIMARY KEY,
    token VARCHAR(3000) NOT NULL,
    issued_date TIMESTAMP WITH TIME ZONE NOT NULL,
    usage VARCHAR(20) NOT NULL,
    resource_id INTEGER NOT NULL,
    is_expired BOOLEAN NOT NULL
);