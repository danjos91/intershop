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

## 🔧 API Endpoints

### Account Management
- `GET /api/v1/balance` - Get current account balance
- `POST /api/v1/payment` - Process a payment transaction

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
