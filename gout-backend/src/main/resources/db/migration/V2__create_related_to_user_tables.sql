CREATE TABLE IF NOT EXISTS "role" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_role" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES "user"(id),
    role_id INTEGER UNIQUE NOT NULL REFERENCES "role"(id)
);

CREATE TABLE IF NOT EXISTS "user_login" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES "user"(id),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "user_point" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES "user"(id),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    balance INTEGER NOT NULL
);


CREATE TABLE IF NOT EXISTS "user_wallet" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES "user"(id),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    balance NUMERIC(13,2) NOT NULL
);