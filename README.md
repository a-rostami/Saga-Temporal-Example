# Temporal Order Saga (Minimal)

A tiny but real-ish Order Saga using Temporal + Spring Boot.

## Run

### Option A: Docker Compose (recommended)

```bash
docker compose up -d
```

Temporal UI: http://localhost:8088

### Option B: Temporal dev server

If you already have a local Temporal dev server, just make sure it listens on `localhost:7233`.

### Start the app

```bash
mvn spring-boot:run
```

## Test

```bash
mvn test -Dspring.profiles.active=test
```

## Sample curl

Create order (happy path):

```bash
curl -X POST http://localhost:8080/orders \
  -H 'Content-Type: application/json' \
  -d '{"totalCents":2599,"currency":"GBP","items":[{"sku":"SKU-1","qty":2}],"failStep":"NONE"}'
```

Create order (payment fail → compensate):

```bash
curl -X POST http://localhost:8080/orders \
  -H 'Content-Type: application/json' \
  -d '{"totalCents":2599,"currency":"GBP","items":[{"sku":"SKU-1","qty":2}],"failStep":"PAYMENT"}'
```

Create order (shipping fail → refund + release):

```bash
curl -X POST http://localhost:8080/orders \
  -H 'Content-Type: application/json' \
  -d '{"totalCents":2599,"currency":"GBP","items":[{"sku":"SKU-1","qty":2}],"failStep":"SHIPPING"}'
```

Get order:

```bash
curl http://localhost:8080/orders/{orderId}
```

Get workflow info:

```bash
curl http://localhost:8080/orders/{orderId}/workflow
```
