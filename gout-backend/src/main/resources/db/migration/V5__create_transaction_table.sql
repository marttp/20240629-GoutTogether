CREATE TABLE IF NOT EXISTS "transaction" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER,
    tour_company_id INTEGER,
    type VARCHAR(10) NOT NULL,
    amount NUMERIC(13,2) NOT NULL,
    transaction_date TIMESTAMP NOT NULL
);