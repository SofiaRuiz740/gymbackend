ALTER TABLE reporting.report_product_view
    ADD COLUMN IF NOT EXISTS version BIGINT;

ALTER TABLE reporting.movement_audit
    ADD COLUMN IF NOT EXISTS version BIGINT;

