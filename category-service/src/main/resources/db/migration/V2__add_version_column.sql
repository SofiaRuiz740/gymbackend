ALTER TABLE catalog.categories
    ADD COLUMN IF NOT EXISTS version BIGINT;

