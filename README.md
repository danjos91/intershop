# Intershop E-commerce Application with Payment Service

This project consists of two applications:

1. **Intershop** - A reactive e-commerce application with H2 database, Redis caching, and testcontainers
2. **Payment Service** - A RESTful payment service that handles account balance and payment processing

## Architecture

The system follows a microservices architecture where:
- Intershop handles the e-commerce functionality (items, cart, orders)
- Payment Service manages account balance and payment processing
- Intershop communicates with Payment Service via HTTP to check balance and process payments

## Prerequisites

- Java 21
- Maven
- Docker

## Running the Applications

### 1. Start Redis (Required for Intershop)

```bash
docker run -d --name redis -p 6379:6379 redis:7-alpine
```

### 2. Start Payment Service

```bash
cd payment-service
mvn spring-boot:run
```

The Payment Service will start on port 8081.

### 3. Start Intershop Application

```bash
cd intershop
mvn spring-boot:run
```

The Intershop application will start on port 8080.

### Intershop Application (Port 8080)

- Main page: http://localhost:8080/main/items
- Cart: http://localhost:8080/cart/items
- Orders: http://localhost:8080/orders

## Payment Flow

1. User adds items to cart
2. Cart page checks account balance via Payment Service
3. If balance is sufficient, checkout button is enabled
4. When checkout is clicked:
   - Payment is processed via Payment Service
   - If payment succeeds, order is created
   - If payment fails, user is redirected to cart with error message

## Configuration

### Payment Service Configuration
- Initial balance: 1000.00 RUB (configurable in `application.properties`)
- Currency: RUB
- Port: 8081

### Intershop Configuration
- Payment service URL: http://localhost:8081
- Port: 8080
- Redis: localhost:6379
