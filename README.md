# dynamic-rest-api

A Spring Boot application providing dynamic REST API endpoints backed by Couchbase.

## Prerequisites

- Java 21
- Maven
- Docker & Docker Compose

## Getting Started

### 1. Start Couchbase

Use Docker Compose to start a Couchbase container. This setup includes an initialization sidecar that automatically configures the cluster, creates an admin user, and sets up the `dynamic-rest-api` bucket.

```bash
docker-compose up -d
```

Wait a moment for the initialization to complete. You can verify it's ready by checking the logs:

```bash
docker-compose logs -f db-init
```

Once initialized, the Couchbase Web UI is available at:
- **URL**: [http://localhost:8091](http://localhost:8091)
- **Username**: `Administrator`
- **Password**: `password`
- **Bucket**: `dynamic-rest-api`

### 2. Run the Application

```bash
./mvnw spring-boot:run
```

The application will start on port `9091`.

## API Usage Examples

You can use the following HTTP requests (compatible with IntelliJ HTTP Client) to test the application:

```http request
POST http://localhost:9091/product
Content-Type: application/json

{
  "name": "Super Gizmo",
  "details": {
    "price": {
      "value": 99.99,
      "unit": "USD"
    },
    "description": "The best gizmo ever."
  }
}
```

```http request
GET http://localhost:9091/product/697f9e6d-f835-4e94-9865-0cb260bcd27a
Content-Type: application/json
```

```http request
GET http://localhost:9091/product?page=0&size=3&sort=price,desc
Content-Type: application/json
```

## Configuration

### `application.yaml`

Configure Couchbase connection and dynamic endpoints here.

```yaml
spring:
  couchbase:
    connection-string: couchbase://localhost
    username: Administrator
    password: password
  data:
    couchbase:
      bucket-name: dynamic-rest-api
```

### `docker-compose.yaml`

Defines the Couchbase service (`db`) and the initialization service (`db-init`).
Data is persisted to `./data/couchbase-data`.
