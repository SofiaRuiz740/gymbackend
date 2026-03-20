ALTER TABLE inventory.product_snapshot
    ADD COLUMN IF NOT EXISTS version BIGINT;

ALTER TABLE inventory.stock_items
    ADD COLUMN IF NOT EXISTS version BIGINT;

ALTER TABLE inventory.inventory_movements
    ADD COLUMN IF NOT EXISTS version BIGINT;

