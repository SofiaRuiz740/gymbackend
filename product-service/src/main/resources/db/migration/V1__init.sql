CREATE SCHEMA IF NOT EXISTS product;

CREATE TABLE IF NOT EXISTS product.category_projection (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS product.products (
    id UUID PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    category_id UUID NOT NULL,
    category_name VARCHAR(120) NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    brand VARCHAR(80) NOT NULL,
    product_type VARCHAR(80) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

