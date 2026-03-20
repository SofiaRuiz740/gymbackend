CREATE SCHEMA IF NOT EXISTS inventory;

CREATE TABLE IF NOT EXISTS inventory.product_snapshot (
    id UUID PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    category_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory.stock_items (
    product_id UUID PRIMARY KEY,
    sku VARCHAR(50) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    category_name VARCHAR(120) NOT NULL,
    available_stock INTEGER NOT NULL,
    active BOOLEAN NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory.inventory_movements (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    sku VARCHAR(50) NOT NULL,
    product_name VARCHAR(120) NOT NULL,
    category_name VARCHAR(120) NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    resulting_stock INTEGER NOT NULL,
    reference VARCHAR(100),
    notes VARCHAR(255),
    registered_by VARCHAR(100) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_inventory_movements_occurred_at
    ON inventory.inventory_movements (occurred_at DESC);

