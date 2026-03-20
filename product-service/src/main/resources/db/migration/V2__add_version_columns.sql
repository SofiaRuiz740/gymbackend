ALTER TABLE product.category_projection
    ADD COLUMN IF NOT EXISTS version BIGINT;

ALTER TABLE product.products
    ADD COLUMN IF NOT EXISTS version BIGINT;

