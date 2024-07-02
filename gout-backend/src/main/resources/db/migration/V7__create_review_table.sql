CREATE TABLE IF NOT EXISTS "review" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(id),
    tour_id INTEGER NOT NULL REFERENCES "tour"(id),
    rate INTEGER NOT NULL,
    description VARCHAR(1000)
);
