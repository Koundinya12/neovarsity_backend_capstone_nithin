# ProductService

A Spring Boot microservice for managing products and categories in an e-commerce system. It exposes REST APIs for CRUD operations, includes JWT-based authentication support, and uses layered architecture with DTOs, mappers, services, repositories, and controller advice for error handling. The project is built with Maven and includes unit/integration tests.

## Features
- Product and Category CRUD APIs
- DTO-based request/response mapping
- JWT authentication filter and security configuration
- Global exception handling with meaningful error responses
- Service and repository layers with tests
- Maven wrapper for consistent builds

## Tech Stack
- Java 17 (or configured in your environment)
- Spring Boot
- Spring Web, Spring Security
- Spring Data JPA (assumed by repositories)
- Maven (via mvnw/mvnw.cmd)
- JUnit/Mockito

## Project Structure
```
src/
  main/
    java/com/ecom/productservice/
      controllers/           # REST controllers
      services/              # Service interfaces and implementations
      repositories/          # Spring Data repositories
      dtos/                  # Request/Response DTOs
      mappers/               # Entity<->DTO mappers
      models/                # JPA entities/base class
      security/              # JWT and security configuration
      controlleradvice/      # Global exception handler
      configuration/         # App configuration beans
      ProductServiceApplication.java
    resources/
      application.properties
  test/
    java/com/ecom/productservice/
      controllers/ services/ repositories/ tests
    resources/
      testNg.xml
```

## Prerequisites
- JDK 17+
- Git (optional)
- No need to install Maven locally: project includes Maven Wrapper (mvnw/mvnw.cmd)

## Setup
1. Configure application properties in `src/main/resources/application.properties` (datasource, JWT secrets, etc.). Example placeholders:
   - spring.datasource.url=jdbc:postgresql://localhost:5432/productdb
   - spring.datasource.username=...
   - spring.datasource.password=...
   - jwt.secret=... (see `security/JwtProperties.java`)
2. Ensure your database is running and reachable (if using a real DB). For local dev, H2 can be used by adjusting properties.

## Build
- Unix/macOS:
  - `./mvnw clean package`
- Windows:
  - `mvnw.cmd clean package`

Artifacts will be in `target/`.

## Run
- Using Maven (dev):
  - `./mvnw spring-boot:run`
- Using built jar:
  - `java -jar target/productservice-*.jar`

The service will start on the port configured in `application.properties` (default 8080 if not overridden).

## Testing
- Run all tests:
  - `./mvnw test`

## Security
JWT-based authentication is configured via `security/SecurityConfig.java` and `JwtAuthenticationFilter.java`. Endpoints may require an Authorization header:
- `Authorization: Bearer <token>`

Token generation/validation logic resides in `security/JwtService.java`. Ensure `JwtProperties` values (e.g., secret, expiration) are set.

## API Overview
Note: Exact paths can be reviewed in controller classes under `controllers/`. Typical endpoints include:

- Categories (`CategoryController`):
  - POST /api/categories
  - GET /api/categories
  - GET /api/categories/{id}
  - PUT /api/categories/{id}
  - DELETE /api/categories/{id}

- Products (`ProductController`):
  - POST /api/products
  - GET /api/products
  - GET /api/products/{id}
  - PUT /api/products/{id}
  - DELETE /api/products/{id}

Request/response payloads are represented by `dtos/` package classes (e.g., `ProductRequestDto`, `ProductResponseDto`, `CategoryRequestDto`). Mapping logic is handled in `mappers/ProductMapper.java`.

## Error Handling
Global exception handling is centralized in `controlleradvice/GlobalExceptionHandler.java` and covers domain exceptions such as:
- `InvalidCategoryException`
- `NoProductsFoundException`
- `ProductNotFoundException`
- `ProductNotInCartException`

These translate to structured error responses with appropriate HTTP status codes.

## Configuration
- `src/main/resources/application.properties` for environment config.
- `configuration/ApplicationConfiguration.java` for additional beans.

Common properties to set:
- Server: `server.port`
- Datasource: `spring.datasource.*` and `spring.jpa.*`
- JWT: see `security/JwtProperties.java`

## Development Tips
- Keep DTOs stable for API compatibility; map in mappers rather than inside controllers.
- Favor service interfaces (`ProductService`, `CategoryService`) with separate implementations for testability.
- Add new domain exceptions and handle them in the global handler for consistent API errors.
- Add tests in the corresponding `test/java/...` package to maintain coverage.

## Running With Profiles
Specify a Spring profile to load profile-specific properties (if present):
- `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`
- `java -jar target/productservice-*.jar --spring.profiles.active=prod`

## Linting/Formatting
Use your IDE’s formatter and Spotless/Checkstyle if added. Not configured by default in this repository.

## Docker (optional)
If you containerize later, a minimal Dockerfile example:
```
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/productservice-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

## License
This project is proprietary or otherwise license to be determined. Update this section with your chosen license.
