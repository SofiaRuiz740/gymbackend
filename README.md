# Gym Inventory Platform

Backend de inventario para gimnasio construido con microservicios reactivos usando Spring Boot WebFlux, PostgreSQL, Kafka, Docker y Kubernetes.

## Arquitectura

El sistema está organizado por bounded contexts:

- `user-service`: autenticación, usuarios y roles.
- `category-service`: catálogo de categorías.
- `product-service`: catálogo de productos.
- `inventory-service`: ledger de movimientos y stock actual.
- `report-service`: proyecciones y reportes.
- `api-gateway`: punto de entrada único.
- `shared-events`: contratos de eventos Kafka.

La documentación detallada está en [docs/architecture.md](docs/architecture.md) y [docs/event-flows.md](docs/event-flows.md).

## Reglas de negocio implementadas

- Todo producto pertenece a una categoría.
- El stock inicial se crea en `0` cuando `inventory-service` consume `ProductEvent`.
- Solo `ADMIN` puede crear categorías y productos.
- `USER` y `ADMIN` pueden registrar entradas y salidas.
- Las salidas no pueden dejar stock negativo.

## Puesta en marcha rápida

### Docker Compose

```bash
docker compose up --build -d
```

Servicios expuestos:

- `api-gateway`: `http://localhost:8080`
- `user-service`: `http://localhost:8081`
- `category-service`: `http://localhost:8082`
- `product-service`: `http://localhost:8083`
- `inventory-service`: `http://localhost:8084`
- `report-service`: `http://localhost:8085`

Credenciales bootstrap:

- usuario: `admin`
- password: `Admin123!`

### Kubernetes

```bash
pwsh ./scripts/build-k8s-images.ps1
pwsh ./scripts/deploy-k8s.ps1
kubectl port-forward svc/api-gateway 8080:8080 -n gym-inventory
```

Para producción, Kafka y PostgreSQL deberían sustituirse por servicios gestionados o StatefulSets endurecidos; los manifiestos incluidos priorizan despliegue rápido y reproducible.

## Endpoints de referencia

### Autenticación

```http
POST /api/v1/auth/login
```

### Usuarios

```http
POST /api/v1/users
GET /api/v1/users
PATCH /api/v1/users/{id}/status
```

### Categorías

```http
POST /api/v1/categories
GET /api/v1/categories
PUT /api/v1/categories/{id}
PATCH /api/v1/categories/{id}/status
```

### Productos

```http
POST /api/v1/products
GET /api/v1/products
PATCH /api/v1/products/{id}/status
```

### Inventario

```http
POST /api/v1/movements/entries
POST /api/v1/movements/exits
GET /api/v1/stock
GET /api/v1/movements
```

### Reportes

```http
GET /api/v1/reports/stock
GET /api/v1/reports/low-stock?threshold=10
GET /api/v1/reports/movements/summary?from=2026-03-01T00:00:00Z&to=2026-03-31T23:59:59Z
```
