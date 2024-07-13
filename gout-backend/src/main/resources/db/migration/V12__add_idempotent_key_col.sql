ALTER TABLE "booking"
ADD COLUMN "idempotent_key" VARCHAR(255) NOT NULL;

ALTER TABLE "transaction"
ADD COLUMN "idempotent_key" VARCHAR(255) NOT NULL;