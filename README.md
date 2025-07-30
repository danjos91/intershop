# Intershop - E-Commerce Platform

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-blue?style=for-the-badge)

## 📚 Technology Stack

### Backend
- **Java 21** - Core programming language
- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Database access
- **H2 Database** - Embedded database
- **Lombok** - Code reduction boilerplate
- **Maven** - Dependency and build management

### Frontend
- **Thymeleaf** - Server-side templating

## 🛍️ Core Features

### User Functionality
- ✅ Product catalog browsing and search
- ✅ Shopping cart management
- ✅ Order placement and history
- ✅ User account system

### Admin Functionality
- ⚙️ Product management (CRUD)
- 📊 Order management
- 👥 User management

## 🚀 Getting Started

### Prerequisites
- Java 21 JDK
- Maven
- Docker (for containerized deployment)
- Git (for version control)

### Installation

1. Clone the repository:
```bash
  git clone https://github.com/your-repo/intershop.git
  cd intershop
```

2. **Option A: Run with Docker (Recommended)**
```bash
# Build the Docker image
  docker buildx build --platform linux/amd64 -t intershop .

# Run the container
  docker run -p 8080:8080 intershop
```

3. **Option B: Run Locally**
```bash
# Build the executable JAR
  mvn clean package

# Run the application
  mvn spring-boot:run
```

## 🌐 Access the Application

Once running, access the application at:
- **Local Development**: http://localhost:8080
- **Docker Container**: http://localhost:8080

## 📁 Project Structure

```
src/
├── main/
│   ├── java/io/github/danjos/intershop/
│   │   ├── controller/     # REST controllers
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   ├── model/          # Entity classes
│   │   ├── dto/            # Data transfer objects
│   │   └── exception/      # Custom exceptions
│   ├── resources/
│   │   ├── templates/      # Thymeleaf templates
│   │   ├── static/         # Static assets
│   │   └── application.yaml # Configuration
│   └── test/               # Test files
```

## 🧪 Testing

Run the test suite:
```bash
  mvn test
```

## 📝 API Documentation

The application provides RESTful endpoints for:
- Product management (`/api/items`)
- Shopping cart operations (`/api/cart`)
- Order processing (`/api/orders`)
- User management (`/api/users`)

## 🗄️ Database Access

### H2 Console Access

The application uses H2 as an embedded database. You can access the H2 console to view and manage the database directly:

1. **Start the application** (using local development)
2. **Open your web browser** and navigate to: `http://localhost:8080/h2-console`


## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
