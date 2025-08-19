# Multi-Project: Intershop + Payment Service

This is a multi-project Maven setup containing two microservices:

## Project Structure

```
multiproject/
├── pom.xml                 # Parent POM (this file)
├── intershop/             # Main e-commerce service
└── payment-service/       # Payment processing service
```

## Services

### Intershop Service
- **Location**: `intershop/`
- **Description**: Main e-commerce application with cart, items, orders, and user management
- **Port**: 8080 (default)
- **Features**: 
  - Product catalog
  - Shopping cart
  - Order management
  - User authentication
  - Redis caching

### Payment Service
- **Location**: `payment-service/`
- **Description**: Dedicated payment processing service
- **Port**: 8081 (default)
- **Features**:
  - Payment processing
  - Balance management
  - REST API for payment operations

## Quick Start

### Prerequisites
- Java 21
- Maven 3.6+
- Docker (for Redis and database containers)

### Building All Services
```bash
# Build all services from the root directory
mvn clean install
```

### Running Individual Services

#### Intershop Service
```bash
cd intershop
mvn spring-boot:run
```

#### Payment Service
```bash
cd payment-service
mvn spring-boot:run
```

### Running All Services
```bash
# From the root directory, you can run both services
mvn -pl intershop spring-boot:run &
mvn -pl payment-service spring-boot:run &
```

## Development

### Adding New Services
1. Create a new directory for your service
2. Add the service as a module in the parent `pom.xml`
3. Ensure your service follows the same groupId structure

### Common Dependencies
The parent POM manages common dependency versions for:
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- Java 17

## API Endpoints

### Intershop Service
- Main application: http://localhost:8080
- API endpoints: http://localhost:8080/api/*

### Payment Service
- API specification: http://localhost:8081/v3/api-docs
- Swagger UI: http://localhost:8081/swagger-ui.html

## Testing

```bash
# Run tests for all services
mvn test

# Run tests for specific service
mvn -pl intershop test
mvn -pl payment-service test
```

## Docker Support

Both services include Docker support for containerized deployment. See individual service directories for Docker-specific instructions.
