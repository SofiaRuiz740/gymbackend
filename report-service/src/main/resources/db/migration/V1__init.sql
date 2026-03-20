CREATE SCHEMA IF NOT EXISTS reporting;

CREATE TABLE IF NOT EXISTS reporting.report_product_view (
    product_id UUID PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    category_id UUID,
    category_name VARCHAR(120) NOT NULL,
    current_stock INTEGER NOT NULL,
    total_entries INTEGER NOT NULL,
    total_exits INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS reporting.movement_audit (
    movement_id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    sku VARCHAR(50) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    category_name VARCHAR(120) NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    resulting_stock INTEGER NOT NULL,
    reference VARCHAR(100) NOT NULL,
    notes VARCHAR(255) NOT NULL,
    registered_by VARCHAR(100) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_report_product_view_stock
    ON reporting.report_product_view (current_stock);

CREATE INDEX IF NOT EXISTS idx_movement_audit_occurred_at
    ON reporting.movement_audit (occurred_at DESC);

