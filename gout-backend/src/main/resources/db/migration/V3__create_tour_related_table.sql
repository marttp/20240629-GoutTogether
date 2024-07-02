CREATE TABLE IF NOT EXISTS "tour_company" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS "tour_company_wallet" (
    id SERIAL PRIMARY KEY,
    tour_company_id INTEGER UNIQUE NOT NULL REFERENCES "tour_company"(id),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    balance NUMERIC(13,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS "tour_company_login" (
    id SERIAL PRIMARY KEY,
    tour_company_id INTEGER UNIQUE NOT NULL REFERENCES "tour_company"(id),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS "tour" (
    id SERIAL PRIMARY KEY,
    tour_company_id INTEGER NOT NULL REFERENCES "tour_company"(id),
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    location VARCHAR(1000) NOT NULL,
    number_of_people INTEGER NOT NULL,
    activity_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS "tour_count" (
    id SERIAL PRIMARY KEY,
    tour_id INTEGER UNIQUE NOT NULL REFERENCES "tour"(id),
    amount INTEGER NOT NULL
);
