# Payment Service - RESTful Payment Processing API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Spring WebFlux](https://img.shields.io/badge/Spring_WebFlux-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![OpenAPI](https://img.shields.io/badge/OpenAPI-6BA539?style=for-the-badge&logo=openapi&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

## 📚 Technology Stack

### Backend (Reactive)
- **Java 21** - Core programming language
- **Spring Boot 3.x** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Project Reactor** - Reactive programming foundation
- **OpenAPI 3.0** - API specification and documentation
- **Lombok** - Code reduction boilerplate
- **Maven** - Dependency and build management

### API Design
- **RESTful API** - HTTP-based service endpoints
- **OpenAPI Specification** - Auto-generated API documentation
- **Validation** - Request/response validation using Jakarta Validation
- **Reactive Endpoints** - Non-blocking I/O for high performance

## 🚀 Reactive Architecture

This service is built using **reactive programming principles**:

- **Non-blocking I/O** - All HTTP operations are non-blocking
- **Event-driven** - Uses reactive streams for data processing
- **Scalable** - Designed to handle high concurrency with minimal resource usage
- **Responsive** - Provides better performance under load

## 💳 Core Features

### Payment Operations
- ✅ **Account Balance Management** - Check current account balance
- ✅ **Payment Processing** - Process payment transactions
- ✅ **Transaction Validation** - Validate payment amounts and account status
- ✅ **Error Handling** - Comprehensive error responses and status codes

### API Capabilities
- 🔒 **RESTful Endpoints** - Standard HTTP methods (GET, POST)
- 📊 **OpenAPI Documentation** - Auto-generated API specification
- ✅ **Input Validation** - Request payload validation
- 🚦 **Health Monitoring** - Built-in health check endpoints

## 🚀 Getting Started

### Prerequisites
- Java 21 JDK
- Maven 3.6+
- Docker (for containerized deployment)
- Git (for version control)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/danjos91/multiproject.git
cd multiproject/payment-service
```

2. **Option A: Run with Docker (Recommended)**
```bash
# Build the Docker image
docker buildx build --platform linux/amd64 -t payment-service .

# Run the container
docker run -p 8081:8081 payment-service
```

3. **Option B: Run Locally**
```bash
# Build the executable JAR
mvn clean package

# Run the application
mvn spring-boot:run
```

4. **Option C: Run from Multi-Project Root**
```bash
# From the multiproject root directory
mvn -pl payment-service spring-boot:run
```

## 🌐 Access the Service

Once running, access the service at:
- **Local Development**: http://localhost:8081
- **Docker Container**: http://localhost:8081

### API Documentation
- **OpenAPI Specification**: http://localhost:8081/v3/api-docs
- **Swagger UI**: http://localhost:8081/swagger-ui.html

## 🔧 API Endpoints

### Account Management
- `GET /api/v1/balance` - Get current account balance
- `POST /api/v1/payment` - Process a payment transaction

### Health & Monitoring
- `GET /actuator/health` - Service health status

## 📊 API Models

### Payment Request
```json
{
  "amount": 150.00,
  "currency": "RUB",
  "description": "Product purchase"
}
```

### Payment Response
```json
{
  "success": true,
  "transactionId": "txn_12345",
  "balance": 850.00,
  "message": "Payment processed successfully"
}
```

### Balance Response
```json
{
  "balance": 1000.00,
  "currency": "RUB",
  "lastUpdated": "2024-01-15T10:30:00Z"
}
```

## 🔧 Development

### Key Reactive Components

- **Controllers**: Use `@RestController` with reactive return types (`Mono<T>`, `Flux<T>`)
- **Services**: Implement reactive business logic using Project Reactor
- **Validation**: Request/response validation using Jakarta Validation
- **OpenAPI**: Auto-generated API documentation from annotations

### Project Structure
```
payment-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── io/github/danjos/payment/
│   │   │       ├── controller/     # REST endpoints
│   │   │       ├── domain/         # DTOs and models
│   │   │       ├── service/        # Business logic
│   │   │       └── config/         # Configuration
│   │   └── resources/
│   │       └── api-spec.yaml       # OpenAPI specification
│   └── test/                       # Test classes
├── Dockerfile                      # Container configuration
└── pom.xml                        # Maven configuration
```

## 🧪 Testing

Run the test suite:
```bash
# Run all tests
mvn test

# Run tests from multi-project root
mvn -pl payment-service test
```

## 🐳 Docker Support

### Building the Image
```bash
docker build -t payment-service .
```

### Running with Docker Compose
```bash
# From the multiproject root directory
docker-compose up payment-service
```

### Environment Variables
- `SERVER_PORT` - Service port (default: 8081)
- `SPRING_PROFILES_ACTIVE` - Spring profile (default: default)

## 🔗 Integration

### Intershop Integration
This service integrates with the Intershop e-commerce platform:
- **Payment Processing** - Handles checkout payments
- **Balance Checking** - Verifies account balance before purchase
- **Transaction Management** - Records and validates payment transactions

### Communication
- **HTTP REST API** - Standard REST endpoints for payment operations
- **JSON Payloads** - Request/response using JSON format
- **Error Handling** - Comprehensive error responses for integration

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](../LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📞 Support

For questions or issues:
- Create an issue in the repository
- Check the API documentation at `/swagger-ui.html`
- Review the OpenAPI specification at `/v3/api-docs`
