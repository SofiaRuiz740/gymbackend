# Flujos de eventos

## Alta de categoría

1. `ADMIN` crea categoría en `category-service`.
2. Se persiste en `gym_category_db`.
3. Se publica `CategoryEvent` en `category.events`.
4. `product-service` actualiza `category_projection`.
5. `report-service` actualiza nombres de categoría en sus vistas.

## Alta de producto

1. `ADMIN` crea producto en `product-service`.
2. `product-service` valida que la categoría exista y esté activa en su proyección local.
3. Se persiste el producto en `gym_product_db`.
4. Se publica `ProductEvent` en `product.events`.
5. `inventory-service` crea o actualiza `product_snapshot` y garantiza `stock = 0`.
6. `report-service` crea la fila inicial en su vista materializada.

## Entrada de inventario

1. `USER` o `ADMIN` registra entrada en `inventory-service`.
2. `inventory-service` valida producto activo.
3. Se guarda movimiento tipo `ENTRY`.
4. Se incrementa `stock_item.available_stock`.
5. Se publica `InventoryMovementEvent` en `inventory.movements`.
6. `report-service` recalcula acumulados y stock actual.

## Salida de inventario

1. `USER` o `ADMIN` registra salida en `inventory-service`.
2. `inventory-service` verifica stock disponible.
3. Se guarda movimiento tipo `EXIT`.
4. Se decrementa `stock_item.available_stock`.
5. Se publica `InventoryMovementEvent`.
6. `report-service` recalcula acumulados.

## Tópicos Kafka

- `category.events`
- `product.events`
- `inventory.movements`

